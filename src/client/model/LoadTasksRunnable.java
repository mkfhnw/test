package client.model;

import client.ClientNetworkPlugin;
import client.controller.ToDoController;
import server.services.InputValidator;

import java.util.ArrayList;

public class LoadTasksRunnable implements Runnable {

    // Fields
    private final ClientNetworkPlugin clientNetworkPlugin;
    private final String itemID;
    private final ToDoController parent;

    // Constructor
    public LoadTasksRunnable(String itemID, ClientNetworkPlugin clientNetworkPlugin, ToDoController parent) {
        this.clientNetworkPlugin = clientNetworkPlugin;
        this.itemID = itemID;
        this.parent = parent;
    }

    // Fetch each item returned
    @Override
    public void run() {
        System.out.println("[LOAD-TASKS-RUNNABLE] Started loading task " + this.itemID + " from database...");

        // Make call for the item ID
        int intID = Integer.parseInt(this.itemID);
        ArrayList<String> itemData = this.clientNetworkPlugin.getToDo(intID);
        ToDo item = this.parseItemFromMessageString(itemData);
        item.setID(Integer.parseInt(itemData.get(0)));

        // Stuff the contents of returnList into the controller
        parent.passItems(item);

    }

    // Helper method
    // Method to parse Instance of Model out of message string
    public ToDo parseItemFromMessageString(ArrayList<String> itemData) {
        ToDo returnItem = null;

        // Grab fixed components & set ambiguous components to null
        String title = itemData.get(1);
        String priority = itemData.get(2);
        String description = null;
        String dueDate = null;
        String category = null;

        // Parse dynamic parts
        InputValidator inputValidator = new InputValidator();

        // Loop through data parts and figure out ambiguous parameters
        if (itemData.size() >= 3) {
            int dataPartsLength = itemData.size();
            for (String ambiguousParameter : itemData.subList(3, dataPartsLength)) {
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
                        return null;
                    }
                }
            }
        }

        // Create new Item

        // 3 missing parameters
        if(description == null && dueDate == null && category == null) {
            return new ToDo(title, priority);
        }

        // Missing 2 parameters
        // Missing dueDate and category
        if(description != null && dueDate == null && category == null) {
            return new ToDo(title, priority, description);
        }

        // Missing description and category
        if(description == null && dueDate != null && category == null) {
            return new ToDo(title, priority, dueDate);
        }

        // Missing description and dueDate
        if(description == null && dueDate == null && category != null) {
            return new ToDo(title, priority, category);
        }

        // Missing 1 parameter
        // Missing category
        if(description != null && dueDate != null && category == null) {
            return new ToDo(title, priority, description, dueDate);
        }

        // Missing dueDate
        if(description != null && dueDate == null && category != null) {
            return new ToDo(title, priority, description, category);
        }

        // Missing description
        if(description == null && dueDate != null && category != null) {
            return new ToDo(title, priority, dueDate, category);
        }

        // No missing parameters
        returnItem = new ToDo(title, priority, description, dueDate, category);

        return returnItem;
    }

}
