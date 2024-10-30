package Service;

import DAO.AccountDAO;
import DAO.MessageDAO;
import Model.Message;
import java.sql.*;
import java.util.*;

public class MessageService {

    private MessageDAO messageDAO;
    private AccountDAO accountDAO;

    public MessageService(MessageDAO messageDAO, AccountDAO accountDAO) {
        this.messageDAO = messageDAO;
        this.accountDAO = accountDAO;
    }

    public Message createMessage(int posted_by, String message_text, long time_posted_epoch) throws SQLException {
        if (message_text == null || message_text.isEmpty()) {
            throw new IllegalArgumentException("messagetext cannot be empty");
        }
        if (message_text.length() > 255) {
            throw new IllegalArgumentException("Message text cannot be longer than 255 characters.");
        }
        if (accountDAO.findById(posted_by) == null) {
            throw new IllegalArgumentException("User not found.");
        }
        Message message = new Message();
        message.setPosted_by(posted_by);
        message.setMessage_text(message_text);
        message.setTime_posted_epoch(time_posted_epoch);
        return messageDAO.createMessage(message);
    }

    public List<Message> getAllMessages() throws SQLException {
        return messageDAO.retrieveAllMessages();
    }

    public Message getMessageById(int messageId) throws SQLException {
        return messageDAO.retrieveById(messageId);
    }

    public List<Message> getMessagesByAccountId(int accountId) throws SQLException {
        return messageDAO.retrieveByAccountId(accountId);
    }

    public boolean deleteMessageById(int messageId) throws SQLException {
        return messageDAO.deleteById(messageId);
    }

    public boolean updateMessage(int messageId, String newText) throws SQLException {
        if (newText == null || newText.trim().isEmpty()) {
            throw new IllegalArgumentException("Message text cannot be empty");
        }
        
        if (newText.length() > 255) {
            throw new IllegalArgumentException("Message text is too long");
        }
        return messageDAO.updateMessageById(messageId, newText);
    }
}