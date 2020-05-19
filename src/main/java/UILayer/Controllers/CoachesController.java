package UILayer.Controllers;

import DataForTest.DataBase;
import ServiceLayer.UserManagement;
import UILayer.Main;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class CoachesController extends Controller {

    @FXML
    TableView<SimpleStringProperty> coaches_table = new TableView<>();

    @FXML
    TextFlow personal_page = new TextFlow();




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Stage s = Main.getStage();
        s.setOnCloseRequest(e->{
            e.consume();
            closeProgram();
        });
        showPlayers();
    }

    private void showPlayers() {
        HashMap<String, String> coaches_name = clientController.getAllCoaches();
        ArrayList<SimpleStringProperty> properties_coaches_name = new ArrayList<>();
        for(String user_name: coaches_name.keySet()){
            String full_name = coaches_name.get(user_name);
            properties_coaches_name.add(new SimpleStringProperty(null,full_name, user_name));
        }
        /* Column 1: coach's name*/
        ObservableList<SimpleStringProperty> coaches_name_data = FXCollections.observableArrayList(properties_coaches_name);
        TableColumn nameCol = new TableColumn("Coach");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        /* Column 2: show-page button */
        TableColumn buttonCol = new TableColumn("Pages");
        buttonCol.setCellValueFactory(new PropertyValueFactory<>(""));


        Callback<TableColumn<SimpleStringProperty, String>, TableCell<SimpleStringProperty, String>> cellFactory
                = //
                new Callback<TableColumn<SimpleStringProperty, String>, TableCell<SimpleStringProperty, String>>() {
                    @Override
                    public TableCell call( TableColumn<SimpleStringProperty, String> param) {
                        TableCell<SimpleStringProperty, String> cell = new TableCell<SimpleStringProperty, String>() {
                            Button btn = new Button("Show Coach's Page");
                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        SimpleStringProperty simple = getTableView().getItems().get(getIndex());
                                        showCoachPage(simple.getValue(), simple.getName());
                                    });
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };

        buttonCol.setCellFactory(cellFactory);

        /* Column 3: follow button */
        if(userType!= null && userType.equals("Fan")) {


            TableColumn buttonCol2 = new TableColumn("");
            buttonCol.setCellValueFactory(new PropertyValueFactory<>(""));

            Callback<TableColumn<SimpleStringProperty, String>, TableCell<SimpleStringProperty, String>> cellFactory2
                    = //
                    new Callback<TableColumn<SimpleStringProperty, String>, TableCell<SimpleStringProperty, String>>() {
                        @Override
                        public TableCell call(TableColumn<SimpleStringProperty, String> param) {
                            TableCell<SimpleStringProperty, String> cell = new TableCell<SimpleStringProperty, String>() {
                                Button btn = new Button("Follow");

                                @Override
                                public void updateItem(String item, boolean empty) {
                                    super.updateItem(item, empty);
                                    if (empty) {
                                        setGraphic(null);
                                        setText(null);
                                    } else {
                                        btn.setOnAction(event -> {
                                            SimpleStringProperty simple = getTableView().getItems().get(getIndex());
                                            followPage(simple.getValue(), simple.getName());
                                        });
                                        setGraphic(btn);
                                        setText(null);
                                    }
                                }
                            };
                            return cell;
                        }
                    };
            buttonCol2.setCellFactory(cellFactory2);
            /* Put all columns in table */
            coaches_table.setItems(coaches_name_data);
            coaches_table.getColumns().addAll(nameCol, buttonCol, buttonCol2);
            return;
        }
        else{
            /* Put all columns in table */
            coaches_table.setItems(coaches_name_data);
            coaches_table.getColumns().addAll(nameCol, buttonCol);
        }
    }

    private void showCoachPage(String user_name, String full_name) {

        String details = clientController.getCoachPageDetails(user_name);
        ArrayList<String> history = clientController.getPageHistory(user_name);

        if(details==null || history==null){
            personal_page.getChildren().clear();
            personal_page.getChildren().add(new Text(full_name + " has no personal page"));
            return;
        }

        String[] split_details = details.split(",");
        personal_page.getChildren().clear();
        personal_page.getChildren().add(new Text(full_name + "'s Personal Page:" + "\n"));
        for(String detail: split_details){
            personal_page.getChildren().add(new Text(detail + "\n"));
        }
        personal_page.getChildren().add(new Text("Team History: "));
        for(String team: history){
            personal_page.getChildren().add(new Text(team + ", "));
        }

    }

    private void followPage(String user_name_coach, String full_name) {
        // 1. get pageName
        String details = clientController.getCoachPageDetails(user_name_coach);
        if(details==null ){
            showAlert(Alert.AlertType.ERROR, "Form Error!", full_name + " has no page to follow.");
            return;
        }
        String[] split_details = details.split(",");
        String pageName = split_details[0];
        // 2. follow
        clientController.followPage(pageName);
        showAlert(Alert.AlertType.INFORMATION, "Success!", userName + ", now you are following " +full_name + "'s page" );
    }
}
