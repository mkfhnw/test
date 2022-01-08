package client.controller;

import client.ClientNetworkPlugin;
import client.model.*;
import client.view.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import server.services.InputValidator;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ToDoController implements Serializable {

    // Fields
    private final ToDoView toDoView;
    private final ToDo toDo;
    private final ToDoList toDoList;
    private final Stage stage;
    private final Scene scene2;
    private final Scene scene1;

    private ImportantBarView importantBarView;
    private GarbageBarView garbageBarView;
    private PlannedBarView plannedBarView;
    private DoneBarView doneBarView;
    private SearchBarView searchBarView;
    private final FocusTimerDialogPane dialog;
    private FocusTimerModel focusModel;

    private final LoginView loginView;
    private final ClientNetworkPlugin clientNetworkPlugin;
    private final ArrayList<ToDo> returnItems;
    private final ArrayList<Thread> threadPool;

    // Constructor
    public ToDoController(ToDoView toDoView, ToDo toDo, ToDoList toDoList, Stage stage, Scene scene2,
                          LoginView loginView, Scene scene1) {

        this.toDoView = toDoView;
        this.toDo = toDo;
        this.toDoList = toDoList;
        this.stage = stage;
        this.scene2 = scene2;
        this.loginView = loginView;
        this.scene1 = scene1;
        this.focusModel = focusModel;

        // Networking stuff
        this.clientNetworkPlugin = new ClientNetworkPlugin();
        this.returnItems = new ArrayList<>();
        this.threadPool = new ArrayList<>();

        // Connect to server
        if (!this.clientNetworkPlugin.isConnected()) {
            this.clientNetworkPlugin.connect();
        }

        // Change listView if not connected to private server
        if(!this.clientNetworkPlugin.isConnectedToPrivateServer()) {
            this.toDoView.getListView().getItems().clear();
            this.toDoView.getListView().getItems().add("Geplant");
        }

        // Load items from database
        this.toDoList.updateSublists();

        // Set default midPane & add initial event handling for searchbar
        this.plannedBarView = new PlannedBarView(this.toDoList.getToDoListPlanned());
        this.plannedBarView.getCreateToDo().setOnMouseClicked(this::createToDoDialog);
        this.plannedBarView.getSearchButton().setOnMouseClicked(this::searchItemAndGenerateView);
        this.plannedBarView.getSearchField().setOnKeyPressed(this::searchItemAndGenerateView);
        this.plannedBarView.getDateFilterCombobox().setOnAction(this::changeCombo);
        this.plannedBarView.getTableView().setOnMouseClicked(this::updateToDo);
        this.linkTableViewListeners(plannedBarView.getTableView().getItems());
        this.toDoView.getBorderPane().setCenter(plannedBarView);

        // Register buttons EventHandling
        if(this.clientNetworkPlugin.isConnectedToPrivateServer()) {
            this.toDoView.getListView().setOnMouseClicked(this::changeCenterBar);
        } else {
            this.toDoView.getListView().setOnMouseClicked(this::changeToPlannedBarView);
            this.plannedBarView.getDateFilterCombobox().setDisable(true);
        }

        // Focus timer button EventHandling
        this.toDoView.getOpenFocusTimer().setOnMouseClicked(this::createFocusTimer);

        // Ping Button Event Handler
        this.toDoView.getPingButton().setOnMouseClicked(this::ping);

        // Add focus timer dialog and model
        this.dialog = new FocusTimerDialogPane();
        this.focusModel = new FocusTimerModel(null);

        // HowTo Menu EventHandling
        this.toDoView.getHowToItem().setOnAction(this::createHowTo);

        // EventHandling for play, stop or replay How-To Video
        this.toDoView.getHowToDialogPane().getPlayButton().setOnMouseClicked(this::playMedia);
        this.toDoView.getHowToDialogPane().getStopButton().setOnMouseClicked(this::stopMedia);
        this.toDoView.getHowToDialogPane().getReplayButton().setOnMouseClicked(this::replayMedia);

        // EventHandling for logout
        this.toDoView.getLogoutButton().setOnMouseClicked(this::logout);

        // EventHandling for registration
        this.loginView.getRegisterButton().setOnMouseClicked(this::openRegistration);

        // EventHandling to open ToDoApp
        this.loginView.getSignInButton().setOnAction(this::handleLogin);

        // EventHandling for changing password
        this.toDoView.getChangePasswordItem().setOnAction(this::changePassword);

        // EventHandling to show Password and change Image
        this.loginView.getHiddenEyeImage().setOnMouseClicked(this::showPassword);

        // EventHandling to hide Password and change Image
        this.loginView.getEyeImage().setOnMouseClicked(this::hidePassword);

        // EventHandling CheckBox to show and hide changed password
        this.toDoView.getChangePasswordDialogPane().getShowPassword().setOnAction(this::showHideChangedPassword);

        // EventHandling CheckBox to show and hide passwords in registration
        this.loginView.getRegistrationDialogPane().getShowPassword().setOnAction(this::showHideRegistrationPW);

        // Ping Button Event Handler for LoginView
        this.loginView.getPingButton().setOnMouseClicked(this::ping);

        // Instantiate barchart with utils
        Timeline Updater = new Timeline(new KeyFrame(Duration.seconds(0.3), new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                toDoView.getSerie1().getData().clear();
                toDoView.getSerie2().getData().clear();
                toDoView.getSerie1().getData().add(new XYChart.Data<String, Number>("Erledigt", toDoList.getToDoListDone().size()));
                toDoView.getSerie2().getData().add(new XYChart.Data<String, Number>("Geplant", toDoList.getToDoListPlanned().size()));
            }
        }));

        Updater.setCycleCount(Timeline.INDEFINITE);
        Updater.play();
        toDoView.getBc().getData().addAll(toDoView.getSerie1(), toDoView.getSerie2());
    }


    // ---------------------------------- Classic Getters
    public ToDoView getToDoView() {
        return toDoView;
    }

    public ToDo getToDo() {
        return toDo;
    }

    public ToDoList getToDoList() {
        return toDoList;
    }

    public ImportantBarView getImportantBarView() {
        return importantBarView;
    }

    public GarbageBarView getGarbageBarView() {
        return garbageBarView;
    }

    public PlannedBarView getPlannedBarView() {
        return plannedBarView;
    }

    public DoneBarView getDoneBarView() {
        return doneBarView;
    }

    public SearchBarView getSearchBarView() {
        return searchBarView;
    }

    public FocusTimerDialogPane getDialog() {
        return dialog;
    }

    public FocusTimerModel getModel() {
        return focusModel;
    }

    // ---------------------------------- CRUD-Methods
    /* Create method
     * Parses the inputs of the user required for a new ToDoInstance, creates the instance and stores it.
     * Needs input from ToDoView
     */
    public void createToDo(String title, String message, LocalDate dueDate, String category, String priorityString,
                           ArrayList<String> tags) {
        ToDo toDo = new ToDo(title, message, dueDate, category, priorityString, tags);
        this.toDoList.addToDo(toDo);
        this.toDoList.updateSublists();

        // Send data to server
        int itemID = this.clientNetworkPlugin.createToDo(toDo);

        // Set returned ID to created item
        toDo.setID(itemID);

    }

    // CREATE Item overload method for missing dueDate
    private void createToDo(String title, String message, String category,
                            String priorityString, ArrayList<String> tagArrayList) {
        ToDo toDo = new ToDo(title, message, category, priorityString, tagArrayList);
        this.toDoList.addToDo(toDo);
        this.toDoList.updateSublists();

        // Send data to server & catch ID
        int itemID = this.clientNetworkPlugin.createToDo(toDo);

        // Set returned ID to created item
        toDo.setID(itemID);
    }

    /* Read method
     * Returns a ToDo based on its ID
     */
    public ToDo getToDo(int ID) {
        this.clientNetworkPlugin.getToDo(ID);
        return this.toDoList.getToDo(ID);

    }

    /* Update method
     * Gets a specific ToDo based on its ID, updated the contents and stores it again.
     * Maybe pass in an ToDo as parameter?
     */
    public void updateToDo(MouseEvent e) {

        // Check for double click
        if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {

            // Get clicked item
            MainBarView activeMidView = (MainBarView) this.getActiveMidView();
            int index = activeMidView.getTableView().getSelectionModel().getSelectedIndex();

            // Don't run if double click on table head
            if (index != -1) {
                ObservableList<ToDo> items = activeMidView.getTableView().getItems();
                ToDo itemToUpdate = items.get(index);

                // Open new dialogPane to make it editable
                Dialog<ButtonType> addDialog = new Dialog<ButtonType>();
                this.toDoView.setAddToDoDialog(addDialog);
                addDialog.setTitle("Aufgabe ansehen");
                AddToDoDialogPane updateDialogPane = new AddToDoDialogPane(this.toDoView.getListView().getItems(), itemToUpdate, this.clientNetworkPlugin);
                Stage stage5 = (Stage) toDoView.getAddToDoDialog().getDialogPane().getScene().getWindow();
                stage5.getIcons().add(new Image(this.getClass().getResource("/common/resources/doneIcon4.png").toString()));
                updateDialogPane.disableAllControls();
                this.toDoView.setToDoDialogPane(updateDialogPane);
                this.toDoView.getAddToDoDialog().setDialogPane(this.toDoView.getToDoDialogPane());
                Optional<ButtonType> result = this.toDoView.getAddToDoDialog().showAndWait();

                // Parse only positive result, ignore CANCEL_CLOSE

            }

        }

        // Update lists
        this.updateInstancedSublists();
    }

    /* Delete method
     * Gets a specific ToDo based on its ID and deletes it.
     */
    public void deleteToDo(MouseEvent e) {

        // Check if item already was deleted, purge it if so
        ToDo toDo = toDoList.getToDo((Button) e.getSource());

        // Recreate if first delete
        this.clientNetworkPlugin.deleteToDo(toDo.getID());
        this.toDoList.removeToDo(toDo);

        this.updateInstancedSublists();
    }


    // ---------------------------------- Methods to change items
    /* Method to set a ToDo on done ("Erledigt") whenever the button is clicked.
     * Fetches out the corresponding toDo from the button clicked
     * Deletes all other categories from the toDo, since a toDo can only be 'done' when it's done
     */
    public void setToDoOnDone(MouseEvent e) {
        ToDo toDo = toDoList.getToDo((Button) e.getSource());
        this.clientNetworkPlugin.deleteToDo(toDo.getID());
        this.toDoList.removeToDo(toDo);

        if (toDo.getCategory() != null && toDo.getCategory().equals("Papierkorb")) {
            this.toDoList.getGarbageList().remove(toDo);
        }

        toDo.setCategory("Erledigt");
        toDo.setDone(true);

        int ID = this.clientNetworkPlugin.createToDo(toDo);
        toDo.setID(ID);

        this.toDoList.addToDo(toDo);
        this.updateInstancedSublists();
    }

    /* Method to mark ToDo as important
     * ToDo gets deleted from preceding sublist via the .setCategory method.
     */
    private void setToDoAsImportant(MouseEvent e) {
        ToDo toDo = toDoList.getToDo((Button) e.getSource());
        this.clientNetworkPlugin.deleteToDo(toDo.getID());
        this.toDoList.removeToDo(toDo);

        if (toDo.getCategory() != null && toDo.getCategory().equals("Papierkorb")) {
            this.toDoList.getGarbageList().remove(toDo);
        }
        toDo.setCategory("Wichtig");

        int ID = this.clientNetworkPlugin.createToDo(toDo);
        toDo.setID(ID);
        this.toDoList.addToDo(toDo);

        this.updateInstancedSublists();
    }

    /* Method to mark Item as garbage
     * Item gets deleted from preceding sublist via the .setCategory method.
     * Deletes the item from the database as well.
     */
    private void setToDoAsGarbage(MouseEvent e) {

        // Check if item already was deleted, purge it if so
        ToDo toDo = toDoList.getToDo((Button) e.getSource());
        if (toDo.getCategory() != null && toDo.getCategory().equals("Papierkorb")) {
            this.clientNetworkPlugin.deleteToDo(toDo.getID());
            // this.toDoList.getToDoList().remove(toDo);
            this.toDoList.removeToDo(toDo);
            this.updateInstancedSublists();
            return;
        }

        // Recreate if first delete
        this.clientNetworkPlugin.deleteToDo(toDo.getID());
        this.toDoList.removeToDo(toDo);
        toDo.setCategory("Papierkorb");
        int ID = this.clientNetworkPlugin.createToDo(toDo);
        toDo.setID(ID);
        this.toDoList.addToDo(toDo);
        this.updateInstancedSublists();

    }


    // ---------------------------------- Searchbar-method
    /* Method that is linked to the searchButton
     * Does not generate a new view and is only used by searchItemAndGenerateView
     */
    private void searchItem(MouseEvent e) {

        // Fetch input
        SearchBarView midView = (SearchBarView) this.getActiveMidView();
        String searchString = midView.getSearchField().getText();

        // Clear pane
        midView.getTableView().getItems().clear();

        // Search items
        ArrayList<ToDo> searchList = this.toDoList.searchItem(searchString);

        // Populate list
        midView.getTableView().getItems().addAll(searchList);

        // Reset action handler
        this.searchBarView.getSearchButton().setOnMouseClicked(this::searchItem);
        this.searchBarView.getSearchField().setOnKeyPressed(this::searchItemWithEnter);

    }
    
    private void searchItemWithEnter(KeyEvent ke) {

        if (ke.getCode().equals(KeyCode.ENTER)) {
            // Fetch input
            SearchBarView midView = (SearchBarView) this.getActiveMidView();
            String searchString = midView.getSearchField().getText();

            // Clear pane
            midView.getTableView().getItems().clear();

            // Search items
            ArrayList<ToDo> searchList = this.toDoList.searchItem(searchString);

            // Populate list
            midView.getTableView().getItems().addAll(searchList);

            // Reset action handler
            this.searchBarView.getSearchButton().setOnMouseClicked(this::searchItem);
            this.searchBarView.getSearchField().setOnKeyPressed(this::searchItemWithEnter);
        }

    }
    
    

    /* Method that is linked to the searchButton
     * Generates a new view and sets it to the center
     */
    private void searchItemAndGenerateView(MouseEvent e) {

        // Fetch input
        MainBarView midView = (MainBarView) this.getActiveMidView();
        String searchString = midView.getSearchField().getText();

        // Only go ahead if input is not empty
        if (searchString.length() != 0) {

            // Search items
            ArrayList<ToDo> searchList = this.toDoList.searchItem(searchString);
            ObservableList<ToDo> observableSearchList = FXCollections.observableArrayList(searchList);

            // Generate new searchView
            this.searchBarView = new SearchBarView(observableSearchList);
            this.searchBarView.getCreateToDo().setOnMouseClicked(this::createToDoDialog);
            this.linkTableViewListeners(searchBarView.getTableView().getItems());
            this.searchBarView.getTableView().setOnMouseClicked(this::updateToDo);
            this.searchBarView.getSearchButton().setOnMouseClicked(this::searchItem);
            this.searchBarView.getSearchField().setOnKeyPressed(this::searchItemWithEnter);

            // Put it on main view
            toDoView.getBorderPane().setCenter(this.searchBarView);

        }

        // Otherwise just consume the event
        e.consume();
    }

    // Takes the enter key instead of a mouseclick on the search button
    private void searchItemAndGenerateView(KeyEvent ae) {

    	if (ae.getCode().equals(KeyCode.ENTER)) {
    		
    		  // Fetch input
            MainBarView midView = (MainBarView) this.getActiveMidView();
            String searchString = midView.getSearchField().getText();

            // Only go ahead if input is not empty
            if (searchString.length() != 0) {

                // Search items
                ArrayList<ToDo> searchList = this.toDoList.searchItem(searchString);
                ObservableList<ToDo> observableSearchList = FXCollections.observableArrayList(searchList);

                // Generate new searchView
                this.searchBarView = new SearchBarView(observableSearchList);
                this.searchBarView.getCreateToDo().setOnMouseClicked(this::createToDoDialog);
                this.linkTableViewListeners(searchBarView.getTableView().getItems());
                this.searchBarView.getTableView().setOnMouseClicked(this::updateToDo);
                this.searchBarView.getSearchButton().setOnMouseClicked(this::searchItem);
                this.searchBarView.getSearchField().setOnKeyPressed(this::searchItemWithEnter);

                // Put it on main view
                toDoView.getBorderPane().setCenter(this.searchBarView);
            }
            
            // Otherwise just consume the event
            ae.consume();
    	}
    	
        // Otherwise just consume the event
        ae.consume(); 	
      


    }

    /* Method to update local as well as instantiated sublists
     * Updates the sublists of the controller, as well as each sublist in the different instantiated views
     */
    public synchronized void updateInstancedSublists() {

        // Update current sublists
        this.toDoList.updateSublists();

        // Update sublists in each view
        if (this.importantBarView != null) {
            this.importantBarView.getTableView().getItems().clear();
            this.importantBarView.getTableView().getItems().addAll(this.toDoList.getToDoListImportant());
            this.linkTableViewListeners(this.importantBarView.getTableView().getItems());
        }

        if (this.garbageBarView != null) {
            this.garbageBarView.getTableView().getItems().clear();
            this.garbageBarView.getTableView().getItems().addAll(this.toDoList.getToDoListGarbage());
            this.linkTableViewListeners(this.garbageBarView.getTableView().getItems());
        }

        if (this.plannedBarView != null) {
            this.plannedBarView.getTableView().getItems().clear();
            this.plannedBarView.getTableView().getItems().addAll(this.toDoList.getToDoListPlanned());
            this.linkTableViewListeners(this.plannedBarView.getTableView().getItems());
        }

        if (this.doneBarView != null) {
            this.doneBarView.getTableView().getItems().clear();
            this.doneBarView.getTableView().getItems().addAll(this.toDoList.getToDoListDone());
            this.linkTableViewListeners(this.doneBarView.getTableView().getItems());
        }
    }

    /* Method that is used to retrieve the active midView
     *
     */
    private Node getActiveMidView() {
        return this.toDoView.getBorderPane().getCenter();
    }

    /* Method to set event handlers for the tableView Items
     *
     */
    private void linkTableViewListeners(ObservableList<ToDo> listItems) {
        for (ToDo toDo : listItems) {

            if(this.clientNetworkPlugin.isConnectedToPrivateServer()) {
                toDo.getDoneButton().setOnMouseClicked(this::setToDoOnDone);
                toDo.getImportantButton().setOnMouseClicked(this::setToDoAsImportant);
                toDo.getGarbageButton().setOnMouseClicked(this::setToDoAsGarbage);
            } else {
                toDo.getDoneButton().setDisable(true);
                toDo.getImportantButton().setDisable(true);
                toDo.getGarbageButton().setOnMouseClicked(this::deleteToDo);
            }

        }
    }

    /* Method to change center view of GUI
     * ----------------------------------- Swapping out centerView
     * We set up a clickListener on the (main) listView and listen on any click
     * On a click, we parse out which item was clicked by its index
     * Based on which item was clicked, we swap out the center of the main borderPane with the corresponding view
     * ----------------------------------- Adding listeners to the rows of the tableview
     * Since we have buttons inside the tableView, they need to be addressed by the controller as well.
     * However, the concept of a javaFX-tableView intends to represent instances of a model inside each row.
     * On the other hand, the MVC pattern demands a strict separation of model (data), and view.
     * This leads to a dilemma, where each solution violates one of the concepts. Placing the button inside the model
     * violates the MVC concept, placing the button inside the tableView via a workaround violates the intends of javaFX.
     * Anyhow - we see less dissonance in adding a button to a model, since this can be perceived as a "trait" of the model.
     */
    private void changeCenterBar(MouseEvent e) {
        switch (toDoView.getListView().getSelectionModel().getSelectedIndex()) {
            case 0 -> {
                // Create new instance of the view, populated with up-to-date dataset
                this.importantBarView = new ImportantBarView(this.toDoList.getToDoListImportant());

                // Add listeners
                importantBarView.getCreateToDo().setOnMouseClicked(this::createToDoDialog);
                linkTableViewListeners(importantBarView.getTableView().getItems());
                importantBarView.getSearchButton().setOnMouseClicked(this::searchItemAndGenerateView);
                importantBarView.getSearchField().setOnKeyPressed(this::searchItemAndGenerateView);
                importantBarView.getDateFilterCombobox().setOnAction(this::changeCombo);
                importantBarView.getTableView().setOnMouseClicked(this::updateToDo);

                // Put it on main view
                toDoView.getBorderPane().setCenter(importantBarView);
            }
            case 1 -> {
                plannedBarView = new PlannedBarView(this.toDoList.getToDoListPlanned());
                plannedBarView.getCreateToDo().setOnMouseClicked(this::createToDoDialog);
                plannedBarView.getSearchButton().setOnMouseClicked(this::searchItemAndGenerateView);
                plannedBarView.getSearchField().setOnKeyPressed(this::searchItemAndGenerateView);
                plannedBarView.getDateFilterCombobox().setOnAction(this::changeCombo);
                plannedBarView.getTableView().setOnMouseClicked(this::updateToDo);
                linkTableViewListeners(plannedBarView.getTableView().getItems());
                toDoView.getBorderPane().setCenter(plannedBarView);

            }
            case 2 -> {
                doneBarView = new DoneBarView(this.toDoList.getToDoListDone());
                doneBarView.getCreateToDo().setOnMouseClicked(this::createToDoDialog);
                doneBarView.getSearchButton().setOnMouseClicked(this::searchItemAndGenerateView);
                doneBarView.getSearchField().setOnKeyPressed(this::searchItemAndGenerateView);
                doneBarView.getDateFilterCombobox().setOnAction(this::changeCombo);
                doneBarView.getTableView().setOnMouseClicked(this::updateToDo);
                linkTableViewListeners(doneBarView.getTableView().getItems());
                toDoView.getBorderPane().setCenter(doneBarView);
            }
            case 3 -> {
                garbageBarView = new GarbageBarView(this.toDoList.getToDoListGarbage());
                garbageBarView.getCreateToDo().setOnMouseClicked(this::createToDoDialog);
                garbageBarView.getSearchButton().setOnMouseClicked(this::searchItemAndGenerateView);
                garbageBarView.getSearchField().setOnKeyPressed(this::searchItemAndGenerateView);
                garbageBarView.getDateFilterCombobox().setOnAction(this::changeCombo);
                garbageBarView.getTableView().setOnMouseClicked(this::updateToDo);
                linkTableViewListeners(garbageBarView.getTableView().getItems());
                toDoView.getBorderPane().setCenter(garbageBarView);
            }
        }
    }

    public void changeToPlannedBarView(MouseEvent event) {
        plannedBarView = new PlannedBarView(this.toDoList.getToDoListPlanned());
        plannedBarView.getCreateToDo().setOnMouseClicked(this::createToDoDialog);
        plannedBarView.getSearchButton().setOnMouseClicked(this::searchItemAndGenerateView);
        plannedBarView.getSearchField().setOnKeyPressed(this::searchItemAndGenerateView);
        plannedBarView.getDateFilterCombobox().setOnAction(this::changeCombo);
        plannedBarView.getTableView().setOnMouseClicked(this::updateToDo);
        linkTableViewListeners(plannedBarView.getTableView().getItems());
        toDoView.getBorderPane().setCenter(plannedBarView);
        this.plannedBarView.getDateFilterCombobox().setDisable(true);
    }


    // ---------------------------------- Creation Dialog methods
    /* Validate user input method
     *
     */
    private boolean validateUserInput() {

        // Parse out data
        String title = this.toDoView.getToDoDialogPane().getTitleTextfield().getText();
        String category = this.toDoView.getToDoDialogPane().getCategoryComboBox().getValue();
        String message = this.toDoView.getToDoDialogPane().getMessageTextArea().getText();
        String dueDateString = "";
        String priority = this.toDoView.getToDoDialogPane().getPriorityComboBox().getValue().toString();
        String tags = this.toDoView.getToDoDialogPane().getTagsTextfield().getText();

        // Parse dueDate string
        try {
            dueDateString = this.toDoView.getToDoDialogPane().getDatePicker().getValue().toString();
            if (dueDateString.equals("")) {
                // Setting default date to today
                // Default value removed for project 2
                // this.toDoView.getToDoDialogPane().getDatePicker().setValue(LocalDate.now());
            }
        } catch (NullPointerException e) {
            // Setting default date to today
            // Default value removed for project 2
            // this.toDoView.getToDoDialogPane().getDatePicker().setValue(LocalDate.now());
            // dueDateString = LocalDate.now().toString();
        }


        // Parse category
        // Set default category if none is chosen
        // We don't do default values for the project 2.0
        // Note that we need to update the stored variable as it is used for the validity check later
        // if (category == null) {
        // this.toDoView.getToDoDialogPane().getCategoryComboBox().setValue("Geplant");
        // category = this.toDoView.getToDoDialogPane().getCategoryComboBox().getValue();
        // }

        // Parse priority - set default if non
        if (priority == null) {
            this.toDoView.getToDoDialogPane().getPriorityComboBox().setValue(Priority.Low);
            priority = Priority.Low.toString();
        }

        // Validate easy inputs first
        boolean titleIsValid = title.length() >= 3 && title.length() <= 20;
        boolean messageIsValid = message.length() <= 255;
        boolean categoryIsValid = this.toDoView.getListView().getItems().contains(category) || category == null;
        boolean tagsAreValid = false;
        String[] tagArray;

        // Validate date
        boolean dateIsValid = false;
        LocalDate paneDate = null;
        if (!dueDateString.equals("")) {
            paneDate = LocalDate.parse(dueDateString);
        }
        if (dueDateString.equals("") || paneDate.compareTo(LocalDate.now()) >= 0) {
            dateIsValid = true;
        }

        // Validate tags
        // Removes all whitespace and non-visible characters with \\s and splits the string by ;
        try {
            tagArray = tags.replaceAll("\\s", "").split(";");
            tagsAreValid = true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            tagsAreValid = false;
        }

        // Validate priority
        boolean priorityIsValid = false;
        Priority priorityValidation = Priority.valueOf(priority);
        if (priorityValidation == Priority.Low
                || priorityValidation == Priority.Medium
                || priorityValidation == Priority.High) {
            priorityIsValid = true;
        }


        // Give graphical feedback
        if (!titleIsValid) {
            this.toDoView.getToDoDialogPane().getTitleTextfield().getStyleClass().add("notOk");
        }
        if (!messageIsValid) {
            this.toDoView.getToDoDialogPane().getMessageTextArea().getStyleClass().add("notOk");
        }
        if (!categoryIsValid) {
            this.toDoView.getToDoDialogPane().getCategoryComboBox().getStyleClass().add("notOk");
        }
        if (!dateIsValid) {
            this.toDoView.getToDoDialogPane().getDatePicker().getStyleClass().add("notOk");
        }
        if (!tagsAreValid) {
            this.toDoView.getToDoDialogPane().getTagsTextfield().getStyleClass().add("notOk");
        }
        if (!priorityIsValid) {
            this.toDoView.getToDoDialogPane().getPriorityComboBox().getStyleClass().add("notOk");
        }

        return (titleIsValid && messageIsValid && categoryIsValid && dateIsValid && tagsAreValid && priorityIsValid);

    }

    /* Dialog creation method
     * Shows the dialog to get input from the user required for a new ToDO
     * After user has made his input, controller parses out the data and creates a new ToDo
     * After the new ToDo is created, it wipes the inputs form the dialog pane so we can set up a clean, new dialog
     */
    public void createToDoDialog(MouseEvent e) {

        // Create & Customize Dialog
        this.toDoView.setAddToDoDialog(new Dialog<ButtonType>());
        this.toDoView.setToDoDialogPane(new AddToDoDialogPane(this.toDoView.getListView().getItems(), this.clientNetworkPlugin));
        this.toDoView.getAddToDoDialog().setDialogPane(this.toDoView.getToDoDialogPane());

        this.toDoView.getAddToDoDialog().setTitle("Neue Aufgabe");
        Stage stage = (Stage) toDoView.getAddToDoDialog().getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(this.getClass().getResource("/common/resources/doneIcon4.png").toString()));
        stage.resizableProperty().setValue(Boolean.FALSE);

        // Set up event filter on OK-button to prevent dialog from closing when user input is not valid
        Button okButton = (Button) this.toDoView.getToDoDialogPane().lookupButton(this.toDoView.getToDoDialogPane().getOkButtonType());
        okButton.addEventFilter(ActionEvent.ACTION,
                event -> {
                    if (!validateUserInput()) {
                        event.consume();
                    }
                });

        // Clear graphical validation
        this.toDoView.getToDoDialogPane().getTitleTextfield().getStyleClass().remove("notOk");
        this.toDoView.getToDoDialogPane().getMessageTextArea().getStyleClass().remove("notOk");
        this.toDoView.getToDoDialogPane().getCategoryComboBox().getStyleClass().remove("notOk");
        this.toDoView.getToDoDialogPane().getDatePicker().getStyleClass().remove("notOk");
        this.toDoView.getToDoDialogPane().getTagsTextfield().getStyleClass().remove("notOk");

        // Show dialog
        Optional<ButtonType> result = this.toDoView.getAddToDoDialog().showAndWait();

        // Parse only positive result, ignore CANCEL_CLOSE
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {

            // Validate user input
            if (this.validateUserInput()) {

                // Parse out data
                String dueDateString = "";
                String title = this.toDoView.getToDoDialogPane().getTitleTextfield().getText();
                String category = this.toDoView.getToDoDialogPane().getCategoryComboBox().getValue();
                String message = this.toDoView.getToDoDialogPane().getMessageTextArea().getText();
                if (this.toDoView.getToDoDialogPane().getDatePicker().getValue() == null) {
                    dueDateString = null;
                } else {
                    dueDateString = this.toDoView.getToDoDialogPane().getDatePicker().getValue().toString();
                }
                String tags = this.toDoView.getToDoDialogPane().getTagsTextfield().getText();
                String priorityString = this.toDoView.getToDoDialogPane().getPriorityComboBox().getValue().toString();
                String[] tagArray = tags.replaceAll("\\s", "").split(";");
                ArrayList<String> tagArrayList = new ArrayList<String>(List.of(tagArray));

                if (category == null) {
                    category = "";
                }

                if (dueDateString != null && !dueDateString.equals("")) {
                    String parsedDateString = LocalDate.parse(dueDateString).format(DateTimeFormatter.ISO_DATE);
                    LocalDate parsedDate = LocalDate.parse(parsedDateString);
                    this.createToDo(title, message, parsedDate, category, priorityString, tagArrayList);
                } else {
                    this.createToDo(title, message, category, priorityString, tagArrayList);
                }

            }

        }

        // Clear out dialogPane
        this.toDoView.getToDoDialogPane().clearPane();

        // Add editing functionality
        MainBarView midView = (MainBarView) this.getActiveMidView();
        midView.getTableView().setOnMouseClicked(this::updateToDo);

        // Refresh views
        this.updateInstancedSublists();

    }


    // ---------------------------------- Focus timer methods

    // Open a new focus timer window
    public void createFocusTimer(MouseEvent e) {

        this.toDoView.getFocusTimerDialog().getModel().restart();
        this.toDoView.getFocusTimerDialog().getModel().stop();

        ((FocusTimerDialogPane) this.toDoView.getFocusDialog().getDialogPane()).getPlayButton().setOnAction(a -> {
            this.toDoView.getFocusTimerDialog().getModel().start();
        });
        ((FocusTimerDialogPane) this.toDoView.getFocusDialog().getDialogPane()).getStopButton().setOnAction(a -> {
            this.toDoView.getFocusTimerDialog().getModel().stop();
        });
        ((FocusTimerDialogPane) this.toDoView.getFocusDialog().getDialogPane()).getReplayButton().setOnAction(a -> {
            this.toDoView.getFocusTimerDialog().getModel().restart();
        });
        this.toDoView.getFocusDialog().showAndWait();
    }

    /*
     * Depending on which date filter (ComboBox) the user choosed,
     * the ToDo task-view will change.
     */
    private void changeCombo(ActionEvent event) {

        // Update sublists
        this.updateInstancedSublists();

        // Set items based on selected category
        MainBarView main = (MainBarView) getActiveMidView();
        switch (main.getDateFilterCombobox().getSelectionModel().getSelectedIndex()) {
            case 0: {
                String selectedCategory = this.toDoView.getListView().getSelectionModel().getSelectedItem();
                ObservableList<ToDo> resultSet = FXCollections.observableArrayList();
                switch (selectedCategory) {

                    case "Geplant": {
                        resultSet = this.toDoList.getToDoListPlanned();
                        break;
                    }
                    case "Wichtig": {
                        resultSet = this.toDoList.getToDoListImportant();
                        break;
                    }
                    case "Papierkorb": {
                        resultSet = this.toDoList.getToDoListGarbage();
                        break;
                    }
                    case "Erledigt": {
                        resultSet = this.toDoList.getToDoListDone();
                    }

                }

                main.getTableView().getItems().clear();
                main.getTableView().getItems().addAll(resultSet);
                break;
            }
            case 1: {

                String selectedCategory = this.toDoView.getListView().getSelectionModel().getSelectedItem();
                ObservableList<ToDo> resultSet = FXCollections.observableArrayList();
                switch (selectedCategory) {

                    case "Geplant": {
                        ArrayList<ToDo> arrayListToday = this.toDoList.searchLocalToday();
                        for (ToDo item : arrayListToday) {
                            if (item.getCategory()== null || item.getCategories().contains("Geplant")) {
                                resultSet.add(item);
                            }
                        }
                        break;
                    }
                    case "Wichtig": {
                        ArrayList<ToDo> arrayListToday = this.toDoList.searchLocalToday();
                        for (ToDo item : arrayListToday) {
                            if (item.getCategories().contains("Wichtig")) {
                                resultSet.add(item);
                            }
                        }
                        break;
                    }
                    case "Papierkorb": {
                        ArrayList<ToDo> arrayListToday = this.toDoList.searchLocalToday();
                        for (ToDo item : arrayListToday) {
                            if (item.getCategories().contains("Papierkorb")) {
                                resultSet.add(item);
                            }
                        }
                        break;
                    }
                    case "Erledigt": {
                        ArrayList<ToDo> arrayListToday = this.toDoList.searchLocalToday();
                        for (ToDo item : arrayListToday) {
                            if (item.getCategories().contains("Erledigt")) {
                                resultSet.add(item);
                            }
                        }
                    }

                }

                ObservableList<ToDo> observableListToday = FXCollections.observableArrayList(resultSet);
                main.getTableView().getItems().clear();
                main.getTableView().getItems().addAll(observableListToday);
            }

        }
    }

    public void playTimer(MouseEvent event) {
        focusModel.start();
    }

    public void stopTimer(MouseEvent event) {
        focusModel.stop();
    }

    public void replayTimer(MouseEvent event) {
        focusModel.restart();
    }

    // Open a new focus timer window
    public void createHowTo(ActionEvent event) {

        // show dialog
        this.toDoView.getHowToDialog().showAndWait();
        this.toDoView.getHowToDialogPane().getMediaPlayer().stop();


        // If ButtonType "beenden" is clicked, stop the Video
        if (toDoView.getHowToDialogPane().getCloseButtonType().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) {

            toDoView.getHowToDialogPane().getMediaPlayer().stop();
        }
    }

    // Plays HowTo video and binds slider
    public void playMedia(MouseEvent event) {

        this.toDoView.getHowToDialogPane().getMediaPlayer().play();
        
        toDoView.getHowToDialogPane().getSlider().setMax(toDoView.getHowToDialogPane().getMediaPlayer().getTotalDuration().toSeconds());
    	
    	toDoView.getHowToDialogPane().getMediaPlayer().currentTimeProperty().addListener(new ChangeListener<Duration>() {
    		@Override
    		public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
    			toDoView.getHowToDialogPane().getSlider().setValue(newValue.toSeconds());
    		}
    	});
	
    	toDoView.getHowToDialogPane().getSlider().setOnMousePressed(new EventHandler <MouseEvent>() {
    		@Override
    		public void handle(MouseEvent event) {
    			toDoView.getHowToDialogPane().getMediaPlayer().seek(Duration.seconds(toDoView.getHowToDialogPane().getSlider().getValue()));
    		}
    		
    	});
    	
    	toDoView.getHowToDialogPane().getMediaPlayer().setOnReady(new Runnable() {
    		@Override
    		public void run() {
    			Duration total = toDoView.getHowToDialogPane().getMedia().getDuration();
    			toDoView.getHowToDialogPane().getSlider().setMax(total.toSeconds());
    		}
    	});
        
    }

    //Stops HowTo video
    public void stopMedia(MouseEvent event) {

        this.toDoView.getHowToDialogPane().getMediaPlayer().pause();

    }

    //Replays HowTo video
    public void replayMedia(MouseEvent event) {

        this.toDoView.getHowToDialogPane().getMediaPlayer().stop();
        
        // Sets slider to zero
        this.toDoView.getHowToDialogPane().getSlider().setValue(0);

    }

    /*
     * Parses login data and checks if ok.
     * If ok, the App will open,
     * if not ok, Alert Box will open.
     */
    public void handleLogin(ActionEvent event) {

        /*
         * If the password is not hidden, it will change to hidden
         * when the user clicks on Login. We need this for the Login-Connection
         */

        if (this.loginView.getPasswordFieldVBox().getChildren().contains(this.loginView.getShowedPasswordField())) {

            this.loginView.getPasswordField().setText(this.loginView.getShowedPasswordField().getText());

            this.loginView.getPasswordFieldVBox().getChildren().clear();
            this.loginView.getPasswordFieldVBox().getChildren().add(this.loginView.getPasswordField());
        }

        // Set up connection
        // this.clientNetworkPlugin.connect("localhost", 50002);
        if (!this.clientNetworkPlugin.isConnected()) {
            this.clientNetworkPlugin.connect();
        }

        String emailLogin = loginView.getUserField().getText();
        String passwordLogin = loginView.getPasswordField().getText();

        boolean result = false;
        if(emailLogin != null && !emailLogin.equals("") && passwordLogin != null && !passwordLogin.equals("")) {
            result = this.clientNetworkPlugin.login(emailLogin, passwordLogin);
        }

        if (result) {
        	
        	this.loginView.getLabel().setText("");
            this.toDoView.getPingButton().getStyleClass().remove("successfulPing");
            this.toDoView.getPingButton().getStyleClass().remove("badPing");
            // Clear lists
            this.toDoList.clearLists();
            this.updateInstancedSublists();

            // Grab Items from database
            ArrayList<String> resultList = this.clientNetworkPlugin.listToDos();

            // Get each item in a separate thread
            for (String id : resultList) {

                Thread callThread = new Thread(new LoadTasksRunnable(id, this.clientNetworkPlugin, this));
                // callThread.setName("TASK-ID-Thread: " + id);

                // We need this sleep, since requests made too close to each other are not getting received by the server
                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                    System.out.println("CONTROLLER: THREAD SLEEP FAILED");
                }

                callThread.setDaemon(true);
                this.threadPool.add(callThread);
                callThread.start();
            }

            // Update UI
            Platform.runLater(() -> {
            			toDoView.getLoggedOnUser().setText("Welcome " + emailLogin);
                        this.stage.setScene(scene2);
                        stage.resizableProperty().setValue(Boolean.TRUE);

                        this.toDoList.addAll(this.returnItems);
                        this.updateInstancedSublists();
                        stage.show();
                    }
            );
        } else {

            this.loginView.getLabel().setText("Anmeldung fehlgeschlagen - Benutzername oder Passwort ist ungültig!");
            this.loginView.getLabel().setFont(Font.font("Verdana", FontWeight.BOLD, 11));
            this.loginView.getLabel().setTextFill(Color.web("#C00000"));
        	
        }

    }

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
        if (description == null && dueDate == null && category == null) {
            return new ToDo(title, priority);
        }

        // Missing 2 parameters
        // Missing dueDate and category
        if (description != null && dueDate == null && category == null) {
            return new ToDo(title, priority, description);
        }

        // Missing description and category
        if (description == null && dueDate != null && category == null) {
            return new ToDo(title, priority, dueDate);
        }

        // Missing description and dueDate
        if (description == null && dueDate == null && category != null) {
            return new ToDo(title, priority, category);
        }

        // Missing 1 parameter
        // Missing category
        if (description != null && dueDate != null && category == null) {
            return new ToDo(title, priority, description, dueDate);
        }

        // Missing dueDate
        if (description != null && dueDate == null && category != null) {
            return new ToDo(title, priority, description, category);
        }

        // Missing description
        if (description == null && dueDate != null && category != null) {
            return new ToDo(title, priority, dueDate, category);
        }

        // No missing parameters
        returnItem = new ToDo(title, priority, description, dueDate, category);
        return returnItem;
    }

    // Signs the user out.
    public void logout(MouseEvent event) {

        // Set ping button style to default
        this.loginView.getPingButton().getStyleClass().remove("successfulPing");
        this.loginView.getPingButton().getStyleClass().remove("badPing");

        boolean result = this.clientNetworkPlugin.logout();

        //if user logged out, show LoginView again
        if (result) {
        	
        	this.toDoList.clearLists();
        	
            this.stage.close();

            /*
            if (!this.clientNetworkPlugin.isConnected()) {
                this.clientNetworkPlugin.connect();
            }
             */
            this.clientNetworkPlugin.connect();

            this.stage.setScene(scene1);
            stage.resizableProperty().setValue(Boolean.FALSE);
            this.loginView.getUserField().setText("");
            this.loginView.getPasswordField().setText("");
            this.stage.show();
        }

    }

    // Change password when signed in.
    public void changePassword(ActionEvent event) {

        this.toDoView.getChangePasswordDialogPane().getRepeatPasswordField().clear();
        this.toDoView.getChangePasswordDialogPane().getNewPasswordField().clear();
    	this.toDoView.getChangePasswordDialogPane().getShowPassword().setSelected(false);
        this.toDoView.getChangePasswordDialogPane().getLabel().setText("");
        Button okButton = (Button) this.toDoView.getChangePasswordDialogPane().lookupButton(
        		this.toDoView.getChangePasswordDialogPane().getOkButtonType());
        okButton.addEventFilter(ActionEvent.ACTION,
                e -> {
                	
                	/*
                	 * Changes the TextField to PasswordField, when user clicks button, 
                	 * because PasswordField is used in the further processes
                	 */
                	if (this.toDoView.getChangePasswordDialogPane().getNewPasswordHBox().getChildren().contains(
                			this.toDoView.getChangePasswordDialogPane().getNewPasswordTextField()) 
                			&& this.toDoView.getChangePasswordDialogPane().getRepeatPasswordHBox().getChildren().contains(
                					this.toDoView.getChangePasswordDialogPane().getRepeatPasswordTextField())) {
                		
                		this.toDoView.getChangePasswordDialogPane().getNewPasswordField().setText(
                				this.toDoView.getChangePasswordDialogPane().getNewPasswordTextField().getText());
                		
                		this.toDoView.getChangePasswordDialogPane().getRepeatPasswordField().setText(
                				this.toDoView.getChangePasswordDialogPane().getRepeatPasswordTextField().getText());
                		
                		this.toDoView.getChangePasswordDialogPane().getNewPasswordHBox().getChildren().clear();
                		this.toDoView.getChangePasswordDialogPane().getRepeatPasswordHBox().getChildren().clear();
                		
                		this.toDoView.getChangePasswordDialogPane().getNewPasswordHBox().getChildren().add(
                				this.toDoView.getChangePasswordDialogPane().getNewPasswordField());
                		
                		this.toDoView.getChangePasswordDialogPane().getRepeatPasswordHBox().getChildren().add(
                				this.toDoView.getChangePasswordDialogPane().getRepeatPasswordField());
                	}
                	
                	
                	
                    if (!validateChangedPassword()) {
                        e.consume();
                    }
                });

        toDoView.getChangePasswordDialog().showAndWait();

    }

    /*
     * Checks if the changed password has between 3 and 20 characters and if the passwords are similar
     * if OK, OK,
     * if NOT OK, the label for "failed" will appear.
     */
    public boolean validateChangedPassword() {

    	String upperPassword;
    	String bottomPassword;
    	
        // If password is hidden, take passwordField, otherwise TextField
    	
    	if (this.toDoView.getChangePasswordDialogPane().getShowPassword().isSelected()) {
    		
    		upperPassword = this.toDoView.getChangePasswordDialogPane().getNewPasswordTextField().getText();
    		bottomPassword = this.toDoView.getChangePasswordDialogPane().getRepeatPasswordTextField().getText();
    		
    	} else {
    		
    		upperPassword = this.toDoView.getChangePasswordDialogPane().getNewPasswordField().getText();
    		bottomPassword = this.toDoView.getChangePasswordDialogPane().getRepeatPasswordField().getText();
    	}
    	
    	// Check upper password for length
        if (upperPassword.length() >= 3 && upperPassword.length() <= 20) {

        	// Check if upper & buttom password match
            if (upperPassword.equals(bottomPassword)) {

                this.toDoView.getChangePasswordDialogPane().getLabel().setText("Passwort wurde geändert.");
                this.toDoView.getChangePasswordDialogPane().getLabel().setFont(Font.font("Verdana", FontWeight.BOLD, 11));
                this.toDoView.getChangePasswordDialogPane().getLabel().setTextFill(Color.web("#00B050"));


                String password;
                
                // Check if passwordField or TextField
                if (this.toDoView.getChangePasswordDialogPane().getShowPassword().isSelected()) {
            		
            		password = this.toDoView.getChangePasswordDialogPane().getNewPasswordTextField().getText();
            		
            	} else {
            		
            		password = this.toDoView.getChangePasswordDialogPane().getNewPasswordField().getText();
            	}

                this.clientNetworkPlugin.changePassword(password);

                return true;


            } else {

                this.toDoView.getChangePasswordDialogPane().getLabel().setText("Passwörter stimmen nicht überein!");
                this.toDoView.getChangePasswordDialogPane().getLabel().setFont(Font.font("Verdana", FontWeight.BOLD, 11));
                this.toDoView.getChangePasswordDialogPane().getLabel().setTextFill(Color.web("#C00000"));

                return false;

            }

        } else {

            this.toDoView.getChangePasswordDialogPane().getLabel().setText("Das Passwort muss zwischen 3 und 20 Zeichen lang sein.");
            this.toDoView.getChangePasswordDialogPane().getLabel().setFont(Font.font("Verdana", FontWeight.BOLD, 11));
            this.toDoView.getChangePasswordDialogPane().getLabel().setTextFill(Color.web("#C00000"));

            return false;

        }


    }

    public void openRegistration(MouseEvent event) {

        // Build up connection to server
        // this.clientNetworkPlugin.connect("localhost", 50002);
        if (!this.clientNetworkPlugin.isConnected()) {
            this.clientNetworkPlugin.connect();
        }

        // Prepare Dialog Pane
        this.loginView.getRegistrationDialogPane().getEmailField().clear();
        this.loginView.getRegistrationDialogPane().getRepeatPasswordField().clear();
        this.loginView.getRegistrationDialogPane().getPasswordField().clear();
        this.loginView.getRegistrationDialogPane().getLabel().setText("");
    	this.loginView.getRegistrationDialogPane().getShowPassword().setSelected(false);

        // Set up event filter on OK-button to prevent dialog from closing when user input is not valid
        Button okButton = (Button) this.loginView.getRegistrationDialogPane().lookupButton(this.loginView.getRegistrationDialogPane().getOkButtonType());
        okButton.addEventFilter(ActionEvent.ACTION,
                anonymousEvent -> {
                    if (!validatePassword()) {
                        anonymousEvent.consume();
                    }
                    
                
                });

        // Wait for user input
        Optional<ButtonType> result = this.loginView.getRegistrationDialog().showAndWait();

        // Catch result from the dialog
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
        	
        	/*
        	 * Set showed password back to hidden, because behind the hidden passwordField there is a indeed passwordField,
        	 * which will be used in further processes.
        	 */
            this.loginView.getRegistrationDialogPane().getPasswordPane().getChildren().clear();
            this.loginView.getRegistrationDialogPane().getRepeatPasswordPane().getChildren().clear();

            this.loginView.getRegistrationDialogPane().getPasswordField().setText(
                    this.loginView.getRegistrationDialogPane().getPasswordTextField().getText());

            this.loginView.getRegistrationDialogPane().getRepeatPasswordField().setText(
                    this.loginView.getRegistrationDialogPane().getRepeatTextField().getText());

            this.loginView.getRegistrationDialogPane().getPasswordPane().getChildren().addAll(
                    this.loginView.getRegistrationDialogPane().getPasswordLabel(),
                    this.loginView.getRegistrationDialogPane().getPasswordField());

            this.loginView.getRegistrationDialogPane().getRepeatPasswordPane().getChildren().addAll(
                    this.loginView.getRegistrationDialogPane().getRepeatPasswordLabel(),
                    this.loginView.getRegistrationDialogPane().getRepeatPasswordField());  
        	
            boolean accountCreationWasSuccessful = createLogin();
            if(accountCreationWasSuccessful) {
                Platform.runLater(() -> {
                    this.stage.setScene(scene1);
                    stage.resizableProperty().setValue(Boolean.FALSE);
                    stage.show();
                });
            }
        }

    }


    /*
     * Validates password in Registration. 
     * It must be >= 3 and <= 20 characters long and the password and the repeated password must match. 
     * If this is the case, the corresponding label is displayed. 
     * If it does not match or is too short/too long, another label is used.
     */
    public boolean validatePassword() {

    	String upperPassword;
    	String bottomPassword;
    	
        // If password is hidden, take passwordField, otherwise TextField
    	
    	if (this.loginView.getRegistrationDialogPane().getShowPassword().isSelected()) {
    		
    		upperPassword = this.loginView.getRegistrationDialogPane().getPasswordTextField().getText();
    		bottomPassword = this.loginView.getRegistrationDialogPane().getRepeatTextField().getText();
    		
    	} else {
    		
    		upperPassword = this.loginView.getRegistrationDialogPane().getPasswordField().getText();
    		bottomPassword = this.loginView.getRegistrationDialogPane().getRepeatPasswordField().getText();
    	}
    	

        // Check upper password for length
        if(upperPassword.length() >= 3 && upperPassword.length() <= 20) {
            // Check if upper & bottom password match
            if(upperPassword.equals(bottomPassword)) {
                this.loginView.getRegistrationDialogPane().getLabel().setText("Passwort wurde geändert.");
                this.loginView.getRegistrationDialogPane().getLabel().setFont(Font.font("Verdana", FontWeight.BOLD, 11));
                this.loginView.getRegistrationDialogPane().getLabel().setTextFill(Color.web("#00B050"));

                return true;
            } else {
                this.loginView.getRegistrationDialogPane().getLabel().setText("Passwörter stimmen nicht überein!");
                this.loginView.getRegistrationDialogPane().getLabel().setFont(Font.font("Verdana", FontWeight.BOLD, 11));
                this.loginView.getRegistrationDialogPane().getLabel().setTextFill(Color.web("#C00000"));

                return false;
            }
        } else {

            this.loginView.getRegistrationDialogPane().getLabel().setText("Das Passwort muss zwischen 3 und 20 Zeichen lang sein.");
            this.loginView.getRegistrationDialogPane().getLabel().setFont(Font.font("Verdana", FontWeight.BOLD, 11));
            this.loginView.getRegistrationDialogPane().getLabel().setTextFill(Color.web("#C00000"));
            
            return false;

        }

    }

    // creates Login
    public boolean createLogin() {

        String emailCreateLogin = this.loginView.getRegistrationDialogPane().getEmailField().getText();
        String passwordCreateLogin; 

        // Check if PasswordField or TextField
        	if (this.loginView.getEyeVBox().getChildren() == this.loginView.getEyeImage()) {
        		passwordCreateLogin = this.loginView.getRegistrationDialogPane().getPasswordField().getText();
        	} else {
        		passwordCreateLogin = this.loginView.getRegistrationDialogPane().getPasswordTextField().getText();
        	}
        
        
        boolean result = this.clientNetworkPlugin.createLogin(emailCreateLogin, passwordCreateLogin);
        return result;

    }

    // Shows password in LoginView, when "open eye" (image) is clicked
    public void showPassword(MouseEvent event) {


        this.loginView.getEyeVBox().getChildren().clear();
        this.loginView.getEyeVBox().getChildren().add(this.loginView.getEyeImage());

        this.loginView.getShowedPasswordField().setText(this.loginView.getPasswordField().getText());

        this.loginView.getPasswordFieldVBox().getChildren().clear();
        this.loginView.getPasswordFieldVBox().getChildren().add(this.loginView.getShowedPasswordField());

    }

    // Hides password in LoginView, when "crossed out eye" (image) is clicked
    public void hidePassword(MouseEvent event) {

        this.loginView.getEyeVBox().getChildren().clear();
        this.loginView.getEyeVBox().getChildren().add(this.loginView.getHiddenEyeImage());

        this.loginView.getPasswordField().setText(this.loginView.getShowedPasswordField().getText());

        this.loginView.getPasswordFieldVBox().getChildren().clear();
        this.loginView.getPasswordFieldVBox().getChildren().add(this.loginView.getPasswordField());
    }

    // If CheckBox is clicked, password can be seen, if not, password will be hidden.
    public void showHideChangedPassword(ActionEvent event) {

        if (this.toDoView.getChangePasswordDialogPane().getShowPassword().isSelected()) {

            this.toDoView.getChangePasswordDialogPane().getNewPasswordHBox().getChildren().clear();
            this.toDoView.getChangePasswordDialogPane().getRepeatPasswordHBox().getChildren().clear();

            this.toDoView.getChangePasswordDialogPane().getNewPasswordTextField().setText(
                    this.toDoView.getChangePasswordDialogPane().getNewPasswordField().getText());

            this.toDoView.getChangePasswordDialogPane().getRepeatPasswordTextField().setText(
                    this.toDoView.getChangePasswordDialogPane().getRepeatPasswordField().getText());

            this.toDoView.getChangePasswordDialogPane().getNewPasswordHBox().getChildren().addAll(
                    this.toDoView.getChangePasswordDialogPane().getNewPasswordLabel(),
                    this.toDoView.getChangePasswordDialogPane().getNewPasswordTextField());

            this.toDoView.getChangePasswordDialogPane().getRepeatPasswordHBox().getChildren().addAll(
                    this.toDoView.getChangePasswordDialogPane().getRepeatPasswordLabel(),
                    this.toDoView.getChangePasswordDialogPane().getRepeatPasswordTextField());
        } else {

            this.toDoView.getChangePasswordDialogPane().getNewPasswordHBox().getChildren().clear();
            this.toDoView.getChangePasswordDialogPane().getRepeatPasswordHBox().getChildren().clear();

            this.toDoView.getChangePasswordDialogPane().getNewPasswordField().setText(
                    this.toDoView.getChangePasswordDialogPane().getNewPasswordTextField().getText());

            this.toDoView.getChangePasswordDialogPane().getRepeatPasswordField().setText(
                    this.toDoView.getChangePasswordDialogPane().getRepeatPasswordTextField().getText());

            this.toDoView.getChangePasswordDialogPane().getNewPasswordHBox().getChildren().addAll(
                    this.toDoView.getChangePasswordDialogPane().getNewPasswordLabel(),
                    this.toDoView.getChangePasswordDialogPane().getNewPasswordField());

            this.toDoView.getChangePasswordDialogPane().getRepeatPasswordHBox().getChildren().addAll(
                    this.toDoView.getChangePasswordDialogPane().getRepeatPasswordLabel(),
                    this.toDoView.getChangePasswordDialogPane().getRepeatPasswordField());

        }

    }

    public void showHideRegistrationPW(ActionEvent event) {

        if (this.loginView.getRegistrationDialogPane().getShowPassword().isSelected()) {

            this.loginView.getRegistrationDialogPane().getPasswordPane().getChildren().clear();
            this.loginView.getRegistrationDialogPane().getRepeatPasswordPane().getChildren().clear();

            this.loginView.getRegistrationDialogPane().getPasswordTextField().setText(
                    this.loginView.getRegistrationDialogPane().getPasswordField().getText());

            this.loginView.getRegistrationDialogPane().getRepeatTextField().setText(
                    this.loginView.getRegistrationDialogPane().getRepeatPasswordField().getText());

            this.loginView.getRegistrationDialogPane().getPasswordPane().getChildren().addAll(
                    this.loginView.getRegistrationDialogPane().getPasswordLabel(),
                    this.loginView.getRegistrationDialogPane().getPasswordTextField());

            this.loginView.getRegistrationDialogPane().getRepeatPasswordPane().getChildren().addAll(
                    this.loginView.getRegistrationDialogPane().getRepeatPasswordLabel(),
                    this.loginView.getRegistrationDialogPane().getRepeatTextField());
        } else {

            this.loginView.getRegistrationDialogPane().getPasswordPane().getChildren().clear();
            this.loginView.getRegistrationDialogPane().getRepeatPasswordPane().getChildren().clear();

            this.loginView.getRegistrationDialogPane().getPasswordField().setText(
                    this.loginView.getRegistrationDialogPane().getPasswordTextField().getText());

            this.loginView.getRegistrationDialogPane().getRepeatPasswordField().setText(
                    this.loginView.getRegistrationDialogPane().getRepeatTextField().getText());

            this.loginView.getRegistrationDialogPane().getPasswordPane().getChildren().addAll(
                    this.loginView.getRegistrationDialogPane().getPasswordLabel(),
                    this.loginView.getRegistrationDialogPane().getPasswordField());

            this.loginView.getRegistrationDialogPane().getRepeatPasswordPane().getChildren().addAll(
                    this.loginView.getRegistrationDialogPane().getRepeatPasswordLabel(),
                    this.loginView.getRegistrationDialogPane().getRepeatPasswordField());

        }

    }

    // Thread safe methods
    public synchronized void passItems(ToDo item) {
        this.toDoList.addToDo(item);
        this.updateInstancedSublists();
    }

    // Ping Button event handling
    public void ping(MouseEvent event) {

        // Clear up graphical feedback
        this.toDoView.getPingButton().getStyleClass().add("openFocusTimer");
        this.loginView.getPingButton().getStyleClass().add("openFocusTimer");

        // Send ping command & capture response
        boolean pingWasSuccessFull = this.clientNetworkPlugin.ping();

        // Give graphical feedback
        if(pingWasSuccessFull) {
            if(!this.toDoView.getPingButton().getStyleClass().contains("successfulPing")) {
                this.toDoView.getPingButton().getStyleClass().add("successfulPing");
            }
            if(!this.loginView.getPingButton().getStyleClass().contains("successfulPing")) {
                this.loginView.getPingButton().getStyleClass().add("successfulPing");
            }

        } else {
            if(!this.toDoView.getPingButton().getStyleClass().contains("badPing")) {
                this.toDoView.getPingButton().getStyleClass().add("badPing");
            }
            if(!this.loginView.getPingButton().getStyleClass().contains("badPing")) {
                this.loginView.getPingButton().getStyleClass().add("badPing");
            }

        }

    }

    
}

