package server.models;

import common.Token;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ToDoServer {

    // Fields
    private ServerSocket serverSocket;
    private final int PORT = 51234;
    private boolean isActive;
    private final ArrayList<Thread> clientThreads;
    private final ArrayList<Token> activeTokens;

    // Constructor
    public ToDoServer() {
        this.createServerSocket();
        this.isActive = false;
        this.clientThreads = new ArrayList<>();
        this.activeTokens = new ArrayList<>();
        System.out.println("[SERVER] New ToDoServer created.");
    }

    // Run method
    public void listen() {

        // Listens for new incoming connections as long as isActive is true
        try {
            Socket clientSocket = this.serverSocket.accept();

            // Create new thread out of clientRunnable & append it to the threadList
            ServerRunnable serverRunnable = new ServerRunnable(clientSocket, this);
            Thread clientThread = new Thread(serverRunnable);
            this.clientThreads.add(clientThread);

            clientThread.setDaemon(true);
            clientThread.start();


        } catch (Exception e) {
            // "Crashes" the listening method on any exception
            this.isActive = false;
            System.out.println(e.getMessage());
        }


    }


    // ServerSocket setup method - outsourced as helper method to keep constructor clear
    private void createServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.PORT);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    // Cleanup method for server shutdown
    public void cleanUp() {
        for(Thread clientThread : this.clientThreads) {
            clientThread.interrupt();
        }
    }


    // Methods to work with the token list
    public synchronized void insertToken(Token token) {
        this.activeTokens.add(token);
        System.out.println("[SERVER] Token added.");
    }

    public synchronized void deleteToken(String tokenString) {
        Token token = this.getToken(tokenString);
        this.activeTokens.remove(token);
        System.out.println("[SERVER] Token deleted.");
    }

    public synchronized Token getToken(String tokenString) {
        List<Token> tokenList = this.activeTokens
                .stream()
                .filter(t -> t.getTokenString().equals(tokenString))
                .collect(Collectors.toList());
        Token token = tokenList.get(0);
        return token;
    }

    // Getter & Setter
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

}
