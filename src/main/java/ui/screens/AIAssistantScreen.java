
package ui.screens;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import ui.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AIAssistantScreen extends JFrame {

    private final JTextArea chatArea;
    private final JTextField inputField;
    private final JButton sendButton;
    private final JLabel thinkingLabel;
    private ChatSession chatSession;

    public AIAssistantScreen() {
        setTitle("AI Assistant");
        setSize(400, 600);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.BACKGROUND_COLOR);

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

    private void initializeGenerativeModel() {
        try {
            String projectId = System.getenv("PROJECT_ID");
            String location = "us-central1";
            String modelName = "gemini-1.5-pro-preview-0409";

            VertexAI vertexAI = new VertexAI(projectId, location);
            GenerativeModel model = new GenerativeModel(modelName, vertexAI);
            chatSession = new ChatSession(model);
        } catch (Exception e) { // Catch a more general exception
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error initializing AI model: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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
                            ex.printStackTrace();
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
}
