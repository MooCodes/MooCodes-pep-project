package Controller;

import java.util.List;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    private AccountService accountService = new AccountService();
    private MessageService messageService = new MessageService();
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.get("example-endpoint", this::exampleHandler);

        // Route for POST /register
        app.post("/register", this::register);

        // Route for POST /login
        app.post("/login", this::login);

        // Route for POST /messages
        app.post("/messages", this::createMessage);

        // Route for GET /messages
        app.get("/messages", this::getAllMessages);

        // Route for GET /messages/{message_id}
        app.get("/messages/{message_id}", this::getMessageById);

        // Route for DELETE /messages/{message_id}
        app.delete("/messages/{message_id}", this::deleteMessageById);

        // Route for PATCH /messages/{message_id}
        app.patch("/messages/{message_id}", this::updateMessageText);

        // Route for GET /accounts/{account_id}/messages
        app.get("/accounts/{account_id}/messages", this::getMessagesByAccountId);

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("sample text");
    }

    // Handler for the registration endpoint
    public void register(Context ctx) {
        System.out.println("yo");
        try {
            // Parse JSON body into Account object
            Account account = ctx.bodyAsClass(Account.class);

            System.out.println(account.getUsername());
            System.out.println(account.getPassword());

            // Call the service layer to register the account
            Account createdAccount = accountService.registerAccount(account.getUsername(), account.getPassword());

            // If account creation is successful, return the account with the account_id
            if (createdAccount != null) {
                ctx.status(200).json(createdAccount);
            } else {
                ctx.status(400).result("Failed to create account.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Bad input");
            ctx.status(400); // Bad input (validation errors)
        } catch (Exception e) {
            ctx.status(500).result("An unexpected error occurred.");
            e.printStackTrace();
        }
    }

        // Handler for the login endpoint
    public void login(Context ctx) {
        try {
            // Parse JSON body into Account object
            Account account = ctx.bodyAsClass(Account.class);

            System.out.println(account.getUsername());
            System.out.println(account.getPassword());

            // Call the service layer to verify login
            Account authenticatedAccount = accountService.login(account.getUsername(), account.getPassword());

            // If login is successful, return the account with the account_id
            ctx.status(200).json(authenticatedAccount);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result();
        } catch (SecurityException e) {
            ctx.status(401).result();
        } catch (Exception e) {
            ctx.status(500).result("An unexpected error occurred.");
            e.printStackTrace();
        }
    }

    // Handler for creating a message
    public void createMessage(Context ctx) {
        try {
            // Parse JSON body into Message object
            Message message = ctx.bodyAsClass(Message.class);

            // Call the service layer to create the message
            Message createdMessage = messageService.createMessage(message);

            // If successful, return the created message with its message_id
            ctx.status(200).json(createdMessage);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result(); // Client error (e.g., validation failure)
        } catch (Exception e) {
            ctx.status(500).result("An unexpected error occurred.");
            e.printStackTrace();
        }
    }

    // Handler for retrieving all messages
    public void getAllMessages(Context ctx) {
        try {
            // Call the service layer to retrieve all messages
            List<Message> messages = messageService.getAllMessages();

            // Return the list of messages (even if empty)
            ctx.status(200).json(messages);
        } catch (Exception e) {
            ctx.status(500).result("An unexpected error occurred.");
            e.printStackTrace();
        }
    }

     // Handler for retrieving a specific message by ID
     public void getMessageById(Context ctx) {
        try {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));

            // Call the service layer to get the message
            Message message = messageService.getMessageById(messageId);

            if (message != null) {
                ctx.status(200).json(message); // Return the message if found
            } else {
                ctx.status(200).json(""); // Return empty if not found
            }
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid message_id format.");
        } catch (Exception e) {
            ctx.status(500).result("An unexpected error occurred.");
            e.printStackTrace();
        }
    }

     // Handler for deleting a message by ID
     public void deleteMessageById(Context ctx) {
        try {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));

            // Call the service layer to delete the message
            Message deletedMessage = messageService.deleteMessageById(messageId);

            if (deletedMessage != null) {
                ctx.status(200).json(deletedMessage); // Return the deleted message
            } else {
                ctx.status(200).json(""); // Return empty if the message didn't exist
            }
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid message_id format.");
        } catch (Exception e) {
            ctx.status(500).result("An unexpected error occurred.");
            e.printStackTrace();
        }
    }

     // Handler for updating the message text by ID
     public void updateMessageText(Context ctx) {
        try {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            String newMessageText = ctx.bodyAsClass(Message.class).getMessage_text();

            // Call the service layer to update the message
            Message updatedMessage = messageService.updateMessageText(messageId, newMessageText);

            if (updatedMessage != null) {
                ctx.status(200).json(updatedMessage); // Return the updated message
            } else {
                ctx.status(400).result();
            }
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid message_id format.");
        } catch (IllegalArgumentException e) {
            ctx.status(400).result();
        } catch (Exception e) {
            ctx.status(500).result("An unexpected error occurred.");
            e.printStackTrace();
        }
    }

     // Handler for retrieving messages by account_id
     public void getMessagesByAccountId(Context ctx) {
        try {
            int accountId = Integer.parseInt(ctx.pathParam("account_id"));

            // Call the service layer to retrieve messages
            List<Message> messages = messageService.getMessagesByAccountId(accountId);

            ctx.status(200).json(messages); // Return the list of messages (empty if none found)
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid account_id format.");
        } catch (Exception e) {
            ctx.status(500).result("An unexpected error occurred.");
            e.printStackTrace();
        }
    }
}