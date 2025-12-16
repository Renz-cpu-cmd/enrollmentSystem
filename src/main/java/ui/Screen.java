package ui;

import ui.screens.*;

import javax.swing.*;

public enum Screen {
    SPLASH("Splash", SplashScreen.class),
    PORTAL_GATEWAY("PortalGateway", PortalGatewayScreen.class),
    DATA_PRIVACY("DataPrivacy", DataPrivacyScreen.class),
    BIO_DATA("BioData", BioDataScreen.class),
    DOCUMENTS("Documents", DocumentsScreen.class),
    PROGRAM_SELECTION("ProgramSelection", ProgramSelectionScreen.class),
    BLOCK_SECTIONING("BlockSectioning", BlockSectioningScreen.class),
    RETURNING_BLOCK_SCHEDULE("ReturningBlockSchedule", ReturningBlockScheduleScreen.class),
    STUDENT_LOGIN("StudentLogin", StudentLoginScreen.class),
    DASHBOARD("Dashboard", DashboardScreen.class),
    ASSESSMENT("Assessment", AssessmentScreen.class),
    PAYMENT("Payment", PaymentScreen.class),
    COR("COR", CORScreen.class),
    AI_ASSISTANT("AIAssistant", ui.screens.AIAssistantScreen.class);

    private final String name;
    private final Class<? extends JPanel> screenClass;

    Screen(String name, Class<? extends JPanel> screenClass) {
        this.name = name;
        this.screenClass = screenClass;
    }

    public String getName() {
        return name;
    }

    public Class<? extends JPanel> getScreenClass() {
        return screenClass;
    }

    public static Screen fromClassName(String className) {
        for (Screen s : values()) {
            if (s.getScreenClass().getName().equals(className)) {
                return s;
            }
        }
        return null;
    }
}
