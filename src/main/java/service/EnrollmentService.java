package service;

import dao.AssessmentDAO;
import dao.BlockDAO;
import dao.EnrollmentDAO;
import dao.StudentDAO;
import model.Assessment;
import model.AssessmentFee;
import model.Block;
import model.Enrollment;
import model.Schedule;
import model.Student;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dao.DatabaseManager;
import util.SessionManager;
import util.Validator;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EnrollmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnrollmentService.class);

    private final StudentDAO studentDAO;
    private final BlockDAO blockDAO;
    private final EnrollmentDAO enrollmentDAO;
    private final AssessmentDAO assessmentDAO;

    private static final String DEFAULT_TERM = "2025-2026";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int PASSWORD_MIN_LENGTH = 12;
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()-_=+[]{}|;:,.<>?";
    private static final String PASSWORD_POOL = UPPER + LOWER + DIGITS + SPECIAL;

    public EnrollmentService(StudentDAO studentDAO, BlockDAO blockDAO, EnrollmentDAO enrollmentDAO, AssessmentDAO assessmentDAO) {
        this.studentDAO = studentDAO;
        this.blockDAO = blockDAO;
        this.enrollmentDAO = enrollmentDAO;
        this.assessmentDAO = assessmentDAO;
    }

    /**
     * Registers a new student using the supplied command payload.
     *
     * @param command enrollment payload (immutable DTO)
     * @return a {@link ServiceResult} describing success/failure plus the {@link EnrollmentResult} payload
     */
    public ServiceResult<EnrollmentResult> registerNewStudent(EnrollStudentCommand command) {
        ServiceResult<Void> validationResult;
        try {
            validationResult = validateCommand(command);
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while validating enrollment data.", ex);
            return ServiceResult.failure("Unable to validate enrollment data at this time.");
        }

        if (!validationResult.isSuccess()) {
            return ServiceResult.failure(validationResult.getMessage());
        }

        Student student = command.toStudent();
        try {
            assignStudentIdIfMissing(student);
        } catch (IllegalStateException ex) {
            LOGGER.error("Unable to assign a unique student ID for {}", student.getEmail(), ex);
            return ServiceResult.failure("Unable to generate a student ID. Please try again.");
        }

        PasswordResolution passwordResolution = resolvePassword(command.getRawPassword());
        student.setPassword(hashPassword(passwordResolution.rawPassword()));

        return persistStudent(student, passwordResolution.generatedPassword());
    }

    private ServiceResult<Void> validateCommand(EnrollStudentCommand command) {
        if (command == null) {
            return ServiceResult.failure("Enrollment data is required.");
        }
        if (isBlank(command.getFirstName())) {
            return ServiceResult.failure("First name is required.");
        }
        if (isBlank(command.getLastName())) {
            return ServiceResult.failure("Last name is required.");
        }
        if (isBlank(command.getEmail())) {
            return ServiceResult.failure("Email is required.");
        }
        if (!Validator.isValidEmail(command.getEmail())) {
            return ServiceResult.failure("Email format is invalid.");
        }
        if (isBlank(command.getMobileNumber())) {
            return ServiceResult.failure("Mobile number is required.");
        }
        if (!Validator.isValidPhilippineMobileNumber(command.getMobileNumber())) {
            return ServiceResult.failure("Mobile number format is invalid.");
        }
        if (studentDAO.existsByEmail(command.getEmail())) {
            return ServiceResult.failure("Email is already registered.");
        }
        if (!isBlank(command.getStudentId()) && studentDAO.existsByStudentId(command.getStudentId())) {
            return ServiceResult.failure("Student ID is already in use.");
        }
        if (!isBlank(command.getRawPassword()) && !isPasswordStrong(command.getRawPassword())) {
            return ServiceResult.failure("Password does not meet complexity requirements.");
        }
        return ServiceResult.success(null);
    }

    private void assignStudentIdIfMissing(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student payload is required.");
        }
        if (isBlank(student.getStudentId())) {
            student.setStudentId(generateUniqueStudentId());
        }
    }

    private PasswordResolution resolvePassword(String requestedPassword) {
        if (isBlank(requestedPassword)) {
            String generated = generateStrongPassword();
            return new PasswordResolution(generated, generated);
        }
        return new PasswordResolution(requestedPassword, null);
    }

    private String hashPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));
    }

    private ServiceResult<EnrollmentResult> persistStudent(Student student, String generatedPassword) {
        Connection connection = null;
        try {
            connection = DatabaseManager.getConnection();
            connection.setAutoCommit(false);

            boolean saved = studentDAO.addWithConnection(student, connection);
            if (!saved) {
                rollbackQuietly(connection);
                return ServiceResult.failure("Could not save student data.");
            }

            connection.commit();
            return ServiceResult.success(
                "Student registered successfully.",
                new EnrollmentResult(student, generatedPassword)
            );
        } catch (Exception ex) {
            rollbackQuietly(connection);
            LOGGER.error("Failed to persist student {}", student != null ? student.getEmail() : "unknown", ex);
            return ServiceResult.failure("Failed to register student. Please try again.");
        } finally {
            finalizeConnection(connection);
        }
    }

    private void rollbackQuietly(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.rollback();
        } catch (SQLException ex) {
            LOGGER.warn("Unable to rollback transaction.", ex);
        }
    }

    private void finalizeConnection(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.setAutoCommit(true);
        } catch (SQLException ex) {
            LOGGER.warn("Unable to reset auto-commit.", ex);
        }
        try {
            connection.close();
        } catch (SQLException ex) {
            LOGGER.warn("Unable to close connection.", ex);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String generateUniqueStudentId() {
        String year = String.valueOf(java.time.Year.now());
        for (int attempts = 0; attempts < 10; attempts++) {
            String candidate = "S-" + year + "-" + randomAlphaNumeric(6).toUpperCase(Locale.ROOT);
            if (!studentDAO.existsByStudentId(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Unable to generate a unique student ID after multiple attempts.");
    }

    private String randomAlphaNumeric(int length) {
        String chars = UPPER + DIGITS;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(SECURE_RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String generateStrongPassword() {
        StringBuilder pwd = new StringBuilder();
        pwd.append(randomChar(UPPER));
        pwd.append(randomChar(LOWER));
        pwd.append(randomChar(DIGITS));
        pwd.append(randomChar(SPECIAL));
        int remaining = PASSWORD_MIN_LENGTH - pwd.length();
        for (int i = 0; i < remaining; i++) {
            pwd.append(randomChar(PASSWORD_POOL));
        }
        return shuffle(pwd.toString());
    }

    private char randomChar(String pool) {
        return pool.charAt(SECURE_RANDOM.nextInt(pool.length()));
    }

    private String shuffle(String input) {
        char[] chars = input.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = SECURE_RANDOM.nextInt(i + 1);
            char tmp = chars[i];
            chars[i] = chars[j];
            chars[j] = tmp;
        }
        return new String(chars);
    }

    /**
     * Creates an enrollment for an already-registered student (returning/continuing flow).
     * Delegates to the same pipeline used by the freshman flow but keeps the API explicit.
     */
    public ServiceResult<Void> createEnrollmentForExisting(String blockCode) {
        return processEnrollment(blockCode);
    }

    public ServiceResult<Void> processEnrollment(String blockCode) {
        if (isBlank(blockCode)) {
            return ServiceResult.failure("Block selection is required before enrollment.");
        }

        var currentStudent = SessionManager.getInstance().getCurrentStudent();
        if (currentStudent == null || currentStudent.getId() <= 0) {
            return ServiceResult.failure("No active student session. Please login again.");
        }

        try {
            return DatabaseManager.runInTransaction(conn -> doProcessEnrollment(blockCode, currentStudent.getId(), conn));
        } catch (SQLException ex) {
            LOGGER.error("Enrollment failed for block {}", blockCode, ex);
            return ServiceResult.failure("Unable to process enrollment right now.");
        }
    }

    private ServiceResult<Void> doProcessEnrollment(String blockCode, int studentId, Connection conn) throws SQLException {
        // Already enrolled?
        if (enrollmentDAO.findActiveByStudent(studentId).isPresent()) {
            return ServiceResult.failure("You already have an active enrollment.");
        }

        Block block = blockDAO.findByCode(blockCode, conn).orElseGet(() -> {
            Block b = new Block();
            b.setBlockCode(blockCode);
            b.setCapacity(40);
            b.setActive(true);
            try {
                return blockDAO.save(b, conn);
            } catch (SQLException ex) {
                LOGGER.error("Failed to create mock block {}", blockCode, ex);
                return b;
            }
        });

        if (block.getSchedules() == null || block.getSchedules().isEmpty()) {
            seedSchedulesForBlock(block, conn);
        }

        double totalUnits = calculateTotalUnits(block);
        if (totalUnits <= 0) {
            totalUnits = 18.0; // fallback when mock blocks have no schedules persisted
        }

        BigDecimal tuition = BigDecimal.valueOf(totalUnits * 1500.0);
        BigDecimal misc = BigDecimal.valueOf(2500.0);
        BigDecimal lab = BigDecimal.valueOf(1000.0);
        BigDecimal other = BigDecimal.ZERO;
        BigDecimal totalDue = tuition.add(misc).add(lab).add(other);

        Enrollment enrollment = new Enrollment(studentId, block.getId(), DEFAULT_TERM, "ENROLLED");
        enrollmentDAO.create(enrollment, conn);

        Assessment assessment = new Assessment();
        assessment.setEnrollmentId(enrollment.getId());
        assessment.setTotalUnits(totalUnits);
        assessment.setTuitionFee(tuition);
        assessment.setMiscFee(misc);
        assessment.setLabFee(lab);
        assessment.setOtherFee(other);
        assessment.setTotalDue(totalDue);
        assessment.setCurrency("PHP");
        assessment.setStatus("PENDING");
        assessmentDAO.saveAssessment(assessment, conn);

        List<AssessmentFee> fees = new ArrayList<>();
        fees.add(new AssessmentFee(assessment.getId(), "TUITION", "Tuition Fee", tuition, 1));
        fees.add(new AssessmentFee(assessment.getId(), "MISC", "Miscellaneous Fees", misc, 2));
        fees.add(new AssessmentFee(assessment.getId(), "LAB", "Laboratory Fees", lab, 3));
        assessmentDAO.saveFees(fees, conn);

        LOGGER.info("Enrollment completed: student {} -> block {} (enrollment #{})", studentId, blockCode, enrollment.getId());
        return ServiceResult.success("Enrollment confirmed for " + blockCode + ".", null);
    }

    private double calculateTotalUnits(Block block) {
        return block.getSchedules().stream()
            .mapToDouble(s -> s.getUnits())
            .sum();
    }

    /**
     * Fallback: if a block has no schedules (e.g., mock block created on the fly), seed a minimal set so
     * downstream UI (Dashboard) can display a timetable and assessment can compute units from real rows.
     */
    private void seedSchedulesForBlock(Block block, Connection conn) throws SQLException {
        if (block == null || block.getId() == null) {
            return;
        }

        String[][] templates = new String[][]{
                {"DSA", "Data Structures", "Mon/Wed", "08:00", "09:30", "CL-1", "Prof. A", "3"},
                {"OOP", "Object Oriented Prog", "Tue/Thu", "10:00", "11:30", "CL-2", "Prof. B", "3"},
                {"ETH", "Ethics & Values", "Fri", "13:00", "15:00", "LEC-1", "Prof. C", "3"}
        };

        String sql = "INSERT INTO schedules (block_id, course_code, subject, day_pattern, time_start, time_end, room, instructor, units) " +
                "VALUES (?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String[] t : templates) {
                double units = Double.parseDouble(t[7]);
                ps.setInt(1, block.getId());
                ps.setString(2, block.getBlockCode() + "-" + t[0]);
                ps.setString(3, t[1]);
                ps.setString(4, t[2]);
                ps.setString(5, t[3]);
                ps.setString(6, t[4]);
                ps.setString(7, t[5]);
                ps.setString(8, t[6]);
                ps.setDouble(9, units);
                ps.executeUpdate();

                Schedule sched = new Schedule(null, block.getId(), null, block.getBlockCode() + "-" + t[0], t[1], t[2], t[3], t[4], t[5], t[6], units);
                block.addSchedule(sched);
            }
        }
    }

    private boolean isPasswordStrong(String password) {
        if (isBlank(password) || password.length() < PASSWORD_MIN_LENGTH) {
            return false;
        }
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (UPPER.indexOf(c) >= 0) hasUpper = true;
            else if (LOWER.indexOf(c) >= 0) hasLower = true;
            else if (DIGITS.indexOf(c) >= 0) hasDigit = true;
            else if (SPECIAL.indexOf(c) >= 0) hasSpecial = true;
        }
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    private record PasswordResolution(String rawPassword, String generatedPassword) {}

    public static final class EnrollmentResult {
        private final Student student;
        private final String generatedPassword;

        public EnrollmentResult(Student student, String generatedPassword) {
            this.student = student;
            this.generatedPassword = generatedPassword;
        }

        public Student getStudent() {
            return student;
        }

        public String getGeneratedPassword() {
            return generatedPassword;
        }

        public boolean hasGeneratedPassword() {
            return generatedPassword != null;
        }
    }

    public static final class ServiceResult<T> {
        private final boolean success;
        private final String message;
        private final T data;

        private ServiceResult(boolean success, String message, T data) {
            this.success = success;
            this.message = message == null ? "" : message;
            this.data = data;
        }

        public static <T> ServiceResult<T> success(String message, T data) {
            return new ServiceResult<>(true, message, data);
        }

        public static <T> ServiceResult<T> success(T data) {
            return new ServiceResult<>(true, "", data);
        }

        public static <T> ServiceResult<T> failure(String message) {
            return new ServiceResult<>(false, message == null ? "" : message, null);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public T getData() {
            return data;
        }
    }

    public static final class EnrollStudentCommand {
        private final String firstName;
        private final String lastName;
        private final String email;
        private final String mobileNumber;
        private final String studentId;
        private final String rawPassword;
        private final Student template;

        private EnrollStudentCommand(Builder builder) {
            this.firstName = trim(builder.firstName);
            this.lastName = trim(builder.lastName);
            this.email = trim(builder.email);
            this.mobileNumber = trim(builder.mobileNumber);
            this.studentId = trim(builder.studentId);
            this.rawPassword = builder.rawPassword;
            this.template = builder.template;
        }

        public static Builder builder() {
            return new Builder();
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }

        public String getMobileNumber() {
            return mobileNumber;
        }

        public String getStudentId() {
            return studentId;
        }

        public String getRawPassword() {
            return rawPassword;
        }

        public Student toStudent() {
            Student student = template != null ? template : new Student();
            student.setFirstName(firstName);
            student.setLastName(lastName);
            student.setEmail(email);
            student.setMobileNumber(mobileNumber);
            if (studentId != null) {
                student.setStudentId(studentId);
            }
            return student;
        }

        public static final class Builder {
            private String firstName;
            private String lastName;
            private String email;
            private String mobileNumber;
            private String studentId;
            private String rawPassword;
            private Student template;

            private Builder() {
            }

            public Builder firstName(String firstName) {
                this.firstName = firstName;
                return this;
            }

            public Builder lastName(String lastName) {
                this.lastName = lastName;
                return this;
            }

            public Builder email(String email) {
                this.email = email;
                return this;
            }

            public Builder mobileNumber(String mobileNumber) {
                this.mobileNumber = mobileNumber;
                return this;
            }

            public Builder studentId(String studentId) {
                this.studentId = studentId;
                return this;
            }

            public Builder rawPassword(String rawPassword) {
                this.rawPassword = rawPassword;
                return this;
            }

            public Builder template(Student template) {
                this.template = template;
                return this;
            }

            public Builder fromStudent(Student student) {
                if (student == null) {
                    return this;
                }
                this.template = student;
                this.firstName = student.getFirstName();
                this.lastName = student.getLastName();
                this.email = student.getEmail();
                this.mobileNumber = student.getMobileNumber();
                this.studentId = student.getStudentId();
                this.rawPassword = student.getPassword();
                return this;
            }

            public EnrollStudentCommand build() {
                return new EnrollStudentCommand(this);
            }
        }

        private static String trim(String value) {
            return value == null ? null : value.trim();
        }
    }
}