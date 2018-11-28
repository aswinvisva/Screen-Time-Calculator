//Student Name:  Aswin Visva (Press the "+" button on the left to see all comments
//Date: 2017/06/03
//Teacher Name: Mr. Jackson
//Program Title: Facial recognition - Culminating assignment
//Program File Name: SampleController.java
//Program Description: Controls the fxml file and the "main" java file and implements both to create the GUI and use the openCV "detectMultiScale" method
// 					   in order to achieve facial recognition. It calculates the current session time, the average session time, the time away from the computer,
//					   and the recommended session time. If the user goes over the recommended session time, the program warns him/her to take a break. At this point
//					   a countdown timer initiates from 300 seconds (or 5 minutes) which executes only while the user is not in front of the screen. 



//1.0 PACKAGE AND IMPORTS
//	1.1 The main package in which this java file is stored, "application"
package application;

//	1.2 Every import used for the project
import javafx.scene.control.Label;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;
import it.polito.elite.teaching.cv.utils.Utils;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.*;


//2.0 PUBLIC CLASS, SAMPLE CONTROLLER
public class SampleController {

	public String s, s1, lastSession, averageSession, temp = "0"; //Public variables which can be accessed from anywhere in the program
	String fileName = "temp.txt"; //File name for the file which stores average session
	String line = null; //String which will store the file contents

	public String readFile() { //Method which reads contents of file
		try {

			FileReader fileReader = new FileReader(fileName); //File reader initialized

			BufferedReader bufferedReader = new BufferedReader(fileReader); //Buffered reader initialized

			line = bufferedReader.readLine(); //Reads line from file

			bufferedReader.close(); //Closes reader after file has been read
		} catch (FileNotFoundException ex) { //Catch the error statement "FileNotFoundException"
			System.out.println("Unable to open file '" + fileName + "'"); //Action to be executed if the error statement is received
		} catch (IOException ex) { //Catch the error statement "IOException"
			System.out.println("Error reading file '" + fileName + "'"); //Action to be executed if the error statement is received

		}
		return line; //Returns the file contents as a string
	}

	public void writeFile() { //Method which writes to a file
		try {

			averageSession = Double.toString((Double.parseDouble(readFile()) + Double.parseDouble(lastSession)) / 2); //Calculates average session by taking the average of the last recorded session and the current average
			String bytes = averageSession; //Stores the value of the String "averageSession" into the string "Bytes"

			byte[] buffer = bytes.getBytes(); //Converts the string "Bytes" into bytes

			FileOutputStream outputStream = new FileOutputStream(fileName); //Initializes file output stream

			outputStream.write(buffer); //Writes the bytes to the file

			outputStream.close(); //Closes output stream

		} catch (IOException ex) {//Catch the error statement "IOException"
			System.out.println("Error writing file '" + fileName + "'");//Action to be executed if the error statement is received

		}
	}

	public long start = 0, current = 0, elapsedSeconds = 0, startGone = 0, currentGone = 0, elapsedGone = 0; //Public long variables used in the timer process

	@FXML //Refer to FXML file
	private Button cameraButton; //Initialize a button with the fx:id of "cameraButton"

	@FXML //Refer to FXML file
	private ImageView originalFrame; //Initialize an ImageView with the fx:id of "originalFrame"

	@FXML //Refer to FXML file
	private CheckBox haarClassifier; //Initialize a CheckBox with the fx:id of "haarClassifier"

	@FXML //Refer to FXML file
	public Label label; //Initialize a Label with the fx:id of "label"
	@FXML
	public Label label1; //Initialize a Label with the fx:id of "label1"

	@FXML //Refer to FXML file
	public Label LastSession; //Initialize a Label with the fx:id of "LastSession"

	@FXML //Refer to FXML file
	public Label Alert; //Initialize a Label with the fx:id of "Alert"
	
	private ScheduledExecutorService timer; //A variable type which executes a 'runnable' at a periodic interval called "timer"

	private VideoCapture capture; //Initializes a videocapture called "capture"

	private boolean cameraActive; //Initializes boolean called "cameraActive"

	private CascadeClassifier faceCascade; // Initializes CascadeClassifier called "faceCascade"
	private int absoluteFaceSize; //Initializes called "absoluteFaceSize"

	protected void init() { //Protected method called "init"
		this.capture = new VideoCapture(); //Initializes new video capture
		this.faceCascade = new CascadeClassifier(); //Initializes new Cascade Classifier
		this.absoluteFaceSize = 0; //Sets the variable "absoluteFaceSize" to 0

		originalFrame.setFitWidth(600); //Sets the image view frame width to 600 pixels

		originalFrame.setPreserveRatio(true); //Preserves ratio of the imageview even when you resize the window
	}

	@FXML //Refer to FXML file
	protected void startCamera() { //When the action "startCamera" is received
		if (!this.cameraActive) { //Checks if the camera is not active

			this.haarClassifier.setDisable(true); //Disables haar classifier

			this.capture.open(0); //Opens capture

			if (this.capture.isOpened()) {//Checks if the capture is opened
				this.cameraActive = true; //Sets the boolean "cameraActive" to true

				Runnable frameGrabber = new Runnable() { //Initializes a new runnable

					@Override //overrides 
					public void run() { 

						Mat frame = grabFrame(); //Grabs frame from webcam

						Image imageToShow = Utils.mat2Image(frame); //Converts from a mat file to an image file
						updateImageView(originalFrame, imageToShow); //Updates the image view to show the frame from the webcam

					}
				};

				this.timer = Executors.newSingleThreadScheduledExecutor(); 
				this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS); //Executes the preceeding runnable every 33 milliseconds, therefore, it grabs a frame from the webcam every 33 milliseconds
				this.cameraButton.setText("Stop Camera"); //Sets the text of the button to "Stop Camera"
				Timer timer = new Timer(); //New Timer is initialized
				timer.scheduleAtFixedRate(new TimerTask() { //New timer task is initialized
					@Override //overrids
					public void run() {
						Platform.runLater(new Runnable() { //New runnable initialized 
							@Override
							public void run() {
								try { //Sets the first label to the current session time
									label.textProperty()
											.bind(new SimpleStringProperty("Current session: " + s + " seconds"));//Binds the text property of the label to the current session time
								} catch (java.lang.NullPointerException e) { //Catches a null pointer exception, this may occur when the program just begins and the current session has a "null" value
								}

								try { //Sets the second label to the time away from camera
									label1.textProperty()
											.bind(new SimpleStringProperty(s1 + " seconds away from camera")); //Binds the text property of the label to the time away from camera
								} catch (java.lang.NullPointerException e) { //Catches a null pointer exception, this may occur when the program just begins and the time away from camera has a "null" value
								}
								try { //Sets the third label to the average session time
									LastSession.textProperty().bind( //Binds the text property of the label to the average session time, note how it says readFile(), this allows the average session time to be outputted even when the program is closed, as it reads this value from a file
											new SimpleStringProperty("Average Session: " + readFile() + " seconds"));
 
								} catch (java.lang.NullPointerException e) { //Catches a null pointer exception, this may occur when the program just begins and the average session time has a "null" value
								}
								try {
									if ((elapsedSeconds / 1000) > 1800 && (elapsedGone / 1000) < 300) { //Checks if the current session time is greater than 30 minutes, and the time away from the camera is less than 5 minutes
										Alert.textProperty() //Changes the fourth label to warn the user to stop using the screen, and sets a countdown timer from 5 minutes
												.bind(new SimpleStringProperty(
														"Maximum session time exceeded, continue using in "
																+ (300 - (elapsedGone / 1000)) + " seconds"));
									} else {
										Alert.textProperty().bind( //If the afore stated conditions are not met, set the label to the recommended maximum session time
												new SimpleStringProperty("Recommended maximum session time: 1800 seconds"));
									}
								} catch (java.lang.NullPointerException e) { //Catches a null pointer exception, this may occur when the program just begins and the current session and the time away from camera have "null" values
								}
							}
						});
					}
				}, 0, 1000); //Executes the afore stated try statements every 1000 milliseconds, or 1 second
			} else {
				System.err.println("Failed to open the camera connection..."); //If the preceeding statements could not be executed, tell the user
			}
		} else { //If the camera is not active
			this.cameraActive = false; //Set the boolean "camera active" to false

			this.cameraButton.setText("Start Camera"); //Change the text of the button to "start camera"

			this.haarClassifier.setDisable(false); //Enables the checkbox "haarclassifier"

			this.stopAcquisition(); 
		}
	}

	private Mat grabFrame() { //Method grab frame
		Mat frame = new Mat();

		if (this.capture.isOpened()) { //If the capture is opened 
			try {

				this.capture.read(frame); //Reads frame

				if (!frame.empty()) { //If the frame isn't empty

					this.detectAndDisplay(frame); //Call the method "detectaAndDisplay" with an input value of "frame"
				}

			} catch (Exception e) { //If an Exception occurs...

				System.err.println("Exception during the image elaboration: " + e); //Tell the user there was an exception
			}
		}

		return frame; //Return frame when this method is called 
	}

	private void detectAndDisplay(Mat frame) { //Method "detect and display" with an input value of "frame"

		MatOfRect faces = new MatOfRect(); 
		Mat grayFrame = new Mat();

		Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

		Imgproc.equalizeHist(grayFrame, grayFrame);

		if (this.absoluteFaceSize == 0) { //Sets minimum face size 
			int height = grayFrame.rows();
			if (Math.round(height * 0.2f) > 0) {
				this.absoluteFaceSize = Math.round(height * 0.2f);
			}
		}

		this.faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE, //Implements detectmultiscale method from openCV
				new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());

		Rect[] facesArray = faces.toArray(); //Sets the number of faces into an array

		try {
			if (facesArray[0] != null) { //If the first array element does not have a null value, assume there is a face in front of the screen

				current = System.currentTimeMillis(); //The current time is set to the current milliseconds of the program
				elapsedSeconds = ((current - start)); //Elapsed milliseconds is equal to the current milliseconds minus the milliseconds at the start, when there was no face
				s = Long.toString(elapsedSeconds / 1000); //The string, s, is set to elapsed milliseconds / 1000

			}
		} catch (ArrayIndexOutOfBoundsException exception) { //When there is no face in front of the camera, it will output an arrayoutofbounds exception, assume there is no face
			elapsedGone = System.currentTimeMillis() - current; //Elapsed time away from screen is set to the current milliseconds minus the time when there was a face
			if (elapsedGone < 1000000) 
				s1 = Long.toString(elapsedGone / 1000);

			if ((elapsedGone / 1000) > 10) { //If the elapsed time away from the computer is over 10 seconds
				lastSession = Long.toString(elapsedSeconds / 1000); //Set the last session to the current one the user just had
				start = System.currentTimeMillis(); //Reset the start variable
				if (!(temp.equals(lastSession))) {//Will only run once for each session time, this prevents it from running multiple times and skewing the average session time data
					writeFile(); //Calls method which calculates average session time and writes it to the file
				}
				temp = lastSession; //Sets the temporary storage to last session

			}
		}

	}

	@FXML //Refer to FXML file
	protected void haarSelected(Event event) { //When the checkbox, haarclassifier, is selected

		this.checkboxSelection("resources/haarcascades/haarcascade_frontalface_alt.xml"); //Calls the method "checkboxselection" with an input value containing the address of the xml file used to detect faces
	}

	private void checkboxSelection(String classifierPath) { 

		this.faceCascade.load(classifierPath); //Loads the address

		this.cameraButton.setDisable(false); //Enables the camera button to be pressed
	}

	private void stopAcquisition() {//Method which shuts down the entire program
		if (this.timer != null && !this.timer.isShutdown()) {
			try {

				this.timer.shutdown(); //Shuts down timer
				this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) { //If there is an exception, it is because the camera is in the midst of capturing a frame

				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
		}

		if (this.capture.isOpened()) {

			this.capture.release(); //Releases camera
		}
	}

	private void updateImageView(ImageView view, Image image) { //Method to update image view

		Utils.onFXThread(view.imageProperty(), image); //Receives image from webcam

	}

	protected void setClosed() { //Method "setclosed"
		this.stopAcquisition();// refers to the afore stated method to shut down program
	}
}