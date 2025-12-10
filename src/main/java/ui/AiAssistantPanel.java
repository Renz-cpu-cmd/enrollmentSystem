package ui;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import dao.MessageDAO;
import model.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class AiAssistantPanel extends JPanel {

    private JTextArea chatArea;
    private JTextField inputBox;
    private JButton sendButton;

    private ChatSession chatSession;
    private MessageDAO messageDAO;
    private static final String SESSION_ID = "default_session";

    public AiAssistantPanel() {
        setLayout(new BorderLayout());
        messageDAO = new MessageDAO();

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputBox = new JTextField();
        sendButton = new JButton("Send");
        inputPanel.add(inputBox, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        inputBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        initializeGenerativeAi();
        loadChatHistory();
    }

    private void initializeGenerativeAi() {
        String projectId = System.getenv("PROJECT_ID");
        String location = System.getenv("LOCATION");
        String modelName = System.getenv("MODEL_NAME");

        if (projectId == null || location == null || modelName == null) {
            chatArea.setText("AI Assistant is not available. Please set PROJECT_ID, LOCATION, and MODEL_NAME environment variables.");
            inputBox.setEnabled(false);
            sendButton.setEnabled(false);
            return;
        }

        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            GenerativeModel model = new GenerativeModel(modelName, vertexAI);
            chatSession = new ChatSession(model);
            chatArea.setText("AI Assistant is ready. How can I help you?\n");
        } catch (Exception e) {
            chatArea.setText("Error initializing AI Assistant: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadChatHistory() {
        try {
            List<Message> messages = messageDAO.getMessagesBySessionId(SESSION_ID);
            for (Message message : messages) {
                chatArea.append(message.getSender() + ": " + message.getMessage() + "\n");
            }
        } catch (SQLException e) {
            chatArea.append("Error loading chat history: " + e.getMessage() + "\n");
        }
    }

    private void sendMessage() {
        String messageText = inputBox.getText().trim();
        if (messageText.isEmpty()) {
            return;
        }

        chatArea.append("You: " + messageText + "\n");
        inputBox.setText("");

        try {
            messageDAO.addMessage(new Message(SESSION_ID, "You", messageText));
            GenerateContentResponse response = chatSession.sendMessage(messageText);
            String responseText = response.getCandidates(0).getContent().getParts(0).getText();
            chatArea.append("AI: " + responseText + "\n");
            messageDAO.addMessage(new Message(SESSION_ID, "AI", responseText));
        } catch (Exception e) {
            chatArea.append("Error sending message: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }
}
