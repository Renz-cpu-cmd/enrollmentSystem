import ui.MainFrame;
import ui.Screen;
import ui.UIConfig;
import util.GlobalExceptionHandler;
import context.ApplicationContext;
import util.DatabaseMigrator;
import model.Student;
import util.SessionManager;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // Set the global exception handler
        Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());

        // Choose light or dark FlatLaf via system property: -Dapp.theme=dark
        boolean useDark = "dark".equalsIgnoreCase(System.getProperty("app.theme", "light"));
        UIConfig.initialize(useDark);

        ApplicationContext applicationContext = new ApplicationContext();

        // Ensure DB schema is present/updated (idempotent)
        DatabaseMigrator.runMigrations();

        // Run the UI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(applicationContext);

            Student mockStudent = new Student();
            mockStudent.setStudentId("123");
            mockStudent.setFirstName("Test");
            mockStudent.setLastName("User");
            SessionManager.getInstance().setCurrentStudent(mockStudent);

            frame.setVisible(true);
            // frame.showScreen(Screen.SPLASH);
            frame.showScreen(Screen.PROGRAM_SELECTION);
        });
    }
}
