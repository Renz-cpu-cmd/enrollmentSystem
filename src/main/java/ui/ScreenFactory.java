package ui;

import context.ApplicationContext;
import ui.screens.AssessmentScreen;
import ui.screens.BioDataScreen;
import ui.screens.BlockSectioningScreen;
import ui.screens.DashboardScreen;
import ui.screens.DataPrivacyScreen;
import ui.screens.DigitalCORScreen;
import ui.screens.DocumentsScreen;
import ui.screens.IrregularScheduleScreen;
import ui.screens.PortalGatewayScreen;
import ui.screens.ProgramSelectionScreen;
import ui.screens.RegularPathScreen;
import ui.screens.RegularScheduleScreen;
import ui.screens.SplashScreen;
import ui.screens.StudentLoginScreen;

/**
 * Central place for building {@link ScreenView} instances with injected dependencies.
 */
public class ScreenFactory {
	private final ApplicationContext applicationContext;

	public ScreenFactory(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * Builds a screen corresponding to the given enum entry.
	 */
	public ScreenView create(Screen screen) {
		return switch (screen) {
			case SPLASH -> new SplashScreen();
			case PORTAL_GATEWAY -> new PortalGatewayScreen();
			case DATA_PRIVACY -> new DataPrivacyScreen();
			case STUDENT_LOGIN -> new StudentLoginScreen();
			case DASHBOARD -> new DashboardScreen();
			case REGULAR_PATH -> new RegularPathScreen();
			case REGULAR_SCHEDULE -> new RegularScheduleScreen();
			case IRREGULAR_SCHEDULE -> new IrregularScheduleScreen();
			case BIO_DATA -> new BioDataScreen(applicationContext.getEnrollmentService());
			case DOCUMENTS -> new DocumentsScreen();
			case PROGRAM_SELECTION -> new ProgramSelectionScreen();
			case BLOCK_SECTIONING -> new BlockSectioningScreen();
			case ASSESSMENT -> new AssessmentScreen();
			case DIGITAL_COR -> new DigitalCORScreen();
			default -> throw new UnsupportedOperationException("Screen not implemented yet: " + screen);
		};
	}

	protected ApplicationContext getApplicationContext() {
		return applicationContext;
	}
}
