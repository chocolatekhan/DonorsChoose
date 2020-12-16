/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package donorschoose.FXMLFiles;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author User
 */
public class HomePageController implements Initializable {

    @FXML
    private Label donorschooseLabel;
    @FXML
    private TextArea searchBar;
    @FXML
    private Label searchLabel;
    @FXML
    private Button homepageButton;
    @FXML
    private Button profileButton;
    @FXML
    private Button logoutButton;
    
    public void logout() throws Exception
    {
        Parent loginPage = FXMLLoader.load(getClass().getResource("loginPage.fxml"));
        Scene scene = new Scene(loginPage);
        Stage stage = (Stage) logoutButton.getScene().getWindow();
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
