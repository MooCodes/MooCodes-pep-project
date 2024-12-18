package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Model.Message;
import Util.ConnectionUtil;

public class MessageDAO {
    // Insert a new message into the database
    public Message createMessage(Message message) throws SQLException {
        String sql = "INSERT INTO Message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, message.getPosted_by());
            ps.setString(2, message.getMessage_text());
            ps.setLong(3, message.getTime_posted_epoch());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to create message.");
            }

            // Get the generated message_id
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    message.setMessage_id(generatedKeys.getInt(1));
                }
            }
        }
        return message;
    }

    // Check if a user exists in the database
    public boolean doesUserExist(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) AS count FROM Account WHERE account_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        }
        return false;
    }

    // Retrieve all messages from the database
    public List<Message> getAllMessages() throws SQLException {
        String sql = "SELECT * FROM Message";
        List<Message> messages = new ArrayList<>();

        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Message message = new Message();
                message.setMessage_id(rs.getInt("message_id"));
                message.setPosted_by(rs.getInt("posted_by"));
                message.setMessage_text(rs.getString("message_text"));
                message.setTime_posted_epoch(rs.getLong("time_posted_epoch"));
                messages.add(message);
            }
        }
        return messages;
    }

     // Retrieve a specific message by message_id
     public Message getMessageById(int messageId) throws SQLException {
        String sql = "SELECT * FROM Message WHERE message_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, messageId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Message message = new Message();
                message.setMessage_id(rs.getInt("message_id"));
                message.setPosted_by(rs.getInt("posted_by"));
                message.setMessage_text(rs.getString("message_text"));
                message.setTime_posted_epoch(rs.getLong("time_posted_epoch"));
                return message;
            }
        }
        return null; // Return null if the message is not found
    }

    // Delete a message by its message_id and return the deleted message if it existed
    public Message deleteMessageById(int messageId) throws SQLException {
        String selectSql = "SELECT * FROM Message WHERE message_id = ?";
        String deleteSql = "DELETE FROM Message WHERE message_id = ?";
        Message deletedMessage = null;

        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement selectPs = connection.prepareStatement(selectSql);
             PreparedStatement deletePs = connection.prepareStatement(deleteSql)) {

            // Check if the message exists
            selectPs.setInt(1, messageId);
            ResultSet rs = selectPs.executeQuery();
            if (rs.next()) {
                // Map the existing message
                deletedMessage = new Message();
                deletedMessage.setMessage_id(rs.getInt("message_id"));
                deletedMessage.setPosted_by(rs.getInt("posted_by"));
                deletedMessage.setMessage_text(rs.getString("message_text"));
                deletedMessage.setTime_posted_epoch(rs.getLong("time_posted_epoch"));

                // Delete the message
                deletePs.setInt(1, messageId);
                deletePs.executeUpdate();
            }
        }
        return deletedMessage; // Return the deleted message or null if it didn't exist
    }

     // Update the message text for a specific message_id
     public Message updateMessageText(int messageId, String newMessageText) throws SQLException {
        System.out.println("yo");
        String selectSql = "SELECT * FROM Message WHERE message_id = ?";
        String updateSql = "UPDATE Message SET message_text = ? WHERE message_id = ?";
        Message updatedMessage = null;

        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement selectPs = connection.prepareStatement(selectSql);
             PreparedStatement updatePs = connection.prepareStatement(updateSql)) {

            // Check if the message exists
            selectPs.setInt(1, messageId);
            ResultSet rs = selectPs.executeQuery();
            if (rs.next()) {
                // Update the message_text
                updatePs.setString(1, newMessageText);
                updatePs.setInt(2, messageId);
                updatePs.executeUpdate();

                // Map the updated message
                updatedMessage = new Message();
                updatedMessage.setMessage_id(rs.getInt("message_id"));
                updatedMessage.setPosted_by(rs.getInt("posted_by"));
                updatedMessage.setMessage_text(newMessageText);
                updatedMessage.setTime_posted_epoch(rs.getLong("time_posted_epoch"));
            }
        }
        return updatedMessage; // Return the updated message or null if it didn't exist
    }

    // Retrieve all messages posted by a specific user
    public List<Message> getMessagesByAccountId(int accountId) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM Message WHERE posted_by = ?";

        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Message message = new Message();
                message.setMessage_id(rs.getInt("message_id"));
                message.setPosted_by(rs.getInt("posted_by"));
                message.setMessage_text(rs.getString("message_text"));
                message.setTime_posted_epoch(rs.getLong("time_posted_epoch"));
                messages.add(message);
            }
        }
        return messages; // Returns empty list if no messages are found
    }
}
