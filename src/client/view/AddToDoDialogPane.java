package client.view;

import client.ClientNetworkPlugin;
import client.model.Priority;
import client.model.ToDo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;


public class AddToDoDialogPane extends DialogPane {

    // Components
    private BorderPane root;
    private VBox leftPane;
    private VBox rightPane;
    private HBox titleBar;
    private HBox categoryBar;
    private HBox dueDateBar;
    private HBox priorityBar;
    private HBox tagsBar;
    private VBox headerBar;
    private final HBox notice;
    private VBox topBar;
    private VBox space;

    private Label newTaskLabel;
    private Label tippLabel;
    private Label titleLabel;
    private Label categoryLabel;
    private Label dueDateLabel;
    private Label priorityLabel;
    private Label messageLabel;
    private Label tagsLabel;
    private final Label noticeLabel;
    
    private final ImageView attention;

    private TextField titleTextfield;
    private TextField tagsTextfield;

    private Tooltip titleToolTip;
    private Tooltip messageToolTip;
    private Tooltip categoryToolTip;
    private Tooltip dateToolTip;
    private Tooltip priorityTip;
    private Tooltip tagsToolTip;

    private ComboBox<String> categoryComboBox;
    private DatePicker datePicker;
    private TextArea messageTextArea;
    private ComboBox<Priority> priorityComboBox;
    
    private final ClientNetworkPlugin clientNetworkPlugin;

    // Custom button type for eventhandling
    ButtonType okButtonType;

    // Fields
    private final int SPACING_CATEGORYBAR = 8;
    private final int SPACING_TITLEBAR = 43;
    private final int SPACING_DUEDATEBAR = 26;
    private final int SPACING_PRIORITYBAR = 18;
    private final int SPACING_TAGSBAR = 40;
    private final int SPACING_HEADERBAR = -10;
    private final int SPACE = 20;
    
    private final Duration DURATION_UNTIL_SHOW = Duration.seconds(0.2);


    // Constructor
    public AddToDoDialogPane(ObservableList<String> listViewItems, ClientNetworkPlugin clientNetworkPlugin) {
    	
    	this.clientNetworkPlugin = clientNetworkPlugin;

        // Instantiate components
        root = new BorderPane();
        leftPane = new VBox();
        rightPane = new VBox();
        titleBar = new HBox(SPACING_TITLEBAR);
        categoryBar = new HBox(SPACING_CATEGORYBAR);
        dueDateBar = new HBox(SPACING_DUEDATEBAR);
        priorityBar = new HBox(SPACING_PRIORITYBAR);
        tagsBar = new HBox(SPACING_TAGSBAR);
        headerBar = new VBox(SPACING_HEADERBAR);
        notice = new HBox();
        topBar = new VBox();
        space = new VBox(SPACE);
        
        newTaskLabel = new Label("Neue Aufgabe");
        newTaskLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        newTaskLabel.setTextFill(Color.web("#181C54"));
        
        titleLabel = new Label("Titel");
        titleLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        titleLabel.setTextFill(Color.web("#181C54"));
        
        categoryLabel = new Label("Kategorie");
        categoryLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        categoryLabel.setTextFill(Color.web("#181C54"));
        
        dueDateLabel = new Label("Termin");
        dueDateLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        dueDateLabel.setTextFill(Color.web("#181C54"));
        
        priorityLabel = new Label("Priorität");
        priorityLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        priorityLabel.setTextFill(Color.web("#181C54"));
        
        messageLabel = new Label("Beschreibung");
        messageLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        messageLabel.setTextFill(Color.web("#181C54"));
        
        tagsLabel = new Label("Tags");
        tippLabel = new Label("Bewegen Sie Ihren Mauszeiger über einen Schriftzug!");
        tippLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, 11));
        tippLabel.setTextFill(Color.web("#181C54"));
        
        noticeLabel = new Label("​Kategorien für ein ToDo können nur auf dem zur Applikation dazugehörigen Server gespeichert werden.");
        noticeLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, 11));
        noticeLabel.setTextFill(Color.web("BLACK"));
        
        attention = new ImageView("/common/resources/attention.png");
		attention.setFitHeight(15);
		attention.setFitWidth(15);

        titleTextfield = new TextField();
        titleTextfield.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));
        tagsTextfield = new TextField();
        tagsTextfield.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));

        // Instantiate tooltips
        titleToolTip = new Tooltip("Ihr Titel muss zwischen 3 - 20 Zeichen lang sein.");
        messageToolTip = new Tooltip("Ihre Beschreibung muss < 255 Zeichen lang sein.");
        categoryToolTip = new Tooltip("Die Kategorie ist optional.");
        dateToolTip = new Tooltip("Ihr Datum muss im Format YYYY.MM.DD sein und in der Zukunft liegen.");
        priorityTip = new Tooltip("Die Priorität muss einen Wert enthalten.");
        tagsToolTip = new Tooltip("Ihre Tags müssen einzelne Wörter sein, separiert mit einem Semikolon (;).");

        // Change tooltip timers
        titleToolTip.setShowDelay(DURATION_UNTIL_SHOW);
        messageToolTip.setShowDelay(DURATION_UNTIL_SHOW);
        categoryToolTip.setShowDelay(DURATION_UNTIL_SHOW);
        dateToolTip.setShowDelay(DURATION_UNTIL_SHOW);
        priorityTip.setShowDelay(DURATION_UNTIL_SHOW);
        tagsToolTip.setShowDelay(DURATION_UNTIL_SHOW);

        // Instantiate the rest of the items, remove "Papierkorb" from selectable category
        categoryComboBox = new ComboBox<>();
        ObservableList<String> copy = FXCollections.observableArrayList();
        copy.addAll(listViewItems);
        if(this.clientNetworkPlugin.isConnectedToPrivateServer()) {
            copy.remove(3);
        }
        categoryComboBox.setItems(copy);
        
        
        // If Server is not "localhost", the category CheckBox and Label will be disabled and a note will be shown.
        if (!this.clientNetworkPlugin.isConnectedToPrivateServer()) {
        	this.categoryComboBox.setDisable(true);
        	this.categoryLabel.setDisable(true);
            this.notice.getChildren().addAll(attention, noticeLabel);
        }

                
        datePicker = new DatePicker();
        messageTextArea = new TextArea();
        messageTextArea.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));

        // ComboBox for Priority
        priorityComboBox = new ComboBox<>();
        Priority[] priorities = Priority.values();
        Priority[] validPriorities = new Priority[3];
        validPriorities[0] = priorities[0];
        validPriorities[1] = priorities[1];
        validPriorities[2] = priorities[2];
        priorityComboBox.setItems(FXCollections.observableArrayList(validPriorities));
        priorityComboBox.setValue(Priority.Low);
        
        // Fill controls into containers
        titleBar.getChildren().addAll(titleLabel, titleTextfield);
        categoryBar.getChildren().addAll(categoryLabel, categoryComboBox);
        dueDateBar.getChildren().addAll(dueDateLabel, datePicker);
        priorityBar.getChildren().addAll(priorityLabel, priorityComboBox);
        tagsBar.getChildren().addAll(tagsLabel, tagsTextfield);
        headerBar.getChildren().addAll(newTaskLabel, tippLabel);
        topBar.getChildren().addAll(notice, headerBar);
        
        leftPane.getChildren().addAll(space, titleBar, categoryBar, dueDateBar, priorityBar);
        rightPane.getChildren().addAll(messageLabel, messageTextArea);

        // Set containers
        root.setTop(topBar);
        root.setLeft(leftPane);
        root.setRight(rightPane);

        // Associate tooltips
        titleLabel.setTooltip(titleToolTip);
        messageLabel.setTooltip(messageToolTip);
        categoryLabel.setTooltip(categoryToolTip);
        dueDateLabel.setTooltip(dateToolTip);
        priorityLabel.setTooltip(priorityTip);
        // tagsLabel.setTooltip(tagsToolTip);
        
        
        // Add CSS styling
        this.getStylesheets().add(getClass().getResource("DialogPaneStyleSheet.css").toExternalForm());
        this.root.getStyleClass().add("root");
        this.leftPane.getStyleClass().add("leftPane");
        this.rightPane.getStyleClass().add("rightPane");
        this.newTaskLabel.getStyleClass().add("newTaskLabel");
        this.tippLabel.getStyleClass().add("tippLabel");
        this.titleLabel.getStyleClass().add("titleLabel");
        this.categoryLabel.getStyleClass().add("categoryLabel");
        this.dueDateLabel.getStyleClass().add("dueDateLabel");
        this.messageLabel.getStyleClass().add("messageLabel");
        this.tagsLabel.getStyleClass().add("tagsLabel");
        this.messageTextArea.getStyleClass().add("messageTextArea");
        this.priorityLabel.getStyleClass().add("priorityLabel");
        this.notice.getStyleClass().add("notice");
        this.categoryComboBox.getStyleClass().add("comboBox");
        this.categoryComboBox.getStyleClass().add("combo-box");
        this.priorityComboBox.getStyleClass().add("comboBox");
        this.priorityComboBox.getStyleClass().add("combo-box");
        this.datePicker.getStyleClass().add("date-picker");

        // Word wrap
        this.messageTextArea.setWrapText(true);
        

        // Add buttonTypes
        okButtonType = new ButtonType("Erstellen", ButtonBar.ButtonData.OK_DONE);
        this.getButtonTypes().add(new ButtonType("Abbrechen", ButtonBar.ButtonData.CANCEL_CLOSE));
        this.getButtonTypes().add(okButtonType);
        this.getStylesheets().add(getClass().getResource("FocusAndHowToDialogPaneStyleSheet.css").toExternalForm());

        // Set content
        this.setContent(root);
        this.setContentText("Neue Aufgabe");

    }

    // Constructor overload method used for updating an item
    public AddToDoDialogPane(ObservableList<String> listViewItems, ToDo todo, ClientNetworkPlugin clientNetworkPlugin) {

        // Set clientNetworkPlugin
        this.clientNetworkPlugin = clientNetworkPlugin;

        // Instantiate components
        root = new BorderPane();
        leftPane = new VBox();
        rightPane = new VBox();
        titleBar = new HBox(SPACING_TITLEBAR);
        categoryBar = new HBox(SPACING_CATEGORYBAR);
        dueDateBar = new HBox(SPACING_DUEDATEBAR);
        priorityBar = new HBox(SPACING_PRIORITYBAR);
        headerBar = new VBox(SPACING_HEADERBAR);

        newTaskLabel = new Label(todo.getTitle());
        newTaskLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        newTaskLabel.setTextFill(Color.web("#181C54"));
        
        titleLabel = new Label("Titel");
        titleLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        titleLabel.setTextFill(Color.web("#181C54"));
        
        categoryLabel = new Label("Kategorie");
        categoryLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        categoryLabel.setTextFill(Color.web("#181C54"));
        
        dueDateLabel = new Label("Termin");
        dueDateLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        dueDateLabel.setTextFill(Color.web("#181C54"));
        
        messageLabel = new Label("Beschreibung");
        messageLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        messageLabel.setTextFill(Color.web("#181C54"));
        
        priorityLabel = new Label("Priorität");
        priorityLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        priorityLabel.setTextFill(Color.web("#181C54"));
        
        tippLabel = new Label("Bewegen Sie Ihren Mauszeiger über einen Schriftzug!");
        tippLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, 11));
        tippLabel.setTextFill(Color.web("#181C54"));

        titleTextfield = new TextField();

        // Instantiate tooltips
        titleToolTip = new Tooltip("Ihr Titel muss zwischen 3 - 20 Zeichen lang sein.");
        messageToolTip = new Tooltip("Ihre Beschreibung muss < 255 Zeichen lang sein.");
        categoryToolTip = new Tooltip("Die Kategorie muss einen Wert enthalten.");
        dateToolTip = new Tooltip("Ihr Datum muss im Format DD.MM.YYYY sein und in der Zukunft liegen.");
        priorityTip = new Tooltip("Die Priorität muss einen Wert enthalten.");

        // Change tooltip timers
        titleToolTip.setShowDelay(DURATION_UNTIL_SHOW);
        messageToolTip.setShowDelay(DURATION_UNTIL_SHOW);
        categoryToolTip.setShowDelay(DURATION_UNTIL_SHOW);
        dateToolTip.setShowDelay(DURATION_UNTIL_SHOW);
        priorityTip.setShowDelay(DURATION_UNTIL_SHOW);

        // Remove Papierkorb
        ObservableList<String> copy = FXCollections.observableArrayList();
        copy.addAll(listViewItems);
        if(this.clientNetworkPlugin.isConnectedToPrivateServer()) {
            copy.remove(3);
        }

        // Instantiate the rest of the items
        categoryComboBox = new ComboBox<>();
        categoryComboBox.setItems(copy);
        datePicker = new DatePicker();
        messageTextArea = new TextArea();

        // Instantiate combobox
        priorityComboBox = new ComboBox<>();
        Priority[] priorities = Priority.values();
        Priority[] validPriorities = new Priority[3];
        validPriorities[0] = priorities[0];
        validPriorities[1] = priorities[1];
        validPriorities[2] = priorities[2];
        priorityComboBox.setItems(FXCollections.observableArrayList(validPriorities));
        priorityComboBox.setValue(Priority.Low);

        // Notification
        noticeLabel = new Label("Diese Darstellung dient nur zur Ansicht. Informationen können nicht geändert werden.");
        noticeLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, 11));
        noticeLabel.setTextFill(Color.web("BLACK"));

        attention = new ImageView("/common/resources/attention.png");
        attention.setFitHeight(15);
        attention.setFitWidth(15);

        this.notice = new HBox();
        this.notice.getChildren().addAll(attention, noticeLabel);
        this.notice.getStyleClass().add("viewOnlyNotice");

        // Fill controls into containers
        titleBar.getChildren().addAll(titleLabel, titleTextfield);
        categoryBar.getChildren().addAll(categoryLabel, categoryComboBox);
        dueDateBar.getChildren().addAll(dueDateLabel, datePicker);
        priorityBar.getChildren().addAll(priorityLabel, priorityComboBox);
        headerBar.getChildren().addAll(newTaskLabel, notice);

        leftPane.getChildren().addAll(titleBar, categoryBar, dueDateBar, priorityBar);
        rightPane.getChildren().addAll(messageLabel, messageTextArea);

        // Set containers
        root.setTop(headerBar);
        root.setLeft(leftPane);
        root.setRight(rightPane);

        // Associate tooltips
        titleLabel.setTooltip(titleToolTip);
        messageLabel.setTooltip(messageToolTip);
        categoryLabel.setTooltip(categoryToolTip);
        dueDateLabel.setTooltip(dateToolTip);
        priorityLabel.setTooltip(priorityTip);

        // Fill fields
        titleTextfield.setText(todo.getTitle());
        titleTextfield.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));
        
        datePicker.getEditor().setText(todo.getDueDateString());

        // Fill category combobox depending on what category the item has
        categoryComboBox.getEditor().setText(todo.getCategory());
        if(todo.getCategory() != null && todo.getCategory().equals("Wichtig")) { categoryComboBox.getSelectionModel().select(0); }
        if(todo.getCategory() != null && todo.getCategory().equals("Geplant")) { categoryComboBox.getSelectionModel().select(1); }
        if(todo.getCategory() != null && todo.getCategory().equals("Erledigt")) { categoryComboBox.getSelectionModel().select(2); }


        // Debugging tag string - if it's empty it will insert a semicolon
        messageTextArea.setText(todo.getMessage());
        messageTextArea.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));

        // Add CSS styling
        this.getStylesheets().add(getClass().getResource("DialogPaneStyleSheet.css").toExternalForm());
        this.root.getStyleClass().add("root");
        this.leftPane.getStyleClass().add("leftPane");
        this.rightPane.getStyleClass().add("rightPane");
        this.newTaskLabel.getStyleClass().add("newTaskLabel");
        this.tippLabel.getStyleClass().add("tippLabel");
        this.titleLabel.getStyleClass().add("titleLabel");
        this.categoryLabel.getStyleClass().add("categoryLabel");
        this.dueDateLabel.getStyleClass().add("dueDateLabel");
        this.messageLabel.getStyleClass().add("messageLabel");
        this.priorityLabel.getStyleClass().add("priorityLabel");
        this.messageTextArea.getStyleClass().add("messageTextArea");
        this.datePicker.getStyleClass().add("date-picker");
        this.categoryComboBox.getStyleClass().add("comboBox");
        this.categoryComboBox.getStyleClass().add("combo-box");
        this.priorityComboBox.getStyleClass().add("comboBox");
        this.priorityComboBox.getStyleClass().add("combo-box");

        // Word wrap
        this.messageTextArea.setWrapText(true);

        // Add buttonTypes
        okButtonType = new ButtonType("Erstellen", ButtonBar.ButtonData.OK_DONE);
        this.getButtonTypes().add(new ButtonType("Schliessen", ButtonBar.ButtonData.CANCEL_CLOSE));
        this.getButtonTypes().add(okButtonType);
        this.lookupButton(okButtonType).setDisable(true);

        // Set content
        this.setContent(root);

    }

    // Clearing method
    public void clearPane() {
        this.titleTextfield.clear();
        this.categoryComboBox.valueProperty().setValue(null);
        this.datePicker.getEditor().clear();
        this.messageTextArea.clear();
    }

    // Disabled all controls
    public void disableAllControls() {
        this.titleTextfield.setDisable(true);
        this.titleLabel.setDisable(true);
        this.categoryComboBox.setDisable(true);
        this.categoryLabel.setDisable(true);
        this.datePicker.setDisable(true);
        this.dueDateLabel.setDisable(true);
        this.messageTextArea.setDisable(true);
        this.messageLabel.setDisable(true);
        this.priorityComboBox.setDisable(true);
        this.priorityLabel.setDisable(true);
        this.tippLabel.setText("");
    }

	public BorderPane getRoot() {
		return root;
	}

	public VBox getLeftPane() {
		return leftPane;
	}

	public VBox getRightPane() {
		return rightPane;
	}

	public HBox getTitleBar() {
		return titleBar;
	}

	public HBox getCategoryBar() {
		return categoryBar;
	}

	public HBox getDueDateBar() {
		return dueDateBar;
	}

	public HBox getTagsBar() {
		return tagsBar;
	}

	public VBox getHeaderBar() {
		return headerBar;
	}

	public Label getNewTaskLabel() {
		return newTaskLabel;
	}

	public Label getTippLabel() {
		return tippLabel;
	}

	public Label getTitleLabel() {
		return titleLabel;
	}

	public Label getCategoryLabel() {
		return categoryLabel;
	}

	public Label getDueDateLabel() {
		return dueDateLabel;
	}

	public Label getMessageLabel() {
		return messageLabel;
	}

	public Label getTagsLabel() {
		return tagsLabel;
	}

	public TextField getTitleTextfield() {
		return titleTextfield;
	}

	public TextField getTagsTextfield() {
		return tagsTextfield;
	}

	public Tooltip getTitleToolTip() {
		return titleToolTip;
	}

	public Tooltip getMessageToolTip() {
		return messageToolTip;
	}

	public Tooltip getCategoryToolTip() {
		return categoryToolTip;
	}

	public Tooltip getDateToolTip() {
		return dateToolTip;
	}

	public Tooltip getTagsToolTip() {
		return tagsToolTip;
	}

	public ComboBox<String> getCategoryComboBox() {
		return categoryComboBox;
	}

	public DatePicker getDatePicker() {
		return datePicker;
	}

	public TextArea getMessageTextArea() {
		return messageTextArea;
	}

	public ButtonType getOkButtonType() {
		return okButtonType;
	}

	public int getSPACING_CATEGORYBAR() {
		return SPACING_CATEGORYBAR;
	}

	public int getSPACING_TITLEBAR() {
		return SPACING_TITLEBAR;
	}

	public int getSPACING_DUEDATEBAR() {
		return SPACING_DUEDATEBAR;
	}

	public int getSPACING_TAGSBAR() {
		return SPACING_TAGSBAR;
	}

	public int getSPACING_HEADERBAR() {
		return SPACING_HEADERBAR;
	}

	public Duration getDURATION_UNTIL_SHOW() {
		return DURATION_UNTIL_SHOW;
	}

	public void setRoot(BorderPane root) {
		this.root = root;
	}

	public void setLeftPane(VBox leftPane) {
		this.leftPane = leftPane;
	}

	public void setRightPane(VBox rightPane) {
		this.rightPane = rightPane;
	}

	public void setTitleBar(HBox titleBar) {
		this.titleBar = titleBar;
	}

	public void setCategoryBar(HBox categoryBar) {
		this.categoryBar = categoryBar;
	}

	public void setDueDateBar(HBox dueDateBar) {
		this.dueDateBar = dueDateBar;
	}

	public void setTagsBar(HBox tagsBar) {
		this.tagsBar = tagsBar;
	}

	public void setHeaderBar(VBox headerBar) {
		this.headerBar = headerBar;
	}

	public void setNewTaskLabel(Label newTaskLabel) {
		this.newTaskLabel = newTaskLabel;
	}

	public void setTippLabel(Label tippLabel) {
		this.tippLabel = tippLabel;
	}

	public void setTitleLabel(Label titleLabel) {
		this.titleLabel = titleLabel;
	}

	public void setCategoryLabel(Label categoryLabel) {
		this.categoryLabel = categoryLabel;
	}

	public void setDueDateLabel(Label dueDateLabel) {
		this.dueDateLabel = dueDateLabel;
	}

	public void setMessageLabel(Label messageLabel) {
		this.messageLabel = messageLabel;
	}

	public void setTagsLabel(Label tagsLabel) {
		this.tagsLabel = tagsLabel;
	}

	public void setTitleTextfield(TextField titleTextfield) {
		this.titleTextfield = titleTextfield;
	}

	public void setTagsTextfield(TextField tagsTextfield) {
		this.tagsTextfield = tagsTextfield;
	}

	public void setTitleToolTip(Tooltip titleToolTip) {
		this.titleToolTip = titleToolTip;
	}

	public void setMessageToolTip(Tooltip messageToolTip) {
		this.messageToolTip = messageToolTip;
	}

	public void setCategoryToolTip(Tooltip categoryToolTip) {
		this.categoryToolTip = categoryToolTip;
	}

	public void setDateToolTip(Tooltip dateToolTip) {
		this.dateToolTip = dateToolTip;
	}

	public void setTagsToolTip(Tooltip tagsToolTip) {
		this.tagsToolTip = tagsToolTip;
	}

	public void setCategoryComboBox(ComboBox<String> categoryComboBox) {
		this.categoryComboBox = categoryComboBox;
	}

	public void setDatePicker(DatePicker datePicker) {
		this.datePicker = datePicker;
	}

	public void setMessageTextArea(TextArea messageTextArea) {
		this.messageTextArea = messageTextArea;
	}

	public void setOkButtonType(ButtonType okButtonType) {
		this.okButtonType = okButtonType;
	}

	public HBox getPriorityBar() {
		return priorityBar;
	}

	public Label getPriorityLabel() {
		return priorityLabel;
	}

	public Tooltip getPriorityTip() {
		return priorityTip;
	}

	public ComboBox<Priority> getPriorityComboBox() {
		return priorityComboBox;
	}

	public int getSPACING_PRIORITYBAR() {
		return SPACING_PRIORITYBAR;
	}

	public void setPriorityBar(HBox priorityBar) {
		this.priorityBar = priorityBar;
	}

	public void setPriorityLabel(Label priorityLabel) {
		this.priorityLabel = priorityLabel;
	}

	public void setPriorityTip(Tooltip priorityTip) {
		this.priorityTip = priorityTip;
	}

	public void setPriorityComboBox(ComboBox<Priority> priorityComboBox) {
		this.priorityComboBox = priorityComboBox;
	}



}
