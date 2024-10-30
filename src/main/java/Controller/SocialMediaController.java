package Controller;

import java.sql.SQLException;

import DAO.AccountDAO;
import Model.Account;
import Model.Message;
import Service.MessageService;
import Service.AccountService;
import Util.ConnectionUtil;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;


/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    private final AccountService accountService;
    private  MessageService messageService;

    public SocialMediaController() {
        /*this.accountService = new AccountService(new DAO.AccountDAO(ConnectionUtil.getConnection()));*/
        AccountDAO accountDAO = new DAO.AccountDAO(ConnectionUtil.getConnection());
        this.accountService = new AccountService(accountDAO);
        this.messageService = new MessageService(new DAO.MessageDAO(ConnectionUtil.getConnection()), accountDAO);
    }

    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::handlingRegister);
        app.post("/login", this::handlingLogin);
        app.post("/messages", this::handlingCreateMessage);
        app.get("/messages", ctx -> {
            ctx.json(messageService.getAllMessages());
        });
        app.get("/messages/{message_id}", this::handlingretrieveMessageById);
        app.get("/accounts/{account_id}/messages", ctx -> {
            handlingMessagesByAccountId(ctx);
        });
        app.delete("/messages/{message_id}", this::handlingDeleteMessageById);
        app.patch("/messages/{message_id}", this::handlingUpdateMessageById);

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */

    private void handlingRegister(Context context) throws SQLException{
        try {
            Account account = context.bodyAsClass(Account.class);
            Account accountCreated = accountService.register(account);
            context.json(accountCreated).status(200);
        } catch (IllegalArgumentException e) {
            context.status(400).result(e.getMessage());
        }
    }

    private void handlingLogin(Context context) throws SQLException {
        try {
            Account account = context.bodyAsClass(Account.class);
            Account loggedInAccount = accountService.login(account.getUsername(), account.getPassword());
            if(loggedInAccount == null){
                context.status(401).result("");
            }
            else{
            context.json(loggedInAccount).status(200);
            }
        } catch (IllegalArgumentException e) {
            context.status(401).result("");
        }
        catch (Exception e){
            context.status(500).result("");
        }
    }

    private void handlingCreateMessage(Context context) throws SQLException {
        try {
            Message message = context.bodyAsClass(Message.class);
            Message messageCreated = messageService.createMessage(message.getPosted_by(), message.getMessage_text(), message.getTime_posted_epoch());
            context.json(messageCreated).status(200);
        }
        catch (IllegalArgumentException e){
            context.status(400).result("");
        }
        catch(Exception e){
            context.status(500).result("");
        }
    }

    private void handlingretrieveMessageById(Context context) {
        try {
            int messageId = Integer.parseInt(context.pathParam("message_id"));
            Message message = messageService.getMessageById(messageId);
    
            if (message == null) {
                context.status(200).result("");
            } else if (message.getMessage_text() == null || message.getMessage_text().isEmpty()) {
                context.status(204).result(""); 
            } else {
                context.json(message).status(200);
            }
        } catch (NumberFormatException e) {
            context.status(400).result("Invalid message ID format");
        } catch (Exception e) {
            context.status(500).result("Server error");
        }
    }

    private void handlingMessagesByAccountId(Context context) throws SQLException {
        try {
            int accountId = Integer.parseInt(context.pathParam("account_id"));
            
            List<Message> messages = messageService.getMessagesByAccountId(accountId);
    
            if (messages == null || messages.isEmpty()) {
                context.json(new ArrayList<>()).status(200);
            } 
            /*else  if(messages.isEmpty()){
                context.status(200).result("");
            }*/
            else {
                context.json(messages).status(200);
            }
        } catch (NumberFormatException e) {
            context.status(400).result("Invalid account ID format");
        } catch (Exception e) {
            context.status(500).result("Server error");
        }
    }

    private void handlingDeleteMessageById(Context context) throws SQLException {
        try {
            int messageId = Integer.parseInt(context.pathParam("message_id"));
            Message message = messageService.getMessageById(messageId); 
            if (message != null) {
                messageService.deleteMessageById(messageId);
                context.json(message).status(200);
            }
            else {
                context.status(200);
            }
        } catch (NumberFormatException e) {
            context.status(400).result("Invalid message ID format");
        } catch (Exception e) {
            context.status(500).result("Server error");
        }
    }

    private void handlingUpdateMessageById(Context context) throws SQLException {
        try {
            int messageId = Integer.parseInt(context.pathParam("message_id"));
            ObjectMapper objectMapper = new ObjectMapper();
            String body = context.body();
            Map<String, String> requestBody = objectMapper.readValue(body, new TypeReference<Map<String, String>>() {});

            String newText = requestBody.get("message_text");
            if (newText == null || newText.trim().isEmpty()) {
                context.status(400).result("");
                return;
            }
            final int MAX_LENGTH = 255;
            if (newText.length() > MAX_LENGTH) {
            context.status(400);
            return;
        }
            boolean isUpdated = messageService.updateMessage(messageId, newText);
            
            if (isUpdated) {
                Message updatedMessage = messageService.getMessageById(messageId); 
            context.json(updatedMessage).status(200);
            } if(!isUpdated){
                context.status(400);
            }
        } catch (NumberFormatException e) {
            context.status(400).result("Invalid message ID format");
        } 
        catch (Exception e) {
            context.status(400).result("An error occurred while updating the message");
        }
    }
}