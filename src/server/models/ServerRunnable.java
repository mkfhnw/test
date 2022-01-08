package server.models;


import common.HashDigest;
import common.Message;
import common.Token;
import server.services.DatabaseManager;
import server.services.InputValidator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/* The ClientRunnable class
 * The ClientRunnable is a class that implements the Runnable interface.
 * An instance of this class gets created each time a client connects to the serverSocket, running on the main
 * thread of the server. The purpose of this class therefore is to manage each incoming client connection to the server
 * as a separate thread. It represents the connection between the server and the client.
 */
public class ServerRunnable implements Runnable {

    // Fields
    private final Socket clientSocket;
    private final BufferedReader inputReader;
    private final PrintWriter outputWriter;
    private final String falseResult = "Result|false\n";
    private final String trueResult = "Result|true\n";
    private final String trueResultWithoutNewline = "Result|true|";
    private final ToDoServer parent;
    private final List<String> categories = Arrays.asList("Geplant", "Wichtig", "Erledigt");

    // Constructor
    public ServerRunnable(Socket clientSocket, ToDoServer parent) {

        this.clientSocket = clientSocket;
        this.inputReader = this.getInputReader(this.clientSocket);
        this.outputWriter = this.getOutputWriter(this.clientSocket);
        this.parent = parent;

        System.out.println("[SERVER-RUNNABLE] Client connected: "
                + clientSocket.getInetAddress().getHostAddress()
                + ":" + clientSocket.getPort());
    }

    // Private helper methods to keep constructor clean of try/catch-clauses
    private BufferedReader getInputReader(Socket clientSocket) {
        try {
            return new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private PrintWriter getOutputWriter(Socket clientSocket) {
        try {
            return new PrintWriter(clientSocket.getOutputStream());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private void sendMessage(String message) {
        try {
            this.outputWriter.write(message);
            this.outputWriter.flush();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /* The run method
     * On a new connection, the serverSocket creates a new thread using this runnable. As soon as the thread gets
     * started, this run method gets called. Inside this run method, we listen for calls from the client until the
     * client sends the LOGOUT-message type. On reception of the LOGOUT-message, the socket gets closed and the runnable
     * comes to an end - which means the above thread closes as well.
     */
    @Override
    public void run() {

        boolean shouldRun = true;

        while (shouldRun) {

            try {
                System.out.println("[SERVER-RUNNABLE] Listening for messages...");

                // Build string from sent message
                String inputString = this.inputReader.readLine();
                System.out.println("[SERVER-RUNNABLE] Received message: " + inputString);

                // Parse messageString to a "message"
                Message clientMessage = null;
                try {
                    clientMessage = new Message(inputString);
                } catch (Exception e) {
                    System.out.println("[SERVER-RUNNABLE] Exception building message @101: " + e.getMessage());
                    this.sendMessage(this.falseResult);
                }

                switch (Objects.requireNonNull(clientMessage).getMessageType()) {

                    // Perform action based on messageType
                    case LOGIN -> {
                        System.out.println("[SERVER-RUNNABLE] Reacting to LOGIN...");
                        this.reactToLogin(clientMessage);
                    }
                    
                    case LOGOUT -> {
                        System.out.println("[SERVER-RUNNABLE] Reacting to LOGOUT...");
                    	this.reactToLogout(clientMessage);

                        System.out.println("[SERVER-RUNNABLE] User logged out, shutting down socket connection.");
                        shouldRun = false;
                        this.clientSocket.close();
                    }
                    
                    case CREATELOGIN -> {
                        System.out.println("[SERVER-RUNNABLE] Reacting to CREATE_LOGIN...");
                    	this.reactToCreateLogin(clientMessage);
                    }
                    
                    case CREATETODO -> {
                        System.out.println("[SERVER-RUNNABLE] Reacting to CREATE_TODO...");
                    	this.reactToCreateToDo(clientMessage);
                    }
                    
                    case CHANGEPASSWORD -> {
                        System.out.println("[SERVER-RUNNABLE] Reacting to CHANGE_PASSWORD...");
                    	this.reactToChangePassword(clientMessage);
                    }
                    
                    case GETTODO -> {
                        System.out.println("[SERVER-RUNNABLE] Reacting to GET_TODO...");
                    	this.reactToGetToDo(clientMessage);
                    }
                    
                    case DELETETODO -> {
                        System.out.println("[SERVER-RUNNABLE] Reacting to DELETE_TODO...");
                    	this.reactToDeleteToDo(clientMessage);
                    }
                    
                    case LISTTODOS -> {
                        System.out.println("[SERVER-RUNNABLE] Reacting to LIST_TODOS...");
                    	this.reactToListToDos(clientMessage);
                    }
                    
                    case PING -> {
                        System.out.println("[SERVER-RUNNABLE] Reacting to PING...");
                    	this.reactToPing(clientMessage);
                    }

                }

                // Make the loop lay back for a bit - your CPU says "Thank you!"
                Thread.sleep(1000);

            } catch (Exception e) {
                System.out.println("[SERVER-RUNNABLE] EXCEPTION: " + e.getMessage());
                System.out.println("[SERVER-RUNNABLE] EXCEPTION: " + Arrays.toString(e.getStackTrace()));
                if(this.clientSocket.isClosed()) { shouldRun = false; }

                // Socket gets closed
                try {
                    this.clientSocket.close();
                } catch (IOException ex) {
                    System.out.println("[SERVER-RUNNABLE] EXCEPTION: " + ex.getMessage());
                }
                
            }

        }


    }

    // Reception methods based on the input of the client
	private void reactToLogin(Message clientMessage) {

        // Grab data - username first, then password
        String userEmail = this.getUsername(clientMessage);
        String username = userEmail.replace("@", "-");
        String password = this.getPassword(clientMessage);
        String hashedUsername = new HashDigest(username).getDigest();

        boolean usernameIsValid = false;
        boolean passwordIsValid = false;
        boolean userDoesAlreadyExist = false;

        // Validate user input
        try {
            InputValidator inputValidator = InputValidator.getInputValidator();
            usernameIsValid = inputValidator.validateUsername(userEmail);
            passwordIsValid = inputValidator.validatePassword(password);
            userDoesAlreadyExist = DatabaseManager.doesDatabaseExist(hashedUsername);
        } catch (Exception e) {
            System.out.println("[SERVER-RUNNABLE] Exception building message @199: " + e.getMessage());
            System.out.println("[SERVER-RUNNABLE] Exception building message @199: Probably invalid user input");
            this.sendMessage(this.falseResult);
            return;
        }

        // Return false if any of the checks above failed
        if(!usernameIsValid || !passwordIsValid || !userDoesAlreadyExist) {
            this.sendMessage(this.falseResult);
            return;
        }

        // See if password matches
        DatabaseManager databaseManager = new DatabaseManager(hashedUsername);
        String storedPassword = databaseManager.getPassword();
        String hashedPasswordInput = new HashDigest(password).getDigest();

        if(storedPassword.equals(hashedPasswordInput)) {

            // If all checks passed - create a token
            Token token = new Token();

            // Assign the token to the user
            token.setUser(username);

            // Add token to the active token list
            this.parent.insertToken(token);

            // Send token back to user
            this.sendMessage(this.trueResultWithoutNewline + token.getTokenString() + "\n");

            return;
        }

        // If we reach this line, return false since the password was not correct
        this.sendMessage(this.falseResult);

    }
	
	private void reactToLogout(Message clientMessage) {

        // Just send trueResult if no token is provided
        if(clientMessage.getToken().equals("0")) { this.sendMessage(this.trueResult); }

        // If a token is provided, delete it from the tokenList on the server parent class
        if(!clientMessage.getToken().equals("0")) {
            this.sendMessage(this.trueResult);
            this.parent.deleteToken(clientMessage.getToken());
        }

		
	}
    
    private void reactToCreateLogin(Message clientMessage) {

        // Grab data - username first, then password
        String userEmail = this.getUsername(clientMessage);
        String username = userEmail.replace("@", "-");
        String password = this.getPassword(clientMessage);
        String hashedUsername = new HashDigest(username).getDigest();


        // Validate user input
        InputValidator inputValidator = InputValidator.getInputValidator();
        boolean usernameIsValid = inputValidator.validateUsername(userEmail);
        boolean passwordIsValid = inputValidator.validatePassword(password);
        boolean userDoesAlreadyExist = DatabaseManager.doesDatabaseExist(hashedUsername);

        // Create new database and store login credentials for the user if input is valid
        if(usernameIsValid && passwordIsValid && !userDoesAlreadyExist) {
            DatabaseManager databaseManager = new DatabaseManager(hashedUsername);
            databaseManager.initializeDatabase();
            databaseManager.storeLoginCredentials(username, password);
            this.sendMessage(this.trueResult);
        }

        // Send response
        if(!usernameIsValid || !passwordIsValid || userDoesAlreadyExist) {
            this.sendMessage(this.falseResult);
        }

	}

    private void reactToChangePassword(Message clientMessage) {

        // Parse out token
        String tokenString = clientMessage.getToken();
        Token token = this.parent.getToken(tokenString);

        // If token is invalid, send negative response
        InputValidator inputValidator = InputValidator.getInputValidator();
        if(!inputValidator.isTokenStillAlive(token)) {
            this.sendMessage(this.falseResult);
            return;
        }

        // If token is valid, go ahead
        if(inputValidator.isTokenStillAlive(token)) {

            // Parse username & validate newPassword input
            String username = token.getUser().replace("@", "-");
            String hashedUsername = new HashDigest(username).getDigest();

            // We can't use the private .getPassword-method here since the newPassword is at index 0 in this case
            String newPassword = clientMessage.getDataParts().get(0);

            // Validate input, make changes if input is valid and return false otherwise
            boolean passwordIsValid = inputValidator.validatePassword(newPassword);
            if(passwordIsValid) {
                // Create new instance of databaseManager and write the newPassword to the DB
                DatabaseManager databaseManager = new DatabaseManager(hashedUsername);
                databaseManager.changePassword(newPassword);
                this.sendMessage(this.trueResult);
            } else { this.sendMessage(this.falseResult); }

        }

    }

    private void reactToCreateToDo(Message clientMessage) {
        // Parse out token
        String tokenString = clientMessage.getToken();
        Token token = this.parent.getToken(tokenString);

        // If token is invalid, send negative response
        InputValidator inputValidator = InputValidator.getInputValidator();
        if(!inputValidator.isTokenStillAlive(token)) {
            this.sendMessage(this.falseResult);
            return;
        }

        // If token is valid, go ahead
        if (inputValidator.isTokenStillAlive(token)) {

            // Setup db-manager
            String username = token.getUser().replace("@", "-");
            String hashedUsername = new HashDigest(username).getDigest();
            DatabaseManager databaseManager = new DatabaseManager(hashedUsername);

            // Parse message - we always have title & priority at the same spot
            String title = clientMessage.getDataParts().get(0);
            String priority = clientMessage.getDataParts().get(1);
            String description = null;
            String dueDate = null;
            String category = null;

            // Data parts length is always between 2 (inc.) and 5 (inc.)
            if (clientMessage.getDataParts().size() == 2) {
                // No missing parameters, only title & priority
                int itemID = databaseManager.createItem(title, priority);
                this.sendMessage(this.trueResultWithoutNewline + itemID + "\n");
                return;
            }

            // Loop through data parts and figure out ambiguous parameters
            if (clientMessage.getDataParts().size() >= 3) {
                int dataPartsLength = clientMessage.getDataParts().size();
                for (String ambiguousParameter : clientMessage.getDataParts().subList(2, dataPartsLength)) {
                    String parameterType = inputValidator.getParameterType(ambiguousParameter);
                    switch (parameterType) {

                        case "Category" -> {
                            category = ambiguousParameter;
                        }
                        case "Description" -> {
                            description = ambiguousParameter;
                        }
                        case "DueDate" -> {
                            dueDate = ambiguousParameter;
                        }
                        case "Undefined" -> {
                            this.sendMessage(falseResult);
                            return;
                        }
                    }
                }


                // Writing to the database

                // Title & Priority only
                if(title != null && priority != null && description == null && dueDate == null && category == null) {
                    int itemID = databaseManager.createItem(title, priority);
                    this.sendMessage(this.trueResultWithoutNewline + itemID + "\n");
                    return;
                }

                // Title, Priority & Description
                if(title != null && priority != null && description != null && dueDate == null && category == null) {
                    int itemID = databaseManager.createItem(title, priority, description);
                    this.sendMessage(this.trueResultWithoutNewline + itemID + "\n");
                    return;
                }

                // Title, Priority & DueDate
                if(title != null && priority != null && description == null && dueDate != null && category == null) {
                    int itemID = databaseManager.createItem(title, priority, dueDate);
                    this.sendMessage(this.trueResultWithoutNewline + itemID + "\n");
                    return;
                }

                // title, Priority & Category
                if(title != null && priority != null && description == null && dueDate == null && category != null) {
                    int itemID = databaseManager.createItem(title, priority, category);
                    this.sendMessage(this.trueResultWithoutNewline + itemID + "\n");
                    return;
                }

                // Title, Priority, Description & DueDate
                if(title != null && priority != null && description != null && dueDate != null && category == null) {
                    int itemID = databaseManager.createItem(title, priority, description, dueDate);
                    this.sendMessage(this.trueResultWithoutNewline + itemID + "\n");
                    return;
                }

                // Title, Priority, Description & Category
                if(title != null && priority != null && description != null && dueDate == null && category != null) {
                    int itemID = databaseManager.createItem(title, priority, description, category);
                    this.sendMessage(this.trueResultWithoutNewline + itemID + "\n");
                    return;
                }

                // Title, Priority, DueDate & Category
                if(title != null && priority != null && description == null && dueDate != null && category != null) {
                    int itemID = databaseManager.createItem(title, priority, dueDate, category);
                    this.sendMessage(this.trueResultWithoutNewline + itemID + "\n");
                    return;
                }

                // All parameters
                if(title != null && priority != null && description != null && dueDate != null && category != null) {
                    int itemID = databaseManager.createItem(title, priority, description, dueDate, category);
                    this.sendMessage(this.trueResultWithoutNewline + itemID + "\n");
                }


            }
        }
    }
    
    private void reactToGetToDo(Message clientMessage) {

        // Parse out token
        String tokenString = clientMessage.getToken();
        Token token = this.parent.getToken(tokenString);
		
        // If token is invalid, send negative response
        InputValidator inputValidator = InputValidator.getInputValidator();
        if(!inputValidator.isTokenStillAlive(token)) {
            this.sendMessage(this.falseResult);
            return;
        }
        
     // If token is valid, go ahead
        if(inputValidator.isTokenStillAlive(token)) {

            // Parse username
            String username = token.getUser().replace("@", "-");
            String hashedUsername = new HashDigest(username).getDigest();

            // Create DatabaseManager
            DatabaseManager databaseManager = new DatabaseManager(hashedUsername);
            ArrayList<String> itemData = databaseManager.getToDo(clientMessage.getDataParts().get(0));

            // If itemData is empty, the item in question does not exist - return false
            if(itemData.size() == 0) {
                this.sendMessage(this.falseResult);
                return;
            }

            // Otherwise, create response
            Message message = new Message(itemData);
            this.sendMessage(message.getMessageString());
        }

	}
    
    private void reactToDeleteToDo(Message clientMessage) {
    	
    	// Parse out token
        String tokenString = clientMessage.getToken();
        Token token = this.parent.getToken(tokenString);
        
        // If token is invalid, send negative response
        InputValidator inputValidator = InputValidator.getInputValidator();
        if(!inputValidator.isTokenStillAlive(token)) {
            this.sendMessage(this.falseResult);
            return;
        }
        
        // If token is valid, go ahead
        if(inputValidator.isTokenStillAlive(token)) {

            // Parse username
            String username = token.getUser().replace("@", "-");
            String hashedUsername = new HashDigest(username).getDigest();
            
            // Create database manager
            DatabaseManager databaseManager = new DatabaseManager(hashedUsername);
            
            //Parse out item ID to delete
            String ID = clientMessage.getDataParts().get(0);
            databaseManager.deleteItem(ID);

            // Send response
            this.sendMessage(this.trueResult);
            return;

        }

        // If we reach this line, something failed
        this.sendMessage(this.falseResult);

	}
    
    private void reactToListToDos(Message clientMessage) {
    	
    	// Parse out token
        String tokenString = clientMessage.getToken();
        Token token = this.parent.getToken(tokenString);
        
        // If token is invalid, send negative response
        InputValidator inputValidator = InputValidator.getInputValidator();
        if(!inputValidator.isTokenStillAlive(token)) {
            this.sendMessage(this.falseResult);
            return;
        }
        
        // If token is valid, go ahead
        if(inputValidator.isTokenStillAlive(token)) {

            // Parse username
            String username = token.getUser().replace("@", "-");
            String hashedUsername = new HashDigest(username).getDigest();

            // Grab all ToDo_IDs from database
            DatabaseManager databaseManager = new DatabaseManager(hashedUsername);
            ArrayList<String> data = databaseManager.listToDos();

            // Forge mesasge
            Message response = new Message(data);

            // Adjust for "null" in message string
            if(response.getMessageString().contains("null")) {
                this.sendMessage(this.trueResult);
                return;
            }

            // Send response
            this.sendMessage(response.getMessageString());
            return;
        }

        // If we reach this line, something went wrong
		this.sendMessage(this.falseResult);
	}
    
    private void reactToPing(Message clientMessage) {
    	
    	// Parse out token
        String tokenString = clientMessage.getToken();

        // See if a token was sent with
        Token token = null;

        if(!tokenString.equals("0")) {
            try {
                token = this.parent.getToken(tokenString);
            } catch (Exception e) {
                // If token was invalid, send false
                this.sendMessage(falseResult);
                return;
            }
        }

        // Check if token is not null
        if(token != null) {
            // If token is invalid, send negative response
            InputValidator inputValidator = InputValidator.getInputValidator();
            if(!inputValidator.isTokenStillAlive(token)) { this.sendMessage(this.falseResult); return; }
        }

        // If no token is provided or the token is valid - always send a true result
        this.sendMessage(this.trueResult);
		
	}

    // Helper methods
    private String getUsername(Message clientMessage) {
        return clientMessage.getDataParts().get(0);
    }

    private String getPassword(Message clientMessage) {
        return clientMessage.getDataParts().get(1);
    }

}