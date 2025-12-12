package ui;

import ui.screens.*;

import javax.swing.*;

public enum Screen {
    SPLASH("Splash", SplashScreen.class),
    PORTAL_GATEWAY("PortalGateway", PortalGatewayScreen.class),
    DATA_PRIVACY("DataPrivacy", DataPrivacyScreen.class),
    BIO_DATA("BioData", BioDataScreen.class),
    PROGRAM_SELECTION("ProgramSelection", ProgramSelectionScreen.class),
    BLOCK_SECTIONING("BlockSectioning", BlockSectioningScreen.class),
    STUDENT_LOGIN("StudentLogin", StudentLoginScreen.class),
    DASHBOARD("Dashboard", DashboardScreen.class),
    STATUS_DECLARATION("StatusDeclaration", StatusDeclarationScreen.class),
    REGULAR_PATH("RegularPath", RegularPathScreen.class),
    IRREGULAR_PATH("IrregularPath", IrregularPathScreen.class),
    ASSESSMENT_OF_FEES("AssessmentOfFees", AssessmentOfFeesScreen.class),
    DIGITAL_COR("DigitalCOR", DigitalCORScreen.class),
    AI_ASSISTANT("AIAssistant", AIAssistantScreen.class);

    private final String name;
    private final Class<? extends JPanel> screenClass;

    Screen(String name, Class<? extends JPanel> screenClass) {
        this.name = name;
        this.screenClass = screenClass;
    }

    public StringgetName() {
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
