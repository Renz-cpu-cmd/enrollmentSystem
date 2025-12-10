
package ui;

import ui.screens.*;
import ui.theme.FloatingActionButton;
import ui.theme.IconCreator;

import javax.swing.*;
import java.awt.*;

/**
 * The main application window, designed to simulate a mobile phone screen.
 * It uses a JLayeredPane to manage a CardLayout of screens and a floating action button.
 */
public class MobileFrame extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final JLayeredPane layeredPane;
    private final FloatingActionButton aiButton;

    public MobileFrame() {
        setTitle("Enrollment System");
        setSize(400, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Use a JLayeredPane to place the FAB over the content
        layeredPane = new JLayeredPane();

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false); // Make it transparent

        // Add all screens to the content panel
        addScreens();

        // Add the main content panel to the layered pane
        contentPanel.setBounds(0, 0, 400, 700);
        layeredPane.add(contentPanel, JLayeredPane.DEFAULT_LAYER);

        // Create and add the Floating Action Button for the AI Assistant
        aiButton = new FloatingActionButton(IconCreator.AI_ASSISTANT_ICON);
        aiButton.setBounds(320, 600, 56, 56); // Position in the bottom-right
        layeredPane.add(aiButton, JLayeredPane.PALETTE_LAYER);

        // Add action listener to the button
        aiButton.addActionListener(e -> {
            AIAssistantScreen aiAssistant = new AIAssistantScreen(); // Corrected constructor call
            aiAssistant.setVisible(true);
        });

        setContentPane(layeredPane);
    }

    private void addScreens() {
        contentPanel.add(new ui.screens.SplashScreen(), "Splash");
        contentPanel.add(new PortalGatewayScreen(), "PortalGateway");
        contentPanel.add(new DataPrivacyScreen(), "DataPrivacy");
        contentPanel.add(new BioDataScreen(), "BioData");
        contentPanel.add(new ProgramSelectionScreen(), "ProgramSelection");
        contentPanel.add(new BlockSectioningScreen(), "BlockSectioning");
        contentPanel.add(new StudentLoginScreen(), "StudentLogin");
        contentPanel.add(new DashboardScreen(), "Dashboard");
        contentPanel.add(new StatusDeclarationScreen(), "StatusDeclaration");
        contentPanel.add(new RegularPathScreen(), "RegularPath");
        contentPanel.add(new IrregularPathScreen(), "IrregularPath");
        contentPanel.add(new AssessmentOfFeesScreen(), "AssessmentOfFees");
        contentPanel.add(new DigitalCORScreen(), "DigitalCOR");
    }

    /**
     * Switches the currently visible screen.
     * @param screenName The name of the screen to show.
     */
    public void showScreen(String screenName) {
        cardLayout.show(contentPanel, screenName);
    }
}
