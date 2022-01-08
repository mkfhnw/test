package client.view;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class RegistrationDialogPane extends DialogPane {
	
	// Elements for registration
	private Label title;
	private Label emailLabel;
	private Label label;
	private TextField emailField;
	private Label passwordLabel;
	private PasswordField passwordField;
	private TextField passwordTextField;
	private Label repeatPasswordLabel;
	private PasswordField repeatPasswordField;
	private TextField repeatTextField;
	
	private CheckBox showPassword;
	
	// Layout
	private BorderPane borderPane;
	private HBox emailPane;
	private HBox passwordPane;
	private HBox repeatPasswordPane;
	private VBox header;
	private VBox vBox;
	private HBox showPasswordHBox; 
	private VBox space;
	private VBox labelVBox;
	
	// Buttontypes for DialogPane
	private ButtonType okButtonType;
	private ButtonType cancelButtonType;
	
	// Spacing for Layout
	private final int SPACING_EMAIL = 60;
	private final int SPACING_PASSWORD = 100;
	private final int SPACING_REPEAT_PASSWORD = 12;
	private final int SPACING_HEADER = 150;
	private final int SPACING = 15;

	// Size for TextFields
	private final int SIZE_TEXTFIELDS = 250;
	
	// Constructor
	public RegistrationDialogPane() {
	
		
	// Fields for the registration formula and design
		
	this.title = new Label("Account erstellen");
	this.title.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
	this.title.setTextFill(Color.web("#181C54"));
	
	this.emailLabel = new Label("E-Mail-Adresse");
	this.emailLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
	this.emailLabel.setTextFill(Color.web("#181C54"));
	
	this.label = new Label("");
	
	this.emailField = new TextField();
	this.emailField = new TextField();
	this.emailField.setPromptText("email@outlook.com");
	this.emailField.setPrefWidth(SIZE_TEXTFIELDS);
	this.emailField.setFont(Font.font("Verdana", FontWeight.MEDIUM, 12));
	
	this.passwordLabel = new Label("Passwort");
	this.passwordLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
	this.passwordLabel.setTextFill(Color.web("#181C54"));
	
	this.passwordField = new PasswordField();
	this.passwordField.setPromptText("Passwort");
	this.passwordField.setPrefWidth(SIZE_TEXTFIELDS);
	this.passwordField.setFont(Font.font("Verdana", FontWeight.MEDIUM, 12));
	
	this.repeatPasswordLabel = new Label("Passwort wiederholen");
	this.repeatPasswordLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
	this.repeatPasswordLabel.setTextFill(Color.web("#181C54"));
	
	this.repeatPasswordField = new PasswordField();
	this.repeatPasswordField.setPromptText("Passwort wiederholen");
	this.repeatPasswordField.setPrefWidth(SIZE_TEXTFIELDS);
	this.repeatPasswordField.setFont(Font.font("Verdana", FontWeight.MEDIUM, 12));
	
	this.passwordTextField = new TextField();
	this.passwordTextField.setPromptText("Passwort");
	this.passwordTextField.setPrefWidth(SIZE_TEXTFIELDS);
	this.passwordTextField.setFont(Font.font("Verdana", FontWeight.MEDIUM, 12));
	
	this.repeatTextField = new TextField();
	this.repeatTextField.setPromptText("Passwort wiederholen");
	this.repeatTextField.setPrefWidth(SIZE_TEXTFIELDS);
	this.repeatTextField.setFont(Font.font("Verdana", FontWeight.MEDIUM, 12));
	
	this.showPassword = new CheckBox("Passwörter zeigen");
	this.showPassword.setFont(Font.font("Verdana", FontWeight.MEDIUM, 12));
	
	// Layout
	this.borderPane = new BorderPane();
	this.emailPane = new HBox(SPACING_EMAIL);
	this.passwordPane = new HBox(SPACING_PASSWORD);
	this.repeatPasswordPane = new HBox(SPACING_REPEAT_PASSWORD);
	this.header = new VBox(SPACING_HEADER);
	this.vBox = new VBox(SPACING);
	this.space = new VBox(SPACING);
	this.showPasswordHBox = new HBox();
	this.labelVBox = new VBox();
	
	// Add Fields to Layout
	this.header.getChildren().addAll(title);
	this.emailPane.getChildren().addAll(emailLabel, emailField);
	this.passwordPane.getChildren().addAll(passwordLabel, passwordField);
	this.repeatPasswordPane.getChildren().addAll(repeatPasswordLabel, repeatPasswordField);
	this.showPasswordHBox.getChildren().add(showPassword);
	this.labelVBox.getChildren().add(label);
	this.vBox.getChildren().addAll(
			header,
			space,
			emailPane, 
			passwordPane,
			repeatPasswordPane,
			showPasswordHBox,
			labelVBox);
	
	this.borderPane.setTop(header);
	this.borderPane.setCenter(vBox);
	
	
	 // Add buttonTypes
    this.okButtonType = new ButtonType("Erstellen", ButtonBar.ButtonData.OK_DONE);
    this.getButtonTypes().add(okButtonType);
    
    this.cancelButtonType = new ButtonType("Abbrechen", ButtonBar.ButtonData.CANCEL_CLOSE);
    this.getButtonTypes().add(cancelButtonType);
	
    // set content and text for content
	this.setContentText("Account erstellen");
	
	this.setContent(borderPane);
	
	this.getStylesheets().add(getClass().getResource("FocusAndHowToDialogPaneStyleSheet.css").toExternalForm());
	
	
	}

	public Label getTitle() {
		return title;
	}

	public Label getEmailLabel() {
		return emailLabel;
	}

	public Label getLabel() {
		return label;
	}

	public TextField getEmailField() {
		return emailField;
	}

	public Label getPasswordLabel() {
		return passwordLabel;
	}

	public PasswordField getPasswordField() {
		return passwordField;
	}

	public Label getRepeatPasswordLabel() {
		return repeatPasswordLabel;
	}

	public PasswordField getRepeatPasswordField() {
		return repeatPasswordField;
	}

	public BorderPane getBorderPane() {
		return borderPane;
	}

	public HBox getEmailPane() {
		return emailPane;
	}

	public HBox getPasswordPane() {
		return passwordPane;
	}

	public HBox getRepeatPasswordPane() {
		return repeatPasswordPane;
	}

	public VBox getvBox() {
		return vBox;
	}

	public VBox getSpace() {
		return space;
	}

	public VBox getLabelVBox() {
		return labelVBox;
	}

	public ButtonType getOkButtonType() {
		return okButtonType;
	}

	public ButtonType getCancelButtonType() {
		return cancelButtonType;
	}

	public int getSPACING_EMAIL() {
		return SPACING_EMAIL;
	}

	public int getSPACING_PASSWORD() {
		return SPACING_PASSWORD;
	}

	public int getSPACING_REPEAT_PASSWORD() {
		return SPACING_REPEAT_PASSWORD;
	}

	public int getSPACING_HEADER() {
		return SPACING_HEADER;
	}

	public int getSPACING() {
		return SPACING;
	}

	public int getSIZE_TEXTFIELDS() {
		return SIZE_TEXTFIELDS;
	}

	public void setTitle(Label title) {
		this.title = title;
	}

	public void setEmailLabel(Label emailLabel) {
		this.emailLabel = emailLabel;
	}

	public void setLabel(Label label) {
		this.label = label;
	}

	public void setEmailField(TextField emailField) {
		this.emailField = emailField;
	}

	public void setPasswordLabel(Label passwordLabel) {
		this.passwordLabel = passwordLabel;
	}

	public void setPasswordField(PasswordField passwordField) {
		this.passwordField = passwordField;
	}

	public void setRepeatPasswordLabel(Label repeatPasswordLabel) {
		this.repeatPasswordLabel = repeatPasswordLabel;
	}

	public void setRepeatPasswordField(PasswordField repeatPasswordField) {
		this.repeatPasswordField = repeatPasswordField;
	}

	public void setBorderPane(BorderPane borderPane) {
		this.borderPane = borderPane;
	}

	public void setEmailPane(HBox emailPane) {
		this.emailPane = emailPane;
	}

	public void setPasswordPane(HBox passwordPane) {
		this.passwordPane = passwordPane;
	}

	public void setRepeatPasswordPane(HBox repeatPasswordPane) {
		this.repeatPasswordPane = repeatPasswordPane;
	}

	public void setHeader(VBox header) {
		this.header = header;
	}

	public void setvBox(VBox vBox) {
		this.vBox = vBox;
	}

	public void setSpace(VBox space) {
		this.space = space;
	}

	public void setLabelVBox(VBox labelVBox) {
		this.labelVBox = labelVBox;
	}

	public void setOkButtonType(ButtonType okButtonType) {
		this.okButtonType = okButtonType;
	}

	public void setCancelButtonType(ButtonType cancelButtonType) {
		this.cancelButtonType = cancelButtonType;
	}

	public TextField getPasswordTextField() {
		return passwordTextField;
	}

	public TextField getRepeatTextField() {
		return repeatTextField;
	}

	public void setPasswordTextField(TextField passwordTextField) {
		this.passwordTextField = passwordTextField;
	}

	public void setRepeatTextField(TextField repeatTextField) {
		this.repeatTextField = repeatTextField;
	}

	public CheckBox getShowPassword() {
		return showPassword;
	}

	public HBox getShowPasswordHBox() {
		return showPasswordHBox;
	}

	public void setShowPassword(CheckBox showPassword) {
		this.showPassword = showPassword;
	}

	public void setShowPasswordHBox(HBox showPasswordHBox) {
		this.showPasswordHBox = showPasswordHBox;
	}




}
