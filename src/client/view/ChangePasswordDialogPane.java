package client.view;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ChangePasswordDialogPane extends DialogPane {
	
	// Elements
	
	private Label title;
	
	private Label newPasswordLabel;
	private Label repeatPasswordLabel;
	
	private PasswordField newPasswordField;
	private PasswordField repeatPasswordField;
	private TextField newPasswordTextField;
	private TextField repeatPasswordTextField;
	private CheckBox showPassword;
	
	private Label label;
	
	// Layout§
	
	private BorderPane borderPane;
	private final VBox header;
	private HBox newPasswordHBox;
	private HBox repeatPasswordHBox;
	private VBox general;
	private VBox space;
	private VBox labelVBox;
	private HBox showPasswordHBox;
	
	// Buttontypes for DialogPane
	private ButtonType okButtonType;
	private ButtonType cancelButtonType;
	
	// Spacing for Layout
	private final int SPACING_NEWPASSWORD = 60;
	private final int SPACING_REPEATPASSWORD = 19;
	private final int SPACING = 15;
	
	// Size for TextFields
	private final int SIZE_TEXTFIELDS = 250;
	
	public ChangePasswordDialogPane() {
		
	// Fields for the registration formula and design
		
	this.title = new Label("Passwort ändern");
	this.title.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
	this.title.setTextFill(Color.web("#181C54"));
	
	this.newPasswordLabel = new Label("Neues Passwort");
	this.newPasswordLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
	this.newPasswordLabel.setTextFill(Color.web("#181C54"));
	
	this.newPasswordField = new PasswordField();
	this.newPasswordField.setPromptText("Neues Passwort");
	this.newPasswordField.setFont(Font.font("Verdana", FontWeight.MEDIUM, 12));
	this.newPasswordField.setPrefWidth(SIZE_TEXTFIELDS);
	
	this.repeatPasswordLabel = new Label("Passwort wiederholen");
	this.repeatPasswordLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
	this.repeatPasswordLabel.setTextFill(Color.web("#181C54"));
	
	this.repeatPasswordField = new PasswordField();
	this.repeatPasswordField.setPromptText("Neues Passwort bestätigen");
	this.repeatPasswordField.setFont(Font.font("Verdana", FontWeight.MEDIUM, 12));
	this.repeatPasswordField.setPrefWidth(SIZE_TEXTFIELDS);
	
	this.label = new Label("");
	this.label.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
	this.label.setTextFill(Color.web("#181C54"));
	
	this.newPasswordTextField = new TextField();
	this.newPasswordTextField.setPromptText("Passwort wiederholen");
	this.newPasswordTextField.setFont(Font.font("Verdana", FontWeight.MEDIUM, 12));
	this.newPasswordTextField.setPrefWidth(SIZE_TEXTFIELDS);
	
	this.repeatPasswordTextField = new TextField();
	this.repeatPasswordTextField.setPromptText("Neues Passwort bestätigen");
	this.repeatPasswordTextField.setFont(Font.font("Verdana", FontWeight.MEDIUM, 12));
	this.repeatPasswordTextField.setPrefWidth(SIZE_TEXTFIELDS);
	
	
	// Layout
	this.borderPane = new BorderPane();
	this.header = new VBox(SPACING);
	this.space = new VBox(SPACING);
	this.newPasswordHBox = new HBox(SPACING_NEWPASSWORD);
	this.repeatPasswordHBox = new HBox(SPACING_REPEATPASSWORD);
	this.labelVBox = new VBox();
	this.showPasswordHBox = new HBox();
	this.showPassword = new CheckBox("Passwörter anzeigen");
	this.showPassword.setFont(Font.font("Verdana", FontWeight.MEDIUM, 12));
	this.general = new VBox(SPACING);
	
	// Add Fields to Layout
	this.header.getChildren().add(title);
	this.newPasswordHBox.getChildren().addAll(newPasswordLabel, newPasswordField);
	this.repeatPasswordHBox.getChildren().addAll(repeatPasswordLabel, repeatPasswordField);
	this.labelVBox.getChildren().add(label);
	this.showPasswordHBox.getChildren().addAll(showPassword);
	this.general.getChildren().addAll(space, newPasswordHBox, repeatPasswordHBox, showPasswordHBox, label);
	
	this.borderPane.setTop(header);
	this.borderPane.setCenter(general);
	
	
	 // Add buttonTypes
    this.okButtonType = new ButtonType("Speichern", ButtonBar.ButtonData.OK_DONE);
    this.getButtonTypes().add(okButtonType);
    
    this.cancelButtonType = new ButtonType("Abbrechen", ButtonBar.ButtonData.CANCEL_CLOSE);
    this.getButtonTypes().add(cancelButtonType);
	
    // set content and text for content
	this.setContentText("Passwort ändern");
	
	this.setContent(borderPane);
	
	this.getStylesheets().add(getClass().getResource("FocusAndHowToDialogPaneStyleSheet.css").toExternalForm());
		
	}

	public Label getTitle() {
		return title;
	}

	public Label getNewPasswordLabel() {
		return newPasswordLabel;
	}

	public Label getRepeatPasswordLabel() {
		return repeatPasswordLabel;
	}

	public PasswordField getNewPasswordField() {
		return newPasswordField;
	}

	public PasswordField getRepeatPasswordField() {
		return repeatPasswordField;
	}

	public TextField getNewPasswordTextField() {
		return newPasswordTextField;
	}

	public TextField getRepeatPasswordTextField() {
		return repeatPasswordTextField;
	}

	public CheckBox getShowPassword() {
		return showPassword;
	}

	public Label getLabel() {
		return label;
	}

	public BorderPane getBorderPane() {
		return borderPane;
	}

	public HBox getNewPasswordHBox() {
		return newPasswordHBox;
	}

	public HBox getRepeatPasswordHBox() {
		return repeatPasswordHBox;
	}

	public VBox getGeneral() {
		return general;
	}

	public VBox getSpace() {
		return space;
	}

	public VBox getLabelVBox() {
		return labelVBox;
	}

	public HBox getShowPasswordHBox() {
		return showPasswordHBox;
	}

	public ButtonType getOkButtonType() {
		return okButtonType;
	}

	public ButtonType getCancelButtonType() {
		return cancelButtonType;
	}

	public int getSPACING_NEWPASSWORD() {
		return SPACING_NEWPASSWORD;
	}

	public int getSPACING_REPEATPASSWORD() {
		return SPACING_REPEATPASSWORD;
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

	public void setNewPasswordLabel(Label newPasswordLabel) {
		this.newPasswordLabel = newPasswordLabel;
	}

	public void setRepeatPasswordLabel(Label repeatPasswordLabel) {
		this.repeatPasswordLabel = repeatPasswordLabel;
	}

	public void setNewPasswordField(PasswordField newPasswordField) {
		this.newPasswordField = newPasswordField;
	}

	public void setRepeatPasswordField(PasswordField repeatPasswordField) {
		this.repeatPasswordField = repeatPasswordField;
	}

	public void setNewPasswordTextField(TextField newPasswordTextField) {
		this.newPasswordTextField = newPasswordTextField;
	}

	public void setRepeatPasswordTextField(TextField repeatPasswordTextField) {
		this.repeatPasswordTextField = repeatPasswordTextField;
	}

	public void setShowPassword(CheckBox showPassword) {
		this.showPassword = showPassword;
	}

	public void setLabel(Label label) {
		this.label = label;
	}

	public void setBorderPane(BorderPane borderPane) {
		this.borderPane = borderPane;
	}

	public void setNewPasswordHBox(HBox newPasswordHBox) {
		this.newPasswordHBox = newPasswordHBox;
	}

	public void setRepeatPasswordHBox(HBox repeatPasswordHBox) {
		this.repeatPasswordHBox = repeatPasswordHBox;
	}

	public void setGeneral(VBox general) {
		this.general = general;
	}

	public void setSpace(VBox space) {
		this.space = space;
	}

	public void setLabelVBox(VBox labelVBox) {
		this.labelVBox = labelVBox;
	}

	public void setShowPasswordHBox(HBox showPasswordHBox) {
		this.showPasswordHBox = showPasswordHBox;
	}

	public void setOkButtonType(ButtonType okButtonType) {
		this.okButtonType = okButtonType;
	}

	public void setCancelButtonType(ButtonType cancelButtonType) {
		this.cancelButtonType = cancelButtonType;
	}

	
	
	
}
