//Student Name:  Aswin Visva
//Date: 2017/06/03
//Teacher Name: Mr. Jackson
//Program Title: Facial recognition - Culminating assignment
//Program File Name: Main.java
//Program Description: Loads the fxml file, sets the scene size, sets the window title, loads controller and loads the openCV library

//1.0 PACKAGE AND IMPORTS
//	1.1 The main package in which this java file is stored, "application"
package application;
// 	1.2 Imports
import org.opencv.core.Core;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
	@Override //Overrides fxml document
	public void start(Stage primaryStage) {//Method to start, with an input value of "primaryStage"
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Sample.fxml")); //Loads the fxml document
			BorderPane root = (BorderPane) loader.load(); //Loads the border pane from the fxml document
			root.setStyle("-fx-background-color: whitesmoke;"); //Sets background color 
			Scene scene = new Scene(root, 800, 600); //Sets window size
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setTitle("Aswin's Computer Science Culminating"); //Sets window title
			primaryStage.setScene(scene); //Sets scene
			primaryStage.show(); //Allows window to be visible
			SampleController controller = loader.getController(); //Loads controller 
			controller.init();
			primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() { //Sets the controller closed when the window is closed
				public void handle(WindowEvent we) {
					controller.setClosed();
				}
			}));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) { //Main Method 
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME); //Load the openCV library

		launch(args);
	}
}