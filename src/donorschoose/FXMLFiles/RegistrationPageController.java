/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package donorschoose.FXMLFiles;

import donorschoose.SQLite;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
    private TextField username;
    @FXML
    private PasswordField password;

    @FXML
    private void loadLoginPage(ActionEvent event) throws Exception
    {
        Parent loginPage = FXMLLoader.load(getClass().getResource("loginPage.fxml"));
        Scene scene = new Scene(loginPage);
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(scene);
    }

    @FXML
    private void register(ActionEvent event) throws Exception
    {
        SQLite db = new SQLite();
        if (db.addNewUser(username.getText(), password.getText()) == 1)
        {
            Parent homePage = FXMLLoader.load(getClass().getResource("homePage.fxml"));
            Scene scene = new Scene(homePage);
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
        }
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
}
