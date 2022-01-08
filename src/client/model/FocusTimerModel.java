package client.model;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.io.Serializable;
import java.text.DecimalFormat;

public class FocusTimerModel implements Serializable {
	
		// Elements
	    private final Timeline timeline;
	    private final Label counterLabel;
	    private int second, minute;
	    private final DecimalFormat fmt;
	    private String secondFormat, minuteFormat;
	    
	    // Constructor
	    public FocusTimerModel (Label counterLabel) {
			this.counterLabel = counterLabel;
			this.timeline = new Timeline();	
			this.fmt = new DecimalFormat("00");
			this.minute = 25;
			this.second = 0;
	    }

	    public void stop() {
	    	timeline.getKeyFrames().clear();
	    	timeline.stop();
	    }
	    

	    /*
	     * --LOGIC--
	     * Starts timer and changes minutes/seconds
	     */
	    public void start() {
	      
	    	timeline.setCycleCount(Timeline.INDEFINITE);
	    	
	    	if(timeline != null) {
	    		timeline.stop();
	    	}
	    	
	    	KeyFrame frame = new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
	    		
	    		@Override
	    		public void handle (ActionEvent event) {
	    			
	    			second--;
	    			secondFormat = fmt.format(second);
	    			minuteFormat = fmt.format(minute);	
					counterLabel.setText(minuteFormat + ":" + secondFormat);
					
					if (second == -1) {
						second = 59;
						minute--;
						secondFormat = fmt.format(second);
						minuteFormat = fmt.format(minute);	
						counterLabel.setText(minuteFormat + ":" + secondFormat);
					}
					if (minute == 0 && second == 0) {
						timeline.stop();				
	    		}
	    		}
	    		});
	    	timeline.getKeyFrames().add(frame);
	    	timeline.playFromStart();
	    	
	    }
	    	
	    public void restart() {
			  
			  minute = 25;
			  second = 0;
			  stop();
			  secondFormat = fmt.format(second);
			  minuteFormat = fmt.format(minute);
			  counterLabel.setText(minuteFormat + ":" + secondFormat);
			  start();
			  
		  }
	

	    // Getter and Setter

		public Label getTimerLbl() {
			return counterLabel;
		}

		public int getSecond() {
			return second;
		}

		public int getMinute() {
			return minute;
		}

		public void setSecond(int seconds) {
			this.second = second;
		}

		public void setMinute(int minute) {
			this.minute = minute;
		}

	}