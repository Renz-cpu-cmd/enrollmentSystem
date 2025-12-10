
import dao.UserDAO;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.SQLException;

public class CreateAdmin {
    public static void main(String[] args) {
        try {
            if (!UserDAO.userExists("admin")) {
                UserDAO.createUser("admin", "admin", "admin");
                System.out.println("Admin user created successfully.");
            } else {
                System.out.println("Admin user already exists.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
