package client.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;

public class HowToDialogPane extends DialogPane {
	
	private Button playButton;
	private Button stopButton;
	private Button replayButton;
	
	private ImageView playIcon;
	private ImageView stopIcon;
	private ImageView replayIcon;

	private HBox hBoxButtons; 
	private VBox vBoxPlay;
	private VBox vBoxStop;
	private VBox vBoxReplay;
	private VBox sliderVBox;
	private final VBox spacing;
	private VBox general;
	
	private Media media;
	private MediaPlayer mediaPlayer;
	private MediaView mediaView;
	
	private Slider slider;
	
	private BorderPane howToBorderPane;
	
	private ButtonType closeButtonType;

	public HowToDialogPane() {
	
	// Create Media
	String path = new File("src/common/resources/howTo.mp4").getAbsolutePath();
	this.media = new Media(new File(path).toURI().toString());
	this.mediaPlayer = new MediaPlayer(media);
	this.mediaView = new MediaView();
	this.mediaView.setMediaPlayer(mediaPlayer);
	
	this.mediaView.setFitHeight(2200);
	this.mediaView.setFitWidth(1450);

	// BorderPane
	this.howToBorderPane = new BorderPane();
	this.howToBorderPane.setCenter(mediaView);
	this.howToBorderPane.setMinWidth(1400);
	this.howToBorderPane.setMinHeight(600);

	
	// Icon for PlayButton
	this.playIcon = new ImageView("/common/resources/startIcon.png");
	this.playIcon.setFitHeight(40);
	this.playIcon.setFitWidth(40);
	
	// Icon for StopButton
	this.stopIcon = new ImageView("/common/resources/stopIcon.png");
	this.stopIcon.setFitHeight(40);
	this.stopIcon.setFitWidth(40);
	
	// Icon for ReplayButton
	this.replayIcon = new ImageView("/common/resources/restartIcon.png");
	this.replayIcon.setFitHeight(40);
	this.replayIcon.setFitWidth(40);

	// PlayButton
	this.playButton = new Button();
	this.playButton.setGraphic(playIcon);
	this.playButton.setAlignment(Pos.CENTER);
	this.playButton.setPrefSize(40, 40);
	
	// StopButton
	this.stopButton = new Button();
	this.stopButton.setGraphic(stopIcon);
	this.stopButton.setAlignment(Pos.CENTER);
	this.stopButton.setPrefSize(40, 40);
	
	// ReplayButton
	this.replayButton = new Button();
	this.replayButton.setGraphic(replayIcon);
	this.replayButton.setAlignment(Pos.CENTER);
	this.replayButton.setPrefSize(40, 40);
	
	// Slider for video
	this.slider = new Slider();
	
	// Layout
	this.vBoxPlay = new VBox();
	this.vBoxPlay.getChildren().add(playButton);
	this.vBoxPlay.setSpacing(10);
	
	this.vBoxStop = new VBox();
	this.vBoxStop.getChildren().add(stopButton);
	this.vBoxStop.setSpacing(10);
	
	this.vBoxReplay = new VBox();
	this.vBoxReplay.getChildren().add(replayButton);
	this.vBoxReplay.setSpacing(10);
	
	this.sliderVBox = new VBox();
	this.sliderVBox.getChildren().add(slider);
	
	this.hBoxButtons = new HBox();
	this.hBoxButtons.getChildren().addAll(vBoxPlay, vBoxStop, vBoxReplay);
	this.hBoxButtons.setSpacing(10);
	this.hBoxButtons.setAlignment(Pos.CENTER);
	this.hBoxButtons.setPadding(new Insets(50.0, 0.0, 0.0, 30.0));
	
	this.spacing = new VBox(30);
	
	this.general = new VBox();
	this.general.getChildren().addAll(spacing, hBoxButtons, sliderVBox);
	this.general.setSpacing(10);
	
	this.howToBorderPane.setBottom(general);
	
	// Add ButtonType
	this.closeButtonType = new ButtonType("Beenden", ButtonBar.ButtonData.CANCEL_CLOSE);
	this.getButtonTypes().add(closeButtonType);
	
	
	this.setContent(howToBorderPane);
	this.setPrefSize(1700, 900);
	
	//Add css-styling
	this.getStylesheets().add(getClass().getResource("FocusAndHowToDialogPaneStyleSheet.css").toExternalForm());
	this.howToBorderPane.getStyleClass().add("root");
	this.playButton.getStyleClass().add("button");
	this.stopButton.getStyleClass().add("button");
	this.slider.getStyleClass().add("slider");
	}
	

	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	public ButtonType getCloseButtonType() {
		return closeButtonType;
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
	
	public MediaView getMediaView() {
		return mediaView;
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


	public VBox getSliderVBox() {
		return sliderVBox;
	}


	public VBox getGeneral() {
		return general;
	}


	public Media getMedia() {
		return media;
	}


	public Slider getSlider() {
		return slider;
	}


	public BorderPane getHowToBorderPane() {
		return howToBorderPane;
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


	public void setSliderVBox(VBox sliderVBox) {
		this.sliderVBox = sliderVBox;
	}


	public void setGeneral(VBox general) {
		this.general = general;
	}


	public void setMedia(Media media) {
		this.media = media;
	}


	public void setMediaPlayer(MediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;
	}


	public void setMediaView(MediaView mediaView) {
		this.mediaView = mediaView;
	}


	public void setSlider(Slider slider) {
		this.slider = slider;
	}


	public void setHowToBorderPane(BorderPane howToBorderPane) {
		this.howToBorderPane = howToBorderPane;
	}


	public void setCloseButtonType(ButtonType closeButtonType) {
		this.closeButtonType = closeButtonType;
	}
	
	}
	