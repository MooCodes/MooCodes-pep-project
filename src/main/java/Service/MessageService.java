package Service;

import java.sql.*;
import java.util.List;

import DAO.MessageDAO;
import Model.Message;

public class MessageService {
    private MessageDAO messageDAO = new MessageDAO();

    // Create a new message
    public Message createMessage(Message message) throws SQLException {
        // Validate message_text
        String messageText = message.getMessage_text();
        if (messageText == null || messageText.trim().isEmpty()) {
            System.out.println("blank");
            throw new IllegalArgumentException();
        }
        if (messageText.length() > 255) {
            throw new IllegalArgumentException();
        }

        // Validate posted_by (check if the user exists)
        if (!messageDAO.doesUserExist(message.getPosted_by())) {
            throw new IllegalArgumentException();
        }

        // Persist the message to the database
        return messageDAO.createMessage(message);
    }

    // Retrieve all messages
    public List<Message> getAllMessages() throws SQLException {
        return messageDAO.getAllMessages();
    }

     // Retrieve a specific message by message_id
     public Message getMessageById(int messageId) throws SQLException {
        return messageDAO.getMessageById(messageId);
    }

     // Delete a message by its ID
     public Message deleteMessageById(int messageId) throws SQLException {
        return messageDAO.deleteMessageById(messageId);
    }

    // Update the message text for a specific message_id
    public Message updateMessageText(int messageId, String newMessageText) throws SQLException {
        // Validate the new message text
        if (newMessageText == null || newMessageText.isBlank() || newMessageText.length() > 255) {
            throw new IllegalArgumentException();
        }

        return messageDAO.updateMessageText(messageId, newMessageText);
    }

     // Get all messages posted by a specific account
     public List<Message> getMessagesByAccountId(int accountId) throws SQLException {
        // No additional business logic in this case, delegate to DAO
        return messageDAO.getMessagesByAccountId(accountId);
    }
}
