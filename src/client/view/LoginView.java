package client.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoginView extends BorderPane {
	
	// Elements for Login Scene
	private Label userLabel;
	private Label passwordLabel;
	private TextField userField;
	private PasswordField passwordField;
	private TextField showedPasswordField;
	private Button signInButton;
	private Button registerButton;
	
	private ImageView image;
	private ImageView eyeImage;
	private ImageView hiddenEyeImage;
	
	private Label label;
	
	private VBox userVBox;
	private VBox passwordVBox;
	private VBox buttonVBox;
	private VBox loginVBox;
	private VBox spaceVBox;
	private VBox vBoxSpace2;
	private VBox imageVBox;
	private HBox passwordHBox;
	private VBox passwordFieldVBox;
	private VBox eyeVBox;
	private VBox vBoxSpace;
	private VBox changePasswordVBox;
	private HBox signInRegister;

	private final Button pingButton;
	
	// Spacings
    private final int SPACING_BUTTON_VBOX = 30;
    private final int SPACING_LOGIN_VBOX = 15;
    private final int SPACING_IMAGE_VBOX = 20;
    private final int SPACING = 20;
    private final int SPACING_TOP = 100;
    private final int SPACING_PASSWORD_HBOX = 7;
	
    // Dialog for CreateAccount
    private Dialog<ButtonType> registrationDialog;
	private RegistrationDialogPane registrationDialogPane;
	
	// Constructor
	public LoginView() {
	
	// Content	
	this.image = new ImageView("/common/resources/User.png");
	this.image.setFitHeight(140);
	this.image.setFitWidth(140);
	
	/*
	 * The eyeImage is here to change the visability of
	 * the passwordField. If the user clicks on the image, 
	 * the image will change and password will be visable oder not --> Handling in Controller
	 */
	this.eyeImage = new ImageView("/common/resources/eye.png");
	this.eyeImage.setPickOnBounds(true);
	this.eyeImage.setFitHeight(20);
	this.eyeImage.setFitWidth(20);
	
	this.hiddenEyeImage = new ImageView("/common/resources/hiddeneye.png");
	this.hiddenEyeImage.setPickOnBounds(true);
	this.hiddenEyeImage.setFitHeight(20);
	this.hiddenEyeImage.setFitWidth(20);
	
	// Label shows if Login failed --> Handling in Controller
	this.label = new Label("");
		
	this.userLabel = new Label("Benutzername");
	this.userLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
	this.userLabel.setTextFill(Color.web("#181C54"));
	
	this.passwordLabel = new Label("Passwort");
	this.passwordLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
	this.passwordLabel.setTextFill(Color.web("#181C54"));
	
	this.userField = new TextField();
	this.userField.setPromptText("email@outlook.com");
	this.userField.setMaxWidth(283);
	this.userField.setFont(Font.font("Verdana", FontWeight.MEDIUM, 12));
	
	this.passwordField = new PasswordField();
	this.passwordField.setPromptText("Passwort");
	this.passwordField.setMaxWidth(300);
	this.passwordField.setFont(Font.font("Verdana", FontWeight.MEDIUM, 12));
	
	this.showedPasswordField = new TextField();
	this.showedPasswordField.setPromptText("Passwort");
	this.showedPasswordField.setMaxWidth(300);
	this.showedPasswordField.setFont(Font.font("Verdana", FontWeight.MEDIUM, 12));
	
	this.signInButton = new Button("Anmelden");
	this.signInButton.setPrefSize(325, 60);
	this.signInButton.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
	this.signInButton.setTextFill(Color.web("#181C54"));
	this.signInButton.setDefaultButton(true);
	
	this.registerButton = new Button("Registrieren");
	this.registerButton.setPrefSize(325, 60);
	this.registerButton.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
	this.registerButton.setTextFill(Color.web("#181C54"));

	this.pingButton = new Button("Ping");
	
	Label label2 = new Label("");
	
	// Layout

	this.vBoxSpace = new VBox(SPACING_TOP);
	this.vBoxSpace.getChildren().add(label2);
	this.vBoxSpace2 = new VBox(SPACING);
	
	this.imageVBox = new VBox(SPACING_IMAGE_VBOX);
	this.imageVBox.getChildren().addAll(vBoxSpace, image, vBoxSpace2);
	this.imageVBox.setSpacing(20);
	this.imageVBox.setAlignment(Pos.CENTER);
		
	this.userVBox= new VBox();
	this.userVBox.getChildren().addAll(userLabel, userField);
	this.userVBox.setSpacing(10);
	this.userVBox.setAlignment(Pos.CENTER);
	
	this.passwordFieldVBox = new VBox();
	this.passwordFieldVBox.getChildren().add(passwordField);
	this.passwordFieldVBox.setPrefWidth(250);
	this.passwordFieldVBox.setPadding(new Insets(0.0, -34.0, 0.0, -2.0));
	this.passwordFieldVBox.setAlignment(Pos.CENTER);
	
	this.eyeVBox = new VBox();
	this.eyeVBox.getChildren().add(hiddenEyeImage);
	
	this.passwordHBox = new HBox(SPACING_PASSWORD_HBOX);
	this.passwordHBox.getChildren().addAll(passwordFieldVBox, eyeVBox);
	this.passwordHBox.setAlignment(Pos.CENTER);

	// Spacing box for setting ping button to the right
	HBox pingButtonBox = new HBox();
	pingButtonBox.getChildren().add(this.pingButton);
	pingButtonBox.setMaxWidth(290);
	pingButtonBox.setMinHeight(30);
	pingButtonBox.setAlignment(Pos.BOTTOM_RIGHT);

	this.passwordVBox = new VBox();
	this.passwordVBox.getChildren().addAll(passwordLabel, passwordHBox, pingButtonBox);
	this.passwordVBox.setSpacing(5);
	this.passwordVBox.setAlignment(Pos.CENTER);
	
	this.spaceVBox = new VBox();
	
	this.loginVBox = new VBox(SPACING_LOGIN_VBOX);
	this.loginVBox.getChildren().addAll(userVBox, passwordVBox, spaceVBox);
	
	this.changePasswordVBox = new VBox();
	this.changePasswordVBox.getChildren().add(label);
	this.changePasswordVBox.setSpacing(5);
	this.changePasswordVBox.setAlignment(Pos.CENTER);
	
	this.signInRegister = new HBox();
	signInRegister.getChildren().addAll(registerButton, signInButton);
	signInRegister.setAlignment(Pos.CENTER);	
	
	this.buttonVBox = new VBox(SPACING_BUTTON_VBOX);
	this.buttonVBox.getChildren().addAll(changePasswordVBox, signInRegister);
	this.buttonVBox.setAlignment(Pos.CENTER);
	
	this.setTop(imageVBox);
	this.setCenter(loginVBox);
	this.setBottom(buttonVBox);
	
	this.setPrefSize(650, 450);
	
	// Create and costumize Registration Dialog
	this.registrationDialog = new Dialog<ButtonType>();
	this.registrationDialog.setTitle("Account erstellen");
	Stage stage3 = (Stage) registrationDialog.getDialogPane().getScene().getWindow();
	stage3.getIcons().add(new Image(this.getClass().getResource("/common/resources/User.png").toString()));
	
	this.registrationDialogPane = new RegistrationDialogPane();
	this.registrationDialog.setDialogPane(registrationDialogPane);
	
	this.registrationDialog.initModality(Modality.NONE);

	// CSS Styling
	this.getStylesheets().add(getClass().getResource("FocusAndHowToDialogPaneStyleSheet.css").toExternalForm());
	this.signInButton.getStyleClass().add("login");
	this.registerButton.getStyleClass().add("login");
	this.pingButton.getStyleClass().add("openFocusTimer");
	
	}


	public Label getUserLabel() {
		return userLabel;
	}


	public Label getPasswordLabel() {
		return passwordLabel;
	}


	public TextField getUserField() {
		return userField;
	}


	public TextField getPasswordField() {
		return passwordField;
	}


	public Button getSignInButton() {
		return signInButton;
	}


	public Button getRegisterButton() {
		return registerButton;
	}


	public ImageView getImage() {
		return image;
	}


	public VBox getUserVBox() {
		return userVBox;
	}


	public VBox getPasswordVBox() {
		return passwordVBox;
	}


	public VBox getButtonVBox() {
		return buttonVBox;
	}


	public VBox getLoginVBox() {
		return loginVBox;
	}


	public VBox getSpaceVBox() {
		return spaceVBox;
	}


	public VBox getImageVBox() {
		return imageVBox;
	}


	public int getSPACING_BUTTON_VBOX() {
		return SPACING_BUTTON_VBOX;
	}


	public int getSPACING_LOGIN_VBOX() {
		return SPACING_LOGIN_VBOX;
	}


	public int getSPACING_IMAGE_VBOX() {
		return SPACING_IMAGE_VBOX;
	}


	public int getSPACING() {
		return SPACING;
	}


	public void setUserLabel(Label userLabel) {
		this.userLabel = userLabel;
	}


	public void setPasswordLabel(Label passwordLabel) {
		this.passwordLabel = passwordLabel;
	}


	public void setUserField(TextField userField) {
		this.userField = userField;
	}


	public void setPasswordField(PasswordField passwordField) {
		this.passwordField = passwordField;
	}


	public void setSignInButton(Button signInButton) {
		this.signInButton = signInButton;
	}


	public void setRegisterButton(Button registerButton) {
		this.registerButton = registerButton;
	}


	public void setImage(ImageView image) {
		this.image = image;
	}


	public void setUserVBox(VBox userVBox) {
		this.userVBox = userVBox;
	}


	public void setPasswordVBox(VBox passwordVBox) {
		this.passwordVBox = passwordVBox;
	}


	public void setButtonVBox(VBox buttonVBox) {
		this.buttonVBox = buttonVBox;
	}


	public void setLoginVBox(VBox loginVBox) {
		this.loginVBox = loginVBox;
	}


	public void setSpaceVBox(VBox spaceVBox) {
		this.spaceVBox = spaceVBox;
	}


	public void setImageVBox(VBox imageVBox) {
		this.imageVBox = imageVBox;
	}


	public Dialog<ButtonType> getRegistrationDialog() {
		return registrationDialog;
	}


	public RegistrationDialogPane getRegistrationDialogPane() {
		return registrationDialogPane;
	}


	public void setRegistrationDialog(Dialog<ButtonType> registrationDialog) {
		this.registrationDialog = registrationDialog;
	}


	public void setRegistrationDialogPane(RegistrationDialogPane registrationDialogPane) {
		this.registrationDialogPane = registrationDialogPane;
	}


	public ImageView getEyeImage() {
		return eyeImage;
	}


	public ImageView getHiddenEyeImage() {
		return hiddenEyeImage;
	}


	public Label getLabel() {
		return label;
	}


	public VBox getvBoxSpace2() {
		return vBoxSpace2;
	}


	public HBox getPasswordHBox() {
		return passwordHBox;
	}


	public VBox getPasswordFieldVBox() {
		return passwordFieldVBox;
	}


	public VBox getEyeVBox() {
		return eyeVBox;
	}


	public VBox getvBoxSpace() {
		return vBoxSpace;
	}


	public int getSPACING_PASSWORD_HBOX() {
		return SPACING_PASSWORD_HBOX;
	}

	public Button getPingButton() { return this.pingButton; }


	public void setEyeImage(ImageView eyeImage) {
		this.eyeImage = eyeImage;
	}


	public void setHiddenEyeImage(ImageView hiddenEyeImage) {
		this.hiddenEyeImage = hiddenEyeImage;
	}


	public void setLabel(Label label) {
		this.label = label;
	}


	public void setvBoxSpace2(VBox vBoxSpace2) {
		this.vBoxSpace2 = vBoxSpace2;
	}


	public void setPasswordHBox(HBox passwordHBox) {
		this.passwordHBox = passwordHBox;
	}


	public void setPasswordFieldVBox(VBox passwordFieldVBox) {
		this.passwordFieldVBox = passwordFieldVBox;
	}


	public void setEyeVBox(VBox eyeVBox) {
		this.eyeVBox = eyeVBox;
	}


	public void setvBoxSpace(VBox vBoxSpace) {
		this.vBoxSpace = vBoxSpace;
	}


	public VBox getChangePasswordVBox() {
		return changePasswordVBox;
	}


	public HBox getSignInRegister() {
		return signInRegister;
	}


	public void setChangePasswordVBox(VBox changePasswordVBox) {
		this.changePasswordVBox = changePasswordVBox;
	}


	public void setSignInRegister(HBox signInRegister) {
		this.signInRegister = signInRegister;
	}


	public TextField getShowedPasswordField() {
		return showedPasswordField;
	}


	public void setShowedPasswordField(TextField showedPasswordField) {
		this.showedPasswordField = showedPasswordField;
	}
	

}