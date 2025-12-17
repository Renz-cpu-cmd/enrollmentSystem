package ui.screens;

/**
 * Optional AI assistant chat panel screen.
 *
 * <p>Extends `JPanel` and implements `ScreenView` to integrate seamlessly with
 * navigation; functionality depends on environment variables for Vertex AI.</p>
 */

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.NavigationContext;
import ui.ScreenView;
import ui.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dialog;

public class AIAssistantScreen extends JPanel implements ScreenView {

    private static final Logger LOGGER = LoggerFactory.getLogger(AIAssistantScreen.class);
    private final JTextArea chatArea;
    private final JTextField inputField;
    private final JButton sendButton;
    private final JLabel thinkingLabel;
    private final JLabel offlineLabel;
    private boolean offlineMode = false;
    private int cannedIndex = 0;
    private ChatSession chatSession;

    public AIAssistantScreen() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_COLOR);

        add(buildHeader(), BorderLayout.NORTH);

        // Chat display area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setBackground(Theme.SURFACE_COLOR);
        chatArea.setForeground(Theme.TEXT_PRIMARY_COLOR);
        chatArea.setFont(Theme.BODY_FONT);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(Theme.BACKGROUND_COLOR);

        offlineLabel = new JLabel();
        offlineLabel.setFont(Theme.LABEL_FONT);
        offlineLabel.setForeground(Color.GRAY);
        offlineLabel.setHorizontalAlignment(SwingConstants.LEFT);
        offlineLabel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        offlineLabel.setVisible(false);
        inputPanel.add(offlineLabel, BorderLayout.NORTH);

        inputField = new JTextField();
        inputField.setFont(Theme.BODY_FONT);
        inputField.addActionListener(new SendAction());
        inputPanel.add(inputField, BorderLayout.CENTER);

        sendButton = new JButton("Send");
        sendButton.setFont(Theme.BOLD_BODY_FONT);
        sendButton.setBackground(Theme.PRIMARY_COLOR);
        sendButton.setForeground(Color.WHITE);
        sendButton.addActionListener(new SendAction());
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        thinkingLabel = new JLabel("Thinking...");
        thinkingLabel.setFont(Theme.LABEL_FONT);
        thinkingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        thinkingLabel.setVisible(false);
        inputPanel.add(thinkingLabel, BorderLayout.SOUTH);

        add(inputPanel, BorderLayout.SOUTH);

        initializeGenerativeModel();
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.SURFACE_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel title = new JLabel("AI Assistant");
        title.setFont(Theme.BOLD_BODY_FONT);
        title.setForeground(Theme.TEXT_PRIMARY_COLOR);

        JButton close = new JButton("Exit");
        close.setFont(Theme.LABEL_FONT);
        close.setBackground(new Color(240, 240, 240));
        close.addActionListener(e -> closeParentWindow());

        header.add(title, BorderLayout.WEST);
        header.add(close, BorderLayout.EAST);
        return header;
    }

    private void initializeGenerativeModel() {
        try {
            String projectId = System.getenv("PROJECT_ID");
            String location = "us-central1";
            String modelName = "gemini-1.5-pro-preview-0409";

            if (projectId == null || projectId.isBlank()) {
                enterOfflineMode("PROJECT_ID is missing; running offline with canned replies.");
                return;
            }

            VertexAI vertexAI = new VertexAI(projectId, location);
            GenerativeModel model = new GenerativeModel(modelName, vertexAI);
            chatSession = new ChatSession(model);
        } catch (Exception e) { // Catch a more general exception
            LOGGER.error("Error initializing AI model", e);
            enterOfflineMode("AI unavailable; using offline canned replies.");
        }
    }

    private void enterOfflineMode(String reason) {
        offlineMode = true;
        offlineLabel.setText(reason);
        offlineLabel.setVisible(true);
    }
    
    private class SendAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String message = inputField.getText().trim();
            if (!message.isEmpty()) {
                // Append user message to chat area
                chatArea.append("You: " + message + "\n");
                inputField.setText("");
                
                setThinking(true);

                if (offlineMode || chatSession == null) {
                    chatArea.append("AI (offline): " + nextCannedResponse(message) + "\n");
                    setThinking(false);
                    return;
                }

                // Send message to AI model in a background thread
                new SwingWorker<String, Void>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        return ResponseHandler.getText(chatSession.sendMessage(message));
                    }

                    @Override
                    protected void done() {
                        try {
                            String response = get();
                            chatArea.append("AI: " + response + "\n");
                        } catch (Exception ex) {
                            LOGGER.error("Error getting AI response", ex);
                            chatArea.append("AI: Error - " + ex.getMessage() + "\n");
                        } finally {
                            setThinking(false);
                        }
                    }
                }.execute();
            }
        }
    }
    
    private void setThinking(boolean thinking) {
        thinkingLabel.setVisible(thinking);
        sendButton.setEnabled(!thinking);
        inputField.setEnabled(!thinking);
    }

    private void closeParentWindow() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof Dialog) {
            w.dispose();
        } else if (w != null) {
            w.setVisible(false);
        }
    }

    public static void openFloating(Window owner) {
        JDialog dialog = new JDialog(owner, "AI Assistant", Dialog.ModalityType.MODELESS);
        AIAssistantScreen panel = new AIAssistantScreen();
        dialog.setContentPane(panel);
        dialog.setSize(new Dimension(460, 560));
        dialog.setLocationRelativeTo(owner);
        dialog.setAlwaysOnTop(true);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    @Override
    public void onEnter(NavigationContext context) {
        // No-op: AI session already initialized on construction.
    }

    @Override
    public void onLeave() {
        // No-op: nothing additional to clean up when leaving yet.
    }

    private String nextCannedResponse(String userMessage) {
        String msg = userMessage == null ? "" : userMessage.toLowerCase();

        // Lightweight keyword routing for better offline answers
        if (msg.contains("enroll") || msg.contains("block")) {
            return "Enrollment basics: pick a block via Returning Block Schedule, confirm, then see status on Dashboard. If the block has no schedule, we auto-seed a timetable so your Dashboard shows classes.";
        }
        if (msg.contains("pay") || msg.contains("payment") || msg.contains("fee")) {
            return "Payments: open the Payment screen. Pending assessments show due vs paid; dashboards show paid/due and trend. For receipts, go to Payments; for COR, finish payment then open COR.";
        }
        if (msg.contains("schedule") || msg.contains("class")) {
            return "Schedule: Dashboard’s Schedule card shows today/week; conflict badges highlight overlaps. Timeline slider previews a time; heatmap shows load by day/hour.";
        }
        if (msg.contains("cor") || msg.contains("certificate")) {
            return "COR: Once status is Officially Enrolled, open the COR screen from Dashboard quick actions. If still pending payment, settle dues first.";
        }
        if (msg.contains("login") || msg.contains("sign")) {
            return "Login issues: ensure session is active; if expired you’ll be sent to Student Login. For dark mode/theme, toggle in Dashboard hero controls.";
        }
        if (msg.contains("offline") || msg.contains("internet")) {
            return "Offline mode: real AI is unavailable; you’re seeing canned help. Set PROJECT_ID and network, then reopen the AI screen for live answers.";
        }

        String[] canned = new String[]{
                "I’m offline right now. Try again when internet/credentials are available.",
                "Quick tip: Check your enrollment status, finances, and schedule on the Dashboard cards.",
                "Need help? Use Payments for dues, Assessment for breakdown, Schedule card for classes.",
                "For conflicts, look for red conflict badges in the Schedule card and pick another block.",
                "COR becomes available after you’re officially enrolled; use the COR quick action.",
                "Theme and density toggles are in the Dashboard hero (Light/Dim, Compact, Focus)."
        };
        String reply = canned[cannedIndex % canned.length];
        cannedIndex++;
        return reply;
    }
}