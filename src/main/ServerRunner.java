package main; 

import server.models.ToDoServer;

public class ServerRunner {

    public static void main(String[] args) {

        // Create new server object
        ToDoServer toDoServer = new ToDoServer();

        // Listen for connections
        toDoServer.setActive(true);
        while (toDoServer.isActive()) {
            // If a connection fails, isActive is set to false
            toDoServer.listen();
        }

        // Clean up socket connections and threads
        if(!toDoServer.isActive()) {
            toDoServer.cleanUp();
        }

    }

}
