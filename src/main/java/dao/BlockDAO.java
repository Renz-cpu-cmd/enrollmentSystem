package dao;

import model.Block;
import model.Schedule;
import model.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * DAO for blocks/sections/schedules backed by the new academic structure tables.
 */
public class BlockDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockDAO.class);

    public List<Block> findAllWithSchedules() {
        try (Connection conn = DatabaseManager.getConnection()) {
            return findAllWithSchedules(conn);
        } catch (SQLException e) {
            LOGGER.error("Error loading blocks with schedules", e);
            return List.of();
        }
    }

    public List<Block> findAllWithSchedules(Connection connection) throws SQLException {
        Connection conn = connection != null ? connection : DatabaseManager.getConnection();
        boolean shouldClose = connection == null;
        try {
            Map<Integer, Block> blocks = fetchBlocks(conn);
            if (!blocks.isEmpty()) {
                fetchSchedules(conn, blocks);
            }
            return new ArrayList<>(blocks.values());
        } finally {
            if (shouldClose) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.warn("Unable to close connection", e);
                }
            }
        }
    }

    public Optional<Block> findByCode(String blockCode) {
        try (Connection conn = DatabaseManager.getConnection()) {
            return findByCode(blockCode, conn);
        } catch (SQLException e) {
            LOGGER.error("Error finding block by code {}", blockCode, e);
            return Optional.empty();
        }
    }

    public Optional<Block> findById(int blockId) {
        try (Connection conn = DatabaseManager.getConnection()) {
            Map<Integer, Block> blocks = fetchBlocksById(conn, blockId);
            if (!blocks.isEmpty()) {
                fetchSchedules(conn, blocks);
                return blocks.values().stream().findFirst();
            }
            return Optional.empty();
        } catch (SQLException e) {
            LOGGER.error("Error finding block by id {}", blockId, e);
            return Optional.empty();
        }
    }

    public Optional<Block> findByCode(String blockCode, Connection connection) throws SQLException {
        Connection conn = connection != null ? connection : DatabaseManager.getConnection();
        boolean shouldClose = connection == null;
        try {
            Map<Integer, Block> blocks = fetchBlocks(conn, blockCode);
            if (!blocks.isEmpty()) {
                fetchSchedules(conn, blocks);
                return blocks.values().stream().findFirst();
            }
            return Optional.empty();
        } finally {
            if (shouldClose) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.warn("Unable to close connection", e);
                }
            }
        }
    }

    /**
     * Persists a new block record. Section may be null; in that case section_id is left null.
     */
    public Block save(Block block) {
        try (Connection conn = DatabaseManager.getConnection()) {
            return save(block, conn);
        } catch (SQLException e) {
            LOGGER.error("Error saving block {}", block != null ? block.getBlockCode() : "null", e);
            return block;
        }
    }

    public Block save(Block block, Connection connection) throws SQLException {
        if (block == null) {
            throw new IllegalArgumentException("block is required");
        }

        Connection conn = connection != null ? connection : DatabaseManager.getConnection();
        boolean shouldClose = connection == null;
        String sql = "INSERT INTO blocks (block_code, title, description, capacity, active, section_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            Section section = ensureSection(conn, block);

            ps.setString(1, block.getBlockCode());
            ps.setString(2, block.getTitle());
            ps.setString(3, block.getDescription());
            ps.setInt(4, block.getCapacity() > 0 ? block.getCapacity() : 40);
            ps.setInt(5, block.isActive() ? 1 : 1);
            ps.setInt(6, section.getId());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    block.setId(rs.getInt(1));
                }
            }
            block.setSection(section);
            return block;
        } finally {
            if (shouldClose) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.warn("Unable to close connection", e);
                }
            }
        }
    }

    private Section ensureSection(Connection conn, Block block) throws SQLException {
        if (block.getSection() != null && block.getSection().getId() != null) {
            return block.getSection();
        }

        String derivedProgram = deriveProgram(block.getBlockCode());
        String sectionCode = block.getBlockCode() + "-SEC";
        String sectionName = block.getBlockCode() + " Section";
        Section section = new Section();
        section.setCode(sectionCode);
        section.setName(sectionName);
        section.setProgram(derivedProgram);
        section.setYearLevel(1);
        section.setShift(null);
        section.setCapacity(block.getCapacity() > 0 ? block.getCapacity() : 40);

        String sql = "INSERT INTO sections (code, name, program, year_level, shift, capacity) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, section.getCode());
            ps.setString(2, section.getName());
            ps.setString(3, section.getProgram());
            ps.setInt(4, section.getYearLevel());
            ps.setString(5, section.getShift());
            ps.setInt(6, section.getCapacity());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    section.setId(rs.getInt(1));
                }
            }
        }
        return section;
    }

    private String deriveProgram(String blockCode) {
        if (blockCode == null) {
            return null;
        }
        String trimmed = blockCode.trim().toUpperCase();
        int spaceIdx = trimmed.indexOf(' ');
        if (spaceIdx > 0) {
            return trimmed.substring(0, spaceIdx);
        }
        return trimmed;
    }

    private Map<Integer, Block> fetchBlocks(Connection conn) throws SQLException {
        return fetchBlocks(conn, null);
    }

    private Map<Integer, Block> fetchBlocks(Connection conn, String blockCode) throws SQLException {
        String sql = "SELECT b.id AS block_id, b.block_code, b.title, b.description, b.capacity, b.active, " +
                "s.id AS section_id, s.code AS section_code, s.name AS section_name, s.program, s.year_level, s.shift, s.capacity AS section_capacity " +
                "FROM blocks b " +
                "JOIN sections s ON s.id = b.section_id " +
                "WHERE (? IS NULL OR b.block_code = ?) AND b.active = 1 " +
                "ORDER BY b.block_code";

        Map<Integer, Block> blocks = new LinkedHashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, blockCode);
            ps.setString(2, blockCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Section section = new Section(
                            rs.getInt("section_id"),
                            rs.getString("section_code"),
                            rs.getString("section_name"),
                            rs.getString("program"),
                            rs.getInt("year_level"),
                            rs.getString("shift"),
                            rs.getInt("section_capacity")
                    );

                    Block block = new Block(
                            rs.getInt("block_id"),
                            rs.getString("block_code"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getInt("capacity"),
                            rs.getInt("active") == 1,
                            section
                    );
                    blocks.put(block.getId(), block);
                }
            }
        }
        return blocks;
    }

    private Map<Integer, Block> fetchBlocksById(Connection conn, int blockId) throws SQLException {
        String sql = "SELECT b.id AS block_id, b.block_code, b.title, b.description, b.capacity, b.active, " +
                "s.id AS section_id, s.code AS section_code, s.name AS section_name, s.program, s.year_level, s.shift, s.capacity AS section_capacity " +
                "FROM blocks b " +
                "JOIN sections s ON s.id = b.section_id " +
                "WHERE b.id = ? AND b.active = 1";

        Map<Integer, Block> blocks = new LinkedHashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, blockId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Section section = new Section(
                            rs.getInt("section_id"),
                            rs.getString("section_code"),
                            rs.getString("section_name"),
                            rs.getString("program"),
                            rs.getInt("year_level"),
                            rs.getString("shift"),
                            rs.getInt("section_capacity")
                    );

                    Block block = new Block(
                            rs.getInt("block_id"),
                            rs.getString("block_code"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getInt("capacity"),
                            rs.getInt("active") == 1,
                            section
                    );
                    blocks.put(block.getId(), block);
                }
            }
        }
        return blocks;
    }

    private void fetchSchedules(Connection conn, Map<Integer, Block> blocks) throws SQLException {
        StringJoiner joiner = new StringJoiner(", ");
        blocks.keySet().forEach(id -> joiner.add("?"));

        String sql = "SELECT sch.id, sch.block_id, sch.course_id, sch.course_code, sch.subject, sch.day_pattern, sch.time_start, sch.time_end, " +
                "sch.room, sch.instructor, sch.units, c.name AS course_name " +
                "FROM schedules sch " +
                "LEFT JOIN courses c ON c.id = sch.course_id " +
                "WHERE sch.block_id IN (" + joiner + ") " +
                "ORDER BY sch.block_id, sch.day_pattern, sch.time_start";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int idx = 1;
            for (Integer id : blocks.keySet()) {
                ps.setInt(idx++, id);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Block block = blocks.get(rs.getInt("block_id"));
                    if (block == null) {
                        continue;
                    }
                    String subject = rs.getString("subject");
                    if (subject == null || subject.isBlank()) {
                        subject = rs.getString("course_name");
                    }
                    Schedule schedule = new Schedule(
                            rs.getInt("id"),
                            rs.getInt("block_id"),
                            (Integer) rs.getObject("course_id"),
                            rs.getString("course_code"),
                            subject,
                            rs.getString("day_pattern"),
                            rs.getString("time_start"),
                            rs.getString("time_end"),
                            rs.getString("room"),
                            rs.getString("instructor"),
                            rs.getDouble("units")
                    );
                    block.addSchedule(schedule);
                }
            }
        }
    }
}
