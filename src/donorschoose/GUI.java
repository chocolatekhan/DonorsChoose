package donorschoose;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX GUI class.
 * @author Alvi Khan (180041229)
 */
public class GUI extends Application
{
    
    /**
     * Launches the application.
     * @param primaryStage  optional parameter to launch application using a given window
     * @throws Exception    in case there is some problem creating the window
     */
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        
        Parent root = FXMLLoader.load(getClass().getResource("FXMLFiles/loginPage.fxml"));      // load FXML file
        Scene scene = new Scene(root);                                                          // create a scene 
        primaryStage.setTitle("Donors Choose");                                                 // title of the window being created
                                                                                                // TODO maybe we should hide the window decorations
                                                                                                // and make the thing full screen? Look into if this can be done properly.
        
        primaryStage.setScene(scene);                                                           // set the scene to the window being created
        primaryStage.show();                                                                    // show the window
    }
    
    /**
     * Calls the launch function from other classes.
     */
    public static void launchApp()
    {
        launch();
    }
    
}
