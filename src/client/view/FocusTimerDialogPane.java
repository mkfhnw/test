package client.view;


import client.model.FocusTimerModel;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class FocusTimerDialogPane extends DialogPane {
	
	private FocusTimerModel model;
	
	private Button playButton;
	private Button stopButton;
	private Button replayButton;
	
	private ImageView playIcon;
	private ImageView stopIcon;
	private ImageView replayIcon;
	
	private Label counterLabel;
	
	private BorderPane timerBorderPane;
	
	private VBox vBoxText;
	private HBox hBoxButtons;
	
	private VBox vBoxPlay;
	private VBox vBoxStop;
	private VBox vBoxReplay;
	
	private Timeline timeline;
	
	private ButtonType closeButtonType;
	
	public FocusTimerDialogPane() {
			
		this.counterLabel = new Label("25:00");
		this.counterLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 70));
		this.counterLabel.setAlignment(Pos.CENTER);
		
		this.model = new FocusTimerModel(counterLabel);
				
		this.playIcon = new ImageView("/common/resources/startIcon.png");
		this.playIcon.setFitHeight(50);
		this.playIcon.setFitWidth(50);
		this.playButton = new Button();
		this.playButton.setGraphic(playIcon);
		this.model.start();
		
		this.stopIcon = new ImageView("/common/resources/stopIcon.png");
		this.stopIcon.setFitHeight(50);
		this.stopIcon.setFitWidth(50);
		this.stopButton = new Button();
		this.stopButton.setGraphic(stopIcon);
		
		this.replayIcon = new ImageView("/common/resources/restartIcon.png");
		this.replayIcon.setFitHeight(50);
		this.replayIcon.setFitWidth(50);
		this.replayButton = new Button();
		this.replayButton.setGraphic(replayIcon);
		
		this.timerBorderPane = new BorderPane();
		
		this.hBoxButtons = new HBox();
		this.hBoxButtons.setAlignment(Pos.CENTER);
		this.hBoxButtons.setSpacing(20);
		this.vBoxText = new VBox();
		
		this.vBoxPlay = new VBox();
		this.vBoxStop = new VBox();
		this.vBoxReplay = new VBox();
	
		this.vBoxPlay.getChildren().add(playButton);
		this.vBoxStop.getChildren().add(stopButton);
		this.vBoxReplay.getChildren().add(replayButton);
		
		this.vBoxText.getChildren().add(counterLabel);
		this.vBoxText.setAlignment(Pos.CENTER);
		this.vBoxText.setSpacing(100);
		
		this.hBoxButtons.getChildren().addAll(vBoxPlay, vBoxStop, vBoxReplay);
		
		this.timerBorderPane.setCenter(vBoxText);
		this.timerBorderPane.setBottom(hBoxButtons);
		
		// Add ButtonType
		this.closeButtonType = new ButtonType("Beenden", ButtonBar.ButtonData.CANCEL_CLOSE);
		this.getButtonTypes().add(closeButtonType);
		
		this.timeline = new Timeline();
		
		this.setContent(timerBorderPane);
		this.setContentText("Fokus Timer");
		
		//Add css-styling
		this.getStylesheets().add(getClass().getResource("FocusAndHowToDialogPaneStyleSheet.css").toExternalForm());
		this.timerBorderPane.getStyleClass().add("root");
		this.playButton.getStyleClass().add("button");
		this.stopButton.getStyleClass().add("button");
		this.replayButton.getStyleClass().add("button");
	}
	
	public Label getCounter() {
		return counterLabel;
	}
	
	public Button getPlayButton() {
		return playButton;
	}
	public Button getStopButton() {
		return stopButton;
	}
	public Button getReplayButton() {
		return replayButton;
	}

	public FocusTimerModel getModel() {
		return model;
	}

	public ImageView getPlayIcon() {
		return playIcon;
	}

	public ImageView getStopIcon() {
		return stopIcon;
	}

	public ImageView getReplayIcon() {
		return replayIcon;
	}

	public Label getCounterLabel() {
		return counterLabel;
	}

	public BorderPane getTimerBorderPane() {
		return timerBorderPane;
	}

	public VBox getvBoxText() {
		return vBoxText;
	}

	public HBox gethBoxButtons() {
		return hBoxButtons;
	}

	public VBox getvBoxPlay() {
		return vBoxPlay;
	}

	public VBox getvBoxStop() {
		return vBoxStop;
	}

	public VBox getvBoxReplay() {
		return vBoxReplay;
	}

	public Timeline getTimeline() {
		return timeline;
	}

	public ButtonType getCloseButtonType() {
		return closeButtonType;
	}

	public void setModel(FocusTimerModel model) {
		this.model = model;
	}

	public void setPlayButton(Button playButton) {
		this.playButton = playButton;
	}

	public void setStopButton(Button stopButton) {
		this.stopButton = stopButton;
	}

	public void setReplayButton(Button replayButton) {
		this.replayButton = replayButton;
	}

	public void setPlayIcon(ImageView playIcon) {
		this.playIcon = playIcon;
	}

	public void setStopIcon(ImageView stopIcon) {
		this.stopIcon = stopIcon;
	}

	public void setReplayIcon(ImageView replayIcon) {
		this.replayIcon = replayIcon;
	}

	public void setCounterLabel(Label counterLabel) {
		this.counterLabel = counterLabel;
	}

	public void setTimerBorderPane(BorderPane timerBorderPane) {
		this.timerBorderPane = timerBorderPane;
	}

	public void setvBoxText(VBox vBoxText) {
		this.vBoxText = vBoxText;
	}

	public void sethBoxButtons(HBox hBoxButtons) {
		this.hBoxButtons = hBoxButtons;
	}

	public void setvBoxPlay(VBox vBoxPlay) {
		this.vBoxPlay = vBoxPlay;
	}

	public void setvBoxStop(VBox vBoxStop) {
		this.vBoxStop = vBoxStop;
	}

	public void setvBoxReplay(VBox vBoxReplay) {
		this.vBoxReplay = vBoxReplay;
	}

	public void setTimeline(Timeline timeline) {
		this.timeline = timeline;
	}

	public void setCloseButtonType(ButtonType closeButtonType) {
		this.closeButtonType = closeButtonType;
	}
	
	
	
	
	
}