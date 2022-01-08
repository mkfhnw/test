package client.view;

import client.model.ToDo;
import client.model.ToDoList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ToDoView extends BorderPane {
	
	// Control elements for this container
	
		private ToDo toDoModel;
		private ToDoList toDoListModel;

		private ListView <String> listView;
		private VBox vBox;
		private BorderPane borderPane;
		private SplitPane splitPane;
		private StackPane stackPane;
		
		private final Label loggedOnUser;
		
		private Dialog<ButtonType> addToDoDialog;
		private AddToDoDialogPane toDoDialogPane;
		
		final static String done = "Erledigt";
		final static String undone = "Geplant";
				
		private final CategoryAxis xAxis;
		private final NumberAxis yAxis;
		private BarChart<String, Number> bc;
		private XYChart.Series serie1;
		private XYChart.Series serie2;
		
		private Dialog<ButtonType> focusDialog;
		private FocusTimerDialogPane focusTimerDialog;
		private Button openFocusTimer;

		private final Button pingButton;
		
		private Dialog<ButtonType> howToDialog;
		private HowToDialogPane howToDialogPane;
		
		private Dialog<ButtonType> changePasswordDialog;
		private ChangePasswordDialogPane changePasswordDialogPane;
				
		private MenuItem changePasswordItem;
		private MenuItem howToItem;
		private Menu settings;
		private Menu help;
		private MenuBar menuBar;
		
		private final ImageView imageSetting;
		private final ImageView imageHelp;
		private final ImageView imageChangePassword;
		private final ImageView imageHowTo;
		
		private HBox hBoxHowTo;
		private VBox vBoxBottom;
		private HBox hBoxBottom;
		
		private Button logoutButton;

		/*
		 * Instantiates all necessary control elements
		 * and adds them to the container
		 */
		public ToDoView(ToDo toDoModel, ToDoList toDoListModel, LoginView loginView) {
			
			// Instantiates our classes
			this.toDoModel = toDoModel;
			this.toDoListModel = toDoListModel;
			
			this.loggedOnUser = new Label("");
			
			
			// Creates a ListView with items and sets the active item		
			this.listView = new ListView<String>();
			listView.getItems().addAll(
					"Wichtig",
					"Geplant",
					"Erledigt",
					"Papierkorb");
			listView.getSelectionModel().select(1);
			
			
			// Creates a VBox in the BorderPane and includes the listView
			
			this.vBox = new VBox();
			this.vBox.getChildren().addAll(listView);
			this.setLeft(this.vBox);
			
			/*
			 * Creates a BorderPane in a BorderPane
			 * This is for the view on the right side
			 */
			this.borderPane = new BorderPane();
			this.setCenter(borderPane);
			this.borderPane.setPrefSize(1000, 600);
			

			/*
			 * Creates a SplitPane between vBox and borderPane
			 * This SplitPane should divide the GUI in two
			 * main views (List on the left, View on the right)
			 */
			this.splitPane = new SplitPane();
			this.splitPane.getItems().addAll(vBox, borderPane);
			this.splitPane.setDividerPositions(0.22);
			this.setLeft(splitPane);

			VBox buffer = new VBox();
			buffer.setPrefHeight(60.0);
			this.vBox.getChildren().add(buffer);
			
			//Creating the BarChart to show the done and undone ToDo's
			this.xAxis = new CategoryAxis();
			this.yAxis = new NumberAxis();
			this.bc = new BarChart<String, Number>(xAxis, yAxis);
			
			bc.setTitle("Status Überblick");
			xAxis.setLabel("Kategorie");
			yAxis.setLabel("Anzahl");
			bc.setAnimated(false);
			
			this.serie1 = new XYChart.Series();
			serie1.setName(done);
			this.serie2 = new XYChart.Series<>();
			serie2.setName(undone);	
			
						
			this.vBox.getChildren().add(bc);
			
			// Menu for settings and help
			this.changePasswordItem = new MenuItem("Passwort ändern");
			this.imageChangePassword = new ImageView("/common/resources/change.png");
			this.imageChangePassword.setFitHeight(20);
			this.imageChangePassword.setFitWidth(20);
			this.changePasswordItem.setGraphic(imageChangePassword);
			
			this.howToItem = new MenuItem("How To");
			this.imageHowTo = new ImageView("/common/resources/howTo.png");
			this.imageHowTo.setFitHeight(20);
			this.imageHowTo.setFitWidth(20);
			this.howToItem.setGraphic(imageHowTo);
			
			this.settings = new Menu("Einstellungen");
			this.imageSetting = new ImageView("/common/resources/einstellungen.png");
			this.imageSetting.setFitHeight(20);
			this.imageSetting.setFitWidth(20);
			this.settings.setGraphic(imageSetting);
			
			this.help = new Menu("Hilfe");
			this.imageHelp = new ImageView("/common/resources/help.png");
			this.imageHelp.setFitHeight(20);
			this.imageHelp.setFitWidth(20);
			this.help.setGraphic(imageHelp);
			
			this.settings.getItems().add(changePasswordItem);
			this.help.getItems().add(howToItem);
			
			this.menuBar = new MenuBar();
			menuBar.getMenus().addAll(settings, help);
			
			this.setTop(menuBar);
			
			/*
			 * Button Focus timer for a focus timer dialog
			 * on the right side of the bottom of the BorderPane
			 */
			this.openFocusTimer = new Button("Fokus Timer");
			this.logoutButton = new Button("Abmelden");
			this.pingButton = new Button("Ping");
			
			this.vBoxBottom = new VBox();
			HBox hBoxContainer = new HBox();
			hBoxContainer.setAlignment(Pos.CENTER);
			hBoxContainer.setSpacing(30);
			hBoxContainer.getChildren().addAll(openFocusTimer, logoutButton, pingButton);
			
			this.vBoxBottom.getChildren().addAll(loggedOnUser, hBoxContainer);
			this.vBoxBottom.setPadding(new Insets(50.0, 00.0, 50.0, 50.0));
			this.vBoxBottom.setSpacing(30);
			this.vBoxBottom.setAlignment(Pos.CENTER);
			
			this.borderPane.setBottom(vBoxBottom);
		    
			// Add CSS styling
			this.getStylesheets().add(getClass().getResource("ToDoViewStyleSheet.css").toExternalForm());
			this.getStyleClass().add("view");
			this.listView.getStylesheets().add(getClass().getResource("ListViewStyleSheet.css").toExternalForm());
			this.vBox.getStyleClass().add("vBox");
			this.splitPane.getStyleClass().add("splitPane");
			this.borderPane.getStyleClass().add("borderPane");
			this.openFocusTimer.getStyleClass().add("openFocusTimer");
			this.pingButton.getStyleClass().add("openFocusTimer");
			this.bc.getStylesheets().add(getClass().getResource("BarChartStyleSheet.css").toExternalForm());
			this.logoutButton.getStyleClass().add("logoutButton");
			this.loggedOnUser.getStyleClass().add("userLabel");
			this.settings.getStyleClass().add("menuBar");
			this.changePasswordItem.getStyleClass().add("menuBar");
			this.help.getStyleClass().add("menuBar");
			this.howToItem.getStyleClass().add("menuBar");
			
			
	        
			
			// Create and customize Focus timer - Dialog
			this.focusDialog = new Dialog<ButtonType>();
			this.focusDialog.setTitle("Fokus Timer");
			
			Stage stage = (Stage) focusDialog.getDialogPane().getScene().getWindow();
			stage.getIcons().add(new Image(this.getClass().getResource("/common/resources/timer.png").toString()));
			
			this.focusTimerDialog = new FocusTimerDialogPane();
			this.focusDialog.setDialogPane(focusTimerDialog);
			this.focusDialog.initModality(Modality.NONE);
			
			// Create and costumize HowTo Dialog
			this.howToDialog = new Dialog<ButtonType>();
			this.howToDialog.setTitle("How-To");
			Stage stage2 = (Stage) howToDialog.getDialogPane().getScene().getWindow();
			stage2.getIcons().add(new Image(this.getClass().getResource("/common/resources/howTo.png").toString()));
			
			this.howToDialogPane = new HowToDialogPane();
			this.howToDialog.setDialogPane(howToDialogPane);
			
			this.howToDialog.initModality(Modality.NONE);
			
			// Create and costumize ChangePasssword Dialog
			this.changePasswordDialog = new Dialog<ButtonType>();
			this.changePasswordDialog.setTitle("Passwort ändern");
			Stage stage4 = (Stage) changePasswordDialog.getDialogPane().getScene().getWindow();
			stage4.getIcons().add(new Image(this.getClass().getResource("/common/resources/change.png").toString()));
			
			this.changePasswordDialogPane = new ChangePasswordDialogPane();
			this.changePasswordDialog.setDialogPane(changePasswordDialogPane);
			
			this.changePasswordDialog.initModality(Modality.APPLICATION_MODAL);
			


		}

		public ToDo getToDoModel() {
			return toDoModel;
		}

		public ToDoList getToDoListModel() {
			return toDoListModel;
		}

		public ListView<String> getListView() {
			return listView;
		}

		public VBox getvBox() {
			return vBox;
		}

		public BorderPane getBorderPane() {
			return borderPane;
		}

		public SplitPane getSplitPane() {
			return splitPane;
		}

		public StackPane getStackPane() {
			return stackPane;
		}

		public Dialog<ButtonType> getAddToDoDialog() {
			return addToDoDialog;
		}

		public AddToDoDialogPane getToDoDialogPane() {
			return toDoDialogPane;
		}

		public static String getDone() {
			return done;
		}

		public static String getUndone() {
			return undone;
		}

		public CategoryAxis getxAxis() {
			return xAxis;
		}

		public NumberAxis getyAxis() {
			return yAxis;
		}

		public BarChart<String, Number> getBc() {
			return bc;
		}

		public XYChart.Series getSerie1() {
			return serie1;
		}

		public XYChart.Series getSerie2() {
			return serie2;
		}

		public Dialog<ButtonType> getFocusDialog() {
			return focusDialog;
		}

		public FocusTimerDialogPane getFocusTimerDialog() {
			return focusTimerDialog;
		}

		public Button getOpenFocusTimer() {
			return openFocusTimer;
		}

		public Dialog<ButtonType> getHowToDialog() {
			return howToDialog;
		}

		public HowToDialogPane getHowToDialogPane() {
			return howToDialogPane;
		}
		
		public Button getLogoutButton() {
			return logoutButton;
		}

		public Button getPingButton() { return this.pingButton; }

		public HBox gethBoxHowTo() {
			return hBoxHowTo;
		}

		public VBox getvBoxBottom() {
			return vBoxBottom;
		}

		public HBox gethBoxBottom() {
			return hBoxBottom;
		}

		public void setToDoModel(ToDo toDoModel) {
			this.toDoModel = toDoModel;
		}

		public void setToDoListModel(ToDoList toDoListModel) {
			this.toDoListModel = toDoListModel;
		}

		public void setListView(ListView<String> listView) {
			this.listView = listView;
		}

		public void setvBox(VBox vBox) {
			this.vBox = vBox;
		}

		public void setBorderPane(BorderPane borderPane) {
			this.borderPane = borderPane;
		}

		public void setSplitPane(SplitPane splitPane) {
			this.splitPane = splitPane;
		}

		public void setStackPane(StackPane stackPane) {
			this.stackPane = stackPane;
		}

		public void setAddToDoDialog(Dialog<ButtonType> addToDoDialog) {
			this.addToDoDialog = addToDoDialog;
		}

		public void setToDoDialogPane(AddToDoDialogPane toDoDialogPane) {
			this.toDoDialogPane = toDoDialogPane;
		}

		public void setBc(BarChart<String, Number> bc) {
			this.bc = bc;
		}

		public void setSerie1(XYChart.Series serie1) {
			this.serie1 = serie1;
		}

		public void setSerie2(XYChart.Series serie2) {
			this.serie2 = serie2;
		}

		public void setFocusDialog(Dialog<ButtonType> focusDialog) {
			this.focusDialog = focusDialog;
		}

		public void setFocusTimerDialog(FocusTimerDialogPane focusTimerDialog) {
			this.focusTimerDialog = focusTimerDialog;
		}

		public void setOpenFocusTimer(Button openFocusTimer) {
			this.openFocusTimer = openFocusTimer;
		}

		public void setHowToDialog(Dialog<ButtonType> howToDialog) {
			this.howToDialog = howToDialog;
		}

		public void setHowToDialogPane(HowToDialogPane howToDialogPane) {
			this.howToDialogPane = howToDialogPane;
		}

		public void sethBoxHowTo(HBox hBoxHowTo) {
			this.hBoxHowTo = hBoxHowTo;
		}

		public void setvBoxBottom(VBox vBoxBottom) {
			this.vBoxBottom = vBoxBottom;
		}

		public void sethBoxBottom(HBox hBoxBottom) {
			this.hBoxBottom = hBoxBottom;
		}

		public Dialog<ButtonType> getChangePasswordDialog() {
			return changePasswordDialog;
		}

		public ChangePasswordDialogPane getChangePasswordDialogPane() {
			return changePasswordDialogPane;
		}

		public void setChangePasswordDialog(Dialog<ButtonType> changePasswordDialog) {
			this.changePasswordDialog = changePasswordDialog;
		}

		public void setChangePasswordDialogPane(ChangePasswordDialogPane changePasswordDialogPane) {
			this.changePasswordDialogPane = changePasswordDialogPane;
		}

		public void setLogoutButton(Button logoutButton) {
			this.logoutButton = logoutButton;
		}

		public MenuItem getChangePasswordItem() {
			return changePasswordItem;
		}

		public MenuItem getHowToItem() {
			return howToItem;
		}

		public Menu getSettings() {
			return settings;
		}

		public Menu getHelp() {
			return help;
		}

		public MenuBar getMenuBar() {
			return menuBar;
		}

		public void setChangePasswordItem(MenuItem changePasswordItem) {
			this.changePasswordItem = changePasswordItem;
		}

		public void setHowToItem(MenuItem howToItem) {
			this.howToItem = howToItem;
		}

		public void setSettings(Menu settings) {
			this.settings = settings;
		}

		public void setHelp(Menu help) {
			this.help = help;
		}

		public void setMenuBar(MenuBar menuBar) {
			this.menuBar = menuBar;
		}
		
		public Label getLoggedOnUser() {
			return this.loggedOnUser;
		}

}
