/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import java.sql.ResultSet;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

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
    private Button homepageButton;
    @FXML
    private Button profileButton;
    @FXML
    private Button logoutButton;
    @FXML
    private ToolBar toolbar;
    @FXML
    private ToolBar verticalToolbar;
    @FXML
    private GridPane grid;
    
    private void createResult(String name, String description, int columnIndex, int rowIndex)
    {
        Label charityName = new Label(name);
        Label charityDescription = new Label(description);
        charityName.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 15px; -fx-font-family: 'Verdana';");
        charityDescription.setWrapText(true);
        charityDescription.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 10px; -fx-font-family: 'Verdana';");
        
        VBox vbox = new VBox(charityName, charityDescription);
        vbox.setMaxHeight(75);
        vbox.setAlignment(Pos.BOTTOM_LEFT);
        vbox.setStyle("-fx-background-color:#90D7E9;");
        vbox.setPadding(new Insets(5));
        
        GridPane.setValignment(vbox, VPos.BOTTOM);
        grid.add(vbox, columnIndex, rowIndex);
    }
    
    @FXML
    public void search()
    {
        SQLite db = new SQLite();
        ResultSet results = db.searchCharity(searchBar.getText());
        
        grid.getChildren().clear();
        
        int rowIndex = 0;
        int columnIndex = 0;
        
        try
        {
            while (results.next())
            {
                createResult(results.getString(1), results.getString(2), columnIndex, rowIndex);
                columnIndex++;
                if (columnIndex == 2)
                {
                    columnIndex = 0;
                    rowIndex++;
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        db.closeConnection();
    }
    
    @FXML
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
