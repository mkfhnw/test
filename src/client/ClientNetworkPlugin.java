package client;

import client.model.ToDo;
import common.Message;
import server.models.ServerRunnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;


public class ClientNetworkPlugin {

    // Fields
    // Uncomment & adjust to connect to other servers
    // private final int PORT = 50002;
    // private final String IP = "147.86.8.31";

    // Uncomment if connecting client to our own server
	private final int PORT = 51234;
	private final String IP = "localhost";
    
    private Socket clientSocket;
    private BufferedReader inputReader;
    private PrintWriter outputWriter;
    private String token;
    private final String sender = "Client";
    private final String recipient = "Server";
    private ServerRunnable serverRunnable;
    private boolean isConnectedToPrivateServer;

    // Constructor
    public ClientNetworkPlugin() {
        this.isConnectedToPrivateServer = false;
        System.out.println("[CLIENT] New ToDoClient created.");
    }

    // Public method to connect to server
    public void connect() {
        try {
            this.clientSocket = new Socket(this.IP, this.PORT);
            this.inputReader = this.getInputReader(this.clientSocket);
            this.outputWriter = this.getOutputWriter(this.clientSocket);

            // Check if connected to mother server
            if (this.PORT == 51234) {
                this.isConnectedToPrivateServer = true;
            }

            System.out.println("[CLIENT] ToDo-Client connected to server");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void disconnect() {
        try {
            this.clientSocket.close();
        } catch (Exception e) {
            System.out.println("[CLIENT] Cannot close socket.");
        }
    }

    public boolean isConnected() {
        return this.clientSocket != null && this.clientSocket.isConnected();
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

    // Message sending method
    public void sendMessage(String command, ArrayList<String> data, String token) {

        // Create cient message based on input
        Message clientMessage = new Message(this.sender, this.recipient, this.token, command, data);

        // Send message
        this.outputWriter.write(clientMessage.getMessageString());
        this.outputWriter.flush();
        System.out.println("[CLIENT] Sent message: " + clientMessage.getMessageString());
    }

    // Message sending method for login and ping
    public void sendMessage(String command, ArrayList<String> data) {

        // Create cient message based on input
        Message clientMessage = new Message(this.sender, this.recipient, command, data);

        // Check for ping edge-case
        if (clientMessage.getMessageString().equals("Ping|null\n")) {
            this.outputWriter.write("Ping\n");
            this.outputWriter.flush();
            System.out.println("[CLIENT] Sent message: Ping");
            return;
        }

        // Send message
        this.outputWriter.write(clientMessage.getMessageString());
        this.outputWriter.flush();
        System.out.println("[CLIENT] Sent message: " + clientMessage.getMessageString());
    }

    // Message reading method
    public Message parseResponse() {

        try {
            // Parse the server response
            String messageString = this.inputReader.readLine();
            System.out.println("[CLIENT] Received message: " + messageString);

            // Create Message
            Message message = new Message(messageString);
            return message;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }


    }

    public boolean login(String emailLogin, String passwordLogin) {

        boolean result = false;

        try {
            ArrayList<String> loginData = new ArrayList<>();
            loginData.add(emailLogin);
            loginData.add(passwordLogin);

            sendMessage("LOGIN", loginData);

            // Receive server response case
            Message responseLogin = this.parseResponse();

            // Parse response result
            result = Boolean.parseBoolean(responseLogin.getMessageParts().get(1));


            if (result) {
                // set token
                this.token = responseLogin.getToken();
            } else {
                result = false;
            }

        } catch (Exception e) {
            System.out.println("[NETWORK-PLUGIN] Exception: " + e.getMessage());
            result = false;
        }

        return result;

    }

    public boolean logout() {

        boolean result = false;

        try {

            ArrayList<String> logoutData = new ArrayList<>();

            sendMessage("LOGOUT", logoutData, this.token);

            // Receive server response case
            Message responseLogin = this.parseResponse();

            // Parse response result
            result = Boolean.parseBoolean(responseLogin.getMessageParts().get(1));

            // Throw away token
            this.token = null;

            System.out.println("[NETWORK-PLUGIN] User logged out, shutting down socket connection.");
            // this.clientSocket.close();

        } catch (Exception e) {
            System.out.println("[NETWORK-PLUGIN] Exception: " + e.getMessage());
            result = false;
        }

        return result;
    }

    public boolean createLogin(String emailCreateLogin, String passwordCreateLogin) {

        boolean result = false;


        try {
            ArrayList<String> createLoginData = new ArrayList<>();
            createLoginData.add(emailCreateLogin);
            createLoginData.add(passwordCreateLogin);

            sendMessage("CREATELOGIN", createLoginData);

            // Receive server response case
            Message responseLogin = this.parseResponse();

            // Parse response result
            result = Boolean.parseBoolean(responseLogin.getMessageParts().get(1));


        } catch (Exception e) {
            System.out.println("[NETWORK-PLUGIN] Exception: " + e.getMessage());
            result = false;
        }


        // Fails if name already taken, or invalid --> SERVER
        // After creating an account, you still have to login --> CONTROLLER

        return result;
    }

    public int createToDo(ToDo toDo) {

        boolean result = false;

        try {
            ArrayList<String> createToDoData = new ArrayList<>();
            createToDoData.add(toDo.getTitle());
            createToDoData.add(toDo.getPriority().toString());
            if (toDo.getMessage().equals("") || toDo.getMessage() == null) {
                createToDoData.add("N/A");
            } else {
                createToDoData.add(toDo.getMessage());
            }
            if (toDo.getDueDate() != null) {
                createToDoData.add(toDo.getDueDate().toString());
            }
            if (toDo.getCategory() != null && !toDo.getCategory().equals("") && !toDo.getCategory().equals(" ")) {
                createToDoData.add(toDo.getCategory());
            }

            sendMessage("CREATETODO", createToDoData, this.token);

            // Receive server response case
            Message responseLogin = this.parseResponse();

            // Parse response result
            result = Boolean.parseBoolean(responseLogin.getMessageParts().get(1));

            if (result) {
                return Integer.parseInt(responseLogin.getMessageParts().get(2));
            }


        } catch (Exception e) {
            System.out.println("[NETWORK-PLUGIN] Exception: " + e.getMessage());

        }

        return -1;
    }

    public boolean changePassword(String newPassword) {

        boolean result = false;

        try {
            ArrayList<String> changePasswordData = new ArrayList<>();
            changePasswordData.add(newPassword);

            // change password --> CONTROLLER
            // Token valid (SERVER))

            sendMessage("CHANGEPASSWORD", changePasswordData, this.token);

            // Receive server response case
            Message responseLogin = this.parseResponse();

            // Parse response result
            result = Boolean.parseBoolean(responseLogin.getMessageParts().get(1));


        } catch (Exception e) {
            System.out.println("[NETWORK-PLUGIN] Exception: " + e.getMessage());
            result = false;
        }
        return result;
    }

    public ArrayList<String> getToDo(int ID) {


        try {
            ArrayList<String> toDoData = new ArrayList<>();
            toDoData.add(Integer.toString(ID));

            sendMessage("GETTODO", toDoData, this.token);

            // Receive server response case
            Message responseLogin = this.parseResponse();

            ArrayList<String> itemData = responseLogin.getDataParts();
            return itemData;

        } catch (Exception e) {
            System.out.println("[NETWORK-PLUGIN] Exception: " + e.getMessage());
            return null;
        }


    }

    public boolean deleteToDo(int ID) {

        boolean result = false;

        try {

            ArrayList<String> deletedToDoData = new ArrayList<>();
            deletedToDoData.add(Integer.toString(ID));

            // toDoController.deleteToDo(); --> SERVER

            // Token-Validation --> SERVER

            sendMessage("DELETETODO", deletedToDoData, this.token);

            // Receive server response case
            Message responseLogin = this.parseResponse();

            // Parse response result
            result = Boolean.parseBoolean(responseLogin.getMessageParts().get(1));


        } catch (Exception e) {
            System.out.println("[NETWORK-PLUGIN] Exception: " + e.getMessage());
            result = false;
        }

        return result;

    }

    public ArrayList<String> listToDos() {

        try {

            ArrayList<String> listToDos = new ArrayList<>();

            sendMessage("LISTTODOS", listToDos, this.token);

            // Receive server response case
            Message responseLogin = this.parseResponse();

            ArrayList<String> resultList = responseLogin.getDataParts();

            return resultList;


        } catch (Exception e) {
            System.out.println("[NETWORK-PLUGIN] Exception: " + e.getMessage());
            return null;
        }


    }

    public boolean ping() {

        boolean result = false;

        try {
            ArrayList<String> pingData = new ArrayList<>();

            /*
             * Ping can be sent with and without token,
             * therefore we check if token is not "null"
             */
            if (token != null && !token.equals("")) {
                sendMessage("PING", pingData, this.token);
            } else {
                sendMessage("PING", pingData);
            }

            // Receive server response case
            Message responseLogin = this.parseResponse();

            // Parse response result
            result = Boolean.parseBoolean(responseLogin.getMessageParts().get(1));

        } catch (Exception e) {
            System.out.println("[NETWORK-PLUGIN] Exception: " + e.getMessage());
            result = false;
        }

        return result;


    }

    public String getToken() {
        return token;
    }

    public int getPORT() {
        return PORT;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public BufferedReader getInputReader() {
        return inputReader;
    }

    public PrintWriter getOutputWriter() {
        return outputWriter;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public ServerRunnable getServerRunnable() {
        return serverRunnable;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void setInputReader(BufferedReader inputReader) {
        this.inputReader = inputReader;
    }

    public void setOutputWriter(PrintWriter outputWriter) {
        this.outputWriter = outputWriter;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setServerRunnable(ServerRunnable serverRunnable) {
        this.serverRunnable = serverRunnable;
    }

    public String getIP() {
        return IP;
    }

	public boolean isConnectedToPrivateServer() {
		return this.isConnectedToPrivateServer;
	}

}
