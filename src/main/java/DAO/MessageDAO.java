package DAO;

import Model.Message;
import java.sql.*;
import java.util.*;


public class MessageDAO {

    private Connection conn;

    public MessageDAO(Connection connection){
        this.conn = connection;
    }

    public Message createMessage(Message message) throws SQLException {
        String sql = "INSERT INTO Message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
        try(PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){
            ps.setInt(1,message.getPosted_by());
            ps.setString(2, message.getMessage_text());
            ps.setLong(3, message.getTime_posted_epoch());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
               message.setMessage_id(rs.getInt(1));
            }
            return message;
        }
    }
    
    public List<Message> retrieveAllMessages() throws SQLException {
        String sql = "SELECT * FROM Message";
        List<Message> messages = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Message message = new Message(rs.getInt("message_id"), rs.getInt("posted_by"), 
                                               rs.getString("message_text"), rs.getLong("time_posted_epoch"));
                messages.add(message);
            }
        }
        return messages;
    }

    public Message retrieveById(int messageId) throws SQLException {
        String sql = "SELECT * FROM Message WHERE message_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) { 
            stmt.setInt(1, messageId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Message(rs.getInt("message_id"), rs.getInt("posted_by"), 
                                   rs.getString("message_text"), rs.getLong("time_posted_epoch"));
            }
            return null;
        }
    }

    public List<Message> retrieveByAccountId(int accountId) throws SQLException {
        String sql = "SELECT * FROM Message WHERE posted_by = ?";
        List<Message> messages = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Message message = new Message(rs.getInt("message_id"), rs.getInt("posted_by"),
                                               rs.getString("message_text"), rs.getLong("time_posted_epoch"));
                messages.add(message);
            }
        }
        return messages;
    }

    public boolean deleteById(int messageId) throws SQLException {
        String sql = "DELETE FROM Message WHERE message_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, messageId);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        }
    }

    public boolean updateMessageById(int messageId, String newText) throws SQLException {
        String sql = "UPDATE Message SET message_text = ? WHERE message_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newText);
            stmt.setInt(2, messageId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        }
    }
}