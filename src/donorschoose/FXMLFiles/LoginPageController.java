package donorschoose.FXMLFiles;

import donorschoose.SQLite;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class LoginPageController implements Initializable {
    
    @FXML
    private Button registerButton;
    @FXML
    private Button loginButton;   
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Label errorLabel;


    @FXML
    private void login() throws Exception
    {
        SQLite db = new SQLite();
        if (db.searchUser(username.getText(), password.getText()) == 1)
        {
            Parent homePage = FXMLLoader.load(getClass().getResource("homePage.fxml"));
            Scene scene = new Scene(homePage);
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
        }
        else if (db.searchUser(username.getText(), password.getText()) == 0)
        {
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void loadRegistrationPage() throws Exception
    {
        Parent registrationPage = FXMLLoader.load(getClass().getResource("registrationPage.fxml"));
        Scene scene = new Scene(registrationPage);
        Stage stage = (Stage) registerButton.getScene().getWindow();
        stage.setScene(scene);
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    } 
    
}

 
