package dao;
import model.User;
import util.DBUtil;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class UserDAO {
    public static boolean userExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, username);
            ResultSet rs = p.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }
    public static void createUser(String username, String plainPassword, String role) throws SQLException {
        String hash = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        String sql = "INSERT INTO users(username,password_hash,role) VALUES(?,?,?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, username); p.setString(2, hash); p.setString(3, role);
            p.executeUpdate();
        }
    }
    public static boolean authenticate(String username, String plainPassword) throws SQLException {
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, username);
            ResultSet rs = p.executeQuery();
            if (!rs.next()) return false;
            String hash = rs.getString("password_hash");
            return BCrypt.checkpw(plainPassword, hash);
        }
    }
}
