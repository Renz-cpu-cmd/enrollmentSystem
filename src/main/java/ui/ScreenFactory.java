package ui;

import context.ApplicationContext;
import ui.screens.AssessmentScreen;
import ui.screens.BioDataScreen;
import ui.screens.BlockSectioningScreen;
import ui.screens.AIAssistantScreen;
import ui.screens.CORScreen;
import ui.screens.DashboardScreen;
import ui.screens.DataPrivacyScreen;
import ui.screens.DocumentsScreen;
import ui.screens.ReturningBlockScheduleScreen;
import ui.screens.PaymentScreen;
import ui.screens.PortalGatewayScreen;
import ui.screens.ProgramSelectionScreen;
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
			case DASHBOARD -> new DashboardScreen(applicationContext.getStudentDAO(), applicationContext.getEnrollmentDAO(), applicationContext.getAssessmentDAO(), applicationContext.getBlockDAO());
			case BIO_DATA -> new BioDataScreen(applicationContext.getEnrollmentService());
			case DOCUMENTS -> new DocumentsScreen();
			case PROGRAM_SELECTION -> new ProgramSelectionScreen();
			case BLOCK_SECTIONING -> new BlockSectioningScreen(applicationContext.getEnrollmentService(), applicationContext.getBlockDAO());
			case RETURNING_BLOCK_SCHEDULE -> new ReturningBlockScheduleScreen(applicationContext.getEnrollmentService(), applicationContext.getBlockDAO());
			case ASSESSMENT -> new AssessmentScreen(applicationContext.getEnrollmentDAO(), applicationContext.getAssessmentDAO());
			case PAYMENT -> new PaymentScreen(applicationContext.getPaymentService(), applicationContext.getAssessmentDAO());
			case COR -> new CORScreen(applicationContext.getAssessmentDAO(), applicationContext.getEnrollmentDAO(), applicationContext.getBlockDAO(), applicationContext.getPaymentDAO());
			case AI_ASSISTANT -> new AIAssistantScreen();
			default -> throw new UnsupportedOperationException("Screen not implemented yet: " + screen);
		};
	}

	protected ApplicationContext getApplicationContext() {
		return applicationContext;
	}
}
