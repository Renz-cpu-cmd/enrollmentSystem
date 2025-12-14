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
    STUDENT_LOGIN("StudentLogin", StudentLoginScreen.class),
    DASHBOARD("Dashboard", DashboardScreen.class),
    REGULAR_PATH("RegularPath", RegularPathScreen.class),
    REGULAR_SCHEDULE("RegularSchedule", RegularScheduleScreen.class),
    IRREGULAR_SCHEDULE("IrregularSchedule", IrregularScheduleScreen.class),
    IRREGULAR_PATH("IrregularPath", IrregularPathScreen.class),
    ASSESSMENT("Assessment", AssessmentScreen.class),
    DIGITAL_COR("DigitalCOR", DigitalCORScreen.class),
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
