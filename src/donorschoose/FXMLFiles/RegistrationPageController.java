/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package donorschoose.FXMLFiles;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author A. Khan
 */
public class RegistrationPageController implements Initializable {

    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;    

    @FXML
    private void loadLoginPage(ActionEvent event) throws Exception
    {
        Parent loginPage = FXMLLoader.load(getClass().getResource("loginPage.fxml"));
        Scene scene = new Scene(loginPage);
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(scene);
    }

    @FXML
    private void register(ActionEvent event)
    {
        
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
}
