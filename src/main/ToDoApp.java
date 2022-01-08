package main;

import client.controller.ToDoController;
import client.model.ToDo;
import client.model.ToDoList;
import client.view.LoginView;
import client.view.ToDoView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ToDoApp extends Application {

	// Fields
	private ToDo todoModel;
	private ToDoList toDoList;
	private ToDoView toDoView;
	private ToDoController toDoController;
	private LoginView loginView;

	// Starts the JavaFX application
	public static void main(String[] args) {
		launch(args);
	}
	
	// Shows a GUI for the App
	
	public void start(Stage stage) {
		
		// Instance for LoginView
		this.loginView = new LoginView();
		
		// Instantiates the root todoView
		this.todoModel = new ToDo();
		this.toDoList = new ToDoList();
		this.toDoView = new ToDoView(todoModel, toDoList, loginView);
		
		stage.resizableProperty().setValue(Boolean.FALSE);
		
		// Passes the root to the scene
		Scene scene2 = new Scene(toDoView);
				
		// Scene for LoginView
		Scene scene1 = new Scene(loginView);
		
		this.toDoController = new ToDoController(
				this.toDoView, 
				this.todoModel, 
				toDoList, 
				stage, 
				scene2, 
				loginView,
				scene1);

		// Shows scene in a window (object stage)
		stage.setScene(scene1);
		stage.setTitle("ToDo-App");		
		stage.show();
		
		// Adds an icon to the window
		Image doneImage = new Image("/common/resources/doneIcon.png");
		stage.getIcons().add(doneImage);
		
	}

	@Override
	public void stop() {

		// Kill all items that are marked as garbage
		ArrayList<ToDo> garbageList = this.toDoController.getToDoList().getGarbageList();
		System.gc();
		System.exit(130);

	}
	
	
	
	

}
