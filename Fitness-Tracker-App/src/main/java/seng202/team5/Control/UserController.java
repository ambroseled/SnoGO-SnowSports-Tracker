package seng202.team5.Control;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import seng202.team5.DataManipulation.DataBaseController;
import seng202.team5.Model.User;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static seng202.team5.Model.CheckGoals.convertDate;


/**
 * This class handles the selection of the current user as well as the creation of new user
 * profiles to then be added to the database.
 */
public class UserController {

    @FXML
    private TableView userTable;
    @FXML
    private TableColumn<User, String> userCol;
    @FXML
    private Button commitDetails;
    @FXML
    private Label detailConfirm;
    @FXML
    private Button confirmUser;
    @FXML
    private TextField newUserName;
    @FXML
    private TextField newUserBirth;
    @FXML
    private TextField newUserHeight;
    @FXML
    private TextField newUserWeight;
    @FXML
    private Button removeButton;
    @FXML
    private Button selectButton;

    private String heightString;

    private int calculatedAge;

    private TextField selectedTextField;

    private ArrayList<User> users;

    private ProfController detailChecker = new ProfController();

    private DataBaseController db = App.getDb();

    private ObservableList<User> userNames = FXCollections.observableArrayList();

    private DateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy");



    // TODO Duplicate detection (Need access to database -> ERSKINE)



    @FXML
    /**
     * Called by mouse movement onto the tab; this method populated the shown table with the current users in the database
     */
    private void fillTable() {
        users = db.getUsers();
        if (userTable.getItems().size() != users.size()) {
            userTable.getItems().clear();
            userCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            userNames.addAll(users);
            userTable.setItems(userNames);
        }
        installListener(newUserWeight, newUserHeight);

    }

    private void refreshTable(){
        users = db.getUsers();
        userTable.getItems().clear();
        userCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        userNames.addAll(users);
        userTable.setItems(userNames);
    }

    /**
     * Called by the fillTable() method; this sets the current text fields to be focused on allowing a singular reference
     * to all of them.
     * @param textFields the current text fields to only allow numbers to be input.
     */
    private void installListener(TextField... textFields) {
        resetState();
        for (TextField textField : textFields) {
            textField.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
                selectedTextField = null;

                if (newValue) {
                    selectedTextField = textField;
                }
            });
        }
    }


    @FXML
    /**
     * Called by the commitDetails button; this method provides a buffer between committing the user details and
     * inputting them whilst also reminding the user to re-check that the information entered is in-fact correct.
     */
    private void confirmLabel() {
        detailConfirm.setVisible(true);
        commitDetails.setVisible(false);
        detailConfirm.setDisable(true);
        confirmUser.setDisable(false);
        confirmUser.setVisible(true);
    }


    /**
     * Called by the detailValidator method; this ensures that the name falls within the pre-set limits arbitrarily
     * determined.
     * @param name The name to be applied to the new user.
     * @return A boolean flag holding the result of the validation check.
     */
    private boolean checkName(String name) {
        // Checking the name is of valid length and is all alphabetical
        if (name.length() > 4 && name.length() < 30) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Called by the detailValidator method; this method calculates the user's age in years as a stand-alone text field is
     * not needed.
     * @param date The date manually entered in the "Birth Date" text field.
     * @return Returns the age in years.
     */
    private int calcAge(String date){
        DateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date current = new Date();
        int[] currentDate = convertDate(dateTimeFormat.format(current).split("/"));
        int[] compDate = convertDate(date.split("/"));
        calculatedAge = (currentDate[2] - compDate[2]);
        return calculatedAge;

    }


    @FXML
    /**
     * Called by the confirmUser button; this method allows the new user to be entered into the database providing all
     * formatting criteria are met.
     */
    private void detailValidator() {
        try {
            double heightConverted = (Double.parseDouble(newUserHeight.getText()) / 100);
            heightString = Double.toString(heightConverted);
            calcAge(newUserBirth.getText());
            if (checkName(newUserName.getText()) && detailChecker.checkWeight(newUserWeight.getText()) &&
                    detailChecker.checkHeight(heightString) && (calculatedAge < 100)) {
            confirmLabel();
        }
        } catch(NumberFormatException e){
            ErrorController.displayError("Invalid user syntax!");
        }
    }

    @FXML
    private void checkDuplicates(){
        boolean duplicate = false;
        try {
            for (User user : db.getUsers()) {
                String birthString = dateTimeFormat.format(user.getBirthDate());
                if ((user.getName().equals(newUserName.getText())) && (birthString.equals(newUserBirth.getText()))) {
                    duplicate = true;
                }
            }
            if (duplicate == true) {
                duplicateAlert();
                ErrorController.displayError("Duplicate Users!");
            } else {
                detailValidator();
            }
        } catch (NumberFormatException e){
            ErrorController.displayError("Error with duplicate check");
        }
    }



    private void duplicateAlert(){
        detailConfirm.setVisible(true);
        detailConfirm.setText("WARNING DUPLICATE USER!");
    }

    @FXML
    /**
     * Called by the pressing of a key in any of the text fields; this method essential reverts the confirmLabel method
     * allowing the process to restart.
     */
    private void resetState(){
        detailConfirm.setText("Check all details are correct!");
        detailConfirm.setVisible(false);
        commitDetails.setVisible(true);
        detailConfirm.setDisable(false);
        confirmUser.setDisable(true);
        confirmUser.setVisible(false);
    }


    @FXML
    /**
     * Called by entering text into any of the currently focused text fields; this method restricts the entry of the
     * weight and height text fields to only numbers.
     */
    private void restrictNumbers() {

        selectedTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    selectedTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }


    @FXML
    /**
     * Called by the dataValidator method (providing all syntax is correct); this method adds a new user to the database
     * using the entered details.
     */
    private void createNewUser(){
        try {
            String finalName = newUserName.getText();
            double finalWeight = Double.parseDouble(newUserWeight.getText());
            double finalHeight = Double.parseDouble(newUserHeight.getText());
            DateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date finalBirth = dateTimeFormat.parse(newUserBirth.getText());
            User user = new User(finalName, calculatedAge, finalHeight, finalWeight, finalBirth);
            db.storeNewUser(user);
            refreshTable();
            clearEntries();
        } catch(Exception e) {
            ErrorController.displayError("Failed to create new user");
        }
    }

    @FXML
    private void deleteUser(){
        User selectedUser = (User) userTable.getSelectionModel().getSelectedItem();
        db.removeUser(selectedUser);
        refreshTable();

    }

    @FXML
    private void checkUserSelected(){
        if (userTable.getSelectionModel().getSelectedItem() != null){
            removeButton.setDisable(false);
            selectButton.setDisable(false);
        }
    }

    @FXML
    private void setSelectedUser(){
        App.setCurrentUser((User) userTable.getSelectionModel().getSelectedItem());
        System.out.println(App.getCurrentUser());
    }


    private void clearEntries(){
        newUserBirth.clear();
        newUserWeight.clear();
        newUserHeight.clear();
        newUserName.clear();
    }

}

