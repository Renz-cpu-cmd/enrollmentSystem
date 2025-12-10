package dao;

import model.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    public void addMessage(Message message) throws SQLException {
        String sql = "INSERT INTO messages (session_id, sender, message) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, message.getSessionId());
            pstmt.setString(2, message.getSender());
            pstmt.setString(3, message.getMessage());
            pstmt.executeUpdate();
        }
    }

    public List<Message> getMessagesBySessionId(String sessionId) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE session_id = ? ORDER BY timestamp ASC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sessionId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Message message = new Message(
                        rs.getInt("id"),
                        rs.getString("session_id"),
                        rs.getString("sender"),
                        rs.getString("message"),
                        rs.getString("timestamp")
                );
                messages.add(message);
            }
        }
        return messages;
    }
}
