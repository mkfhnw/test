package client.view;

import client.model.ToDo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/*
 * This abstract class is the super class
 * of the GUI-side-bar, which changes
 * based on the clicked ListView item
 */
public abstract class MainBarView extends VBox {
	
	// control elements for this container
	private ObservableList<ToDo> subSet;
	protected ImageView icon;
	private final ImageView plus;
	protected Label label;
	private ImageView lupe;
	private TextField searchField;
	private Button searchButton;
	private Button createToDo;
	protected TableView<ToDo> tableView;
	private TableColumn<ToDo, String> important;
	private TableColumn<ToDo, String> task;
	private TableColumn<ToDo, String> dueDate;
	private final TableColumn<ToDo, String> priority;
	private TableColumn<ToDo, String> checkBox;
	private TableColumn<ToDo, String> garbage;
	protected HBox header;
	private HBox searchBar;
	private ObservableList<String> filter;
	private ComboBox<String> dateFilterCombobox;
	
	// Constructor
	public MainBarView() {

		// Add data
		this.subSet = subSet;

		/*
		 * HBox for the icon and label in the
		 * GUI-SideBar (items will be set in
		 * the subclass)
		 */
		this.header = new HBox();
		this.getChildren().add(header);
		
		// Lupe Icon for the searchField		
		this.lupe = new ImageView("/common/resources/lupe.png");
		this.lupe.setFitHeight(15);
		this.lupe.setFitWidth(15);
				
		
		// SearchBar and button for creating a new item
		this.createToDo = new Button();
		this.plus = new ImageView("/common/resources/plusIcon.png");
		this.plus.setFitHeight(15);
		this.plus.setFitWidth(15);
		this.createToDo.setGraphic(plus);
		this.searchBar = new HBox();
		
		/*
		 * A ComboBox for a ToDo filter
		 * helps to see what kind of tasks the user has today,
		 * this week or this month
		 */
		this.filter = FXCollections.observableArrayList(
				"Alle",
				"Heute"
				);
		this.dateFilterCombobox = new ComboBox<>(filter);
		this.searchBar.getChildren().add(dateFilterCombobox);
		
		// Puts the Button and Searchfunction to the right side of the view
		this.searchBar.setPadding(new Insets(0.0, 0.0, 30.0, 800.0));
		this.searchField = new TextField();
		this.searchButton = new Button();
		this.searchButton.setGraphic(this.lupe);
		this.searchBar.getChildren().addAll(createToDo, searchField, searchButton);
		this.getChildren().add(searchBar);
		this.searchField.setMaxWidth(250);

		/*
         * Creates a TableView with Columns
         * and includes data from ObservableArrayList.
         * The setCellValueFactory method specifies a cell factory for each column. 
         */
		this.tableView = new TableView<>();
		this.tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		this.tableView.setEditable(true);
		this.tableView.setPrefHeight(600);

		this.important = new TableColumn<>("Wichtig");
		this.important.setCellValueFactory(new PropertyValueFactory<ToDo, String>("importantButton"));
    	    
		this.task = new TableColumn<>("Aufgabe");
		this.task.setCellValueFactory(new PropertyValueFactory<ToDo, String>("title"));
    		
		this.dueDate = new TableColumn<>("Termin");
		this.dueDate.setCellValueFactory(new PropertyValueFactory<ToDo, String>("dueDateString"));

		this.priority = new TableColumn<>("Priorität");
		this.priority.setCellValueFactory(new PropertyValueFactory<ToDo, String>("priority"));
		
		this.checkBox = new TableColumn<>("Erledigt");
		this.checkBox.setCellValueFactory(new PropertyValueFactory<ToDo, String>("doneButton"));
		
		this.garbage = new TableColumn<>("Papierkorb");
		this.garbage.setCellValueFactory(new PropertyValueFactory<ToDo, String>("garbageButton"));
	
		// Adds Columns to the TableView
		this.tableView.getColumns().addAll(this.important, this.task, this.dueDate, this.priority, this.checkBox, this.garbage);
    	    
		this.getChildren().addAll(tableView);
						
		this.setPrefHeight(600);
	
		
		// Add CSS styling
		this.getStylesheets().add(getClass().getResource("MainBarView.css").toExternalForm());
		this.getStyleClass().add("mainBarView");
		this.lupe.getStyleClass().add("lupe");
		this.searchButton.getStyleClass().add("searchButton");
		this.searchBar.getStyleClass().add("searchField");
		this.createToDo.getStyleClass().add("createToDo");
		this.tableView.getStyleClass().add("tableView");    
		this.priority.getStyleClass().add("priority");
        this.checkBox.getStyleClass().add("checkBox");
        this.task.getStyleClass().add("task");
        this.dueDate.getStyleClass().add("dueDate");
        this.important.getStyleClass().add("important");
        this.garbage.getStyleClass().add("garbage");
        this.important.getStyleClass().add("tableColumn");
        this.task.getStyleClass().add("tableColumn");
        this.dueDate.getStyleClass().add("tableColumn");
        this.priority.getStyleClass().add("tableColumn");
        this.checkBox.getStyleClass().add("tableColumn");
        this.garbage.getStyleClass().add("tableColumn");
        this.dateFilterCombobox.getStyleClass().add("comboBox");
        this.dateFilterCombobox.getStyleClass().add("combo-box");
      		
	}

	public ComboBox<String> getDateFilterCombobox() {
		return dateFilterCombobox;
	}

	public ObservableList<ToDo> getSubSet() {
		return subSet;
	}

	public ImageView getIcon() {
		return icon;
	}

	public ImageView getPlus() {
		return plus;
	}

	public Label getLabel() {
		return label;
	}

	public ImageView getLupe() {
		return lupe;
	}

	public TextField getSearchField() {
		return searchField;
	}

	public Button getSearchButton() {
		return searchButton;
	}

	public Button getCreateToDo() {
		return createToDo;
	}

	public TableView<ToDo> getTableView() {
		return tableView;
	}

	public TableColumn<ToDo, String> getImportant() {
		return important;
	}

	public TableColumn<ToDo, String> getTask() {
		return task;
	}

	public TableColumn<ToDo, String> getDueDate() {
		return dueDate;
	}

	public TableColumn<ToDo, String> getCheckBox() {
		return checkBox;
	}

	public TableColumn<ToDo, String> getGarbage() {
		return garbage;
	}

	public HBox getHeader() {
		return header;
	}

	public HBox getSearchBar() {
		return searchBar;
	}

	public ObservableList<String> getFilter() {
		return filter;
	}

	public void setSubSet(ObservableList<ToDo> subSet) {
		this.subSet = subSet;
	}

	public void setIcon(ImageView icon) {
		this.icon = icon;
	}

	public void setLabel(Label label) {
		this.label = label;
	}

	public void setLupe(ImageView lupe) {
		this.lupe = lupe;
	}

	public void setSearchField(TextField searchField) {
		this.searchField = searchField;
	}

	public void setSearchButton(Button searchButton) {
		this.searchButton = searchButton;
	}

	public void setCreateToDo(Button createToDo) {
		this.createToDo = createToDo;
	}

	public void setTableView(TableView<ToDo> tableView) {
		this.tableView = tableView;
	}

	public void setImportant(TableColumn<ToDo, String> important) {
		this.important = important;
	}

	public void setTask(TableColumn<ToDo, String> task) {
		this.task = task;
	}

	public void setDueDate(TableColumn<ToDo, String> dueDate) {
		this.dueDate = dueDate;
	}

	public void setCheckBox(TableColumn<ToDo, String> checkBox) {
		this.checkBox = checkBox;
	}

	public void setGarbage(TableColumn<ToDo, String> garbage) {
		this.garbage = garbage;
	}

	public void setHeader(HBox header) {
		this.header = header;
	}

	public void setSearchBar(HBox searchBar) {
		this.searchBar = searchBar;
	}

	public void setFilter(ObservableList<String> filter) {
		this.filter = filter;
	}

	public void setDateFilterCombobox(ComboBox<String> dateFilterCombobox) {
		this.dateFilterCombobox = dateFilterCombobox;
	}

	
	
}
	

