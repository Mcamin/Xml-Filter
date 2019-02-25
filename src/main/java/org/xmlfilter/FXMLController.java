package org.xmlfilter;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTimePicker;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.ResourceBundle;

import static java.nio.charset.Charset.forName;


public class FXMLController implements Initializable {
    private int type;
    private SimpleDateFormat inputformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");


    /*XML FILTER CONTROLLER*/
    @FXML
    private ToggleGroup group;
    @FXML
    private JFXButton chooseButton;
    @FXML
    private JFXButton cancelButton;
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXTimePicker fromTime;
    @FXML
    private JFXDatePicker fromDate;
    @FXML
    private JFXDatePicker toDate;
    @FXML
    private JFXTimePicker toTime;
    @FXML
    private TextField filepath;
    @FXML
    private JFXRadioButton before;
    @FXML
    private JFXRadioButton after;
    @FXML
    private JFXRadioButton exact;
    @FXML
    private JFXRadioButton range;

    /*ID Changer CONTROLLER*/
    @FXML
    private JFXButton chooseButtonSrc;
    @FXML
    private JFXButton chooseButtonDest;
    @FXML
    private JFXButton chooseButtonComp;
    @FXML
    private TextField filepathDest;
    @FXML
    private TextField filepathSrc;
    @FXML
    private TextField filepathComp;
    @FXML
    private JFXButton startButton;
    @FXML
    private Tab idChTab;
    @FXML
    private CheckBox safeMode;
    @FXML
    private Tab XmlFilterTab;
    @FXML
    private TabPane TabPane;
    @FXML
    private CheckBox compareOnly;

    /**
     * Initialize the tabs
     * @param location: URL
     * @param resources: resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TabPane.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Tab>() {
                    @Override
                    public void changed(ObservableValue<? extends Tab> ov, Tab idChTab, Tab XmlFilterTab) {
                        if (TabPane.getSelectionModel().getSelectedItem().getId().equals("idChTab")) {

                            filepath.setText("");
                        } else if (TabPane.getSelectionModel().getSelectedItem().getId().equals("XmlFilterTab")) {
                            safeMode.setSelected(true);
                            filepathSrc.setText("");
                            filepathDest.setText("");
                            filepathComp.setText("");
                        }
                    }
                }
        );


    }

    /*Filter tool Functions*/

    /**
     * Range Radiobutton Function
     * @param event
     */
    @FXML
    void handlerangeSelected(ActionEvent event) {

        type = 0;
        if (this.toTime.isDisable() && this.toDate.isDisable()) {
            this.toTime.setDisable(false);
            this.toDate.setDisable(false);
        }
    }

    /**
     * Before Radiobutton function
     * @param event
     */
    @FXML
    void handlebeforeSelected(ActionEvent event) {
        type = 2;
        if (!(this.toTime.isDisable() && this.toDate.isDisable())) {
            this.toTime.setDisable(true);
            this.toDate.setDisable(true);
        }

    }

    /**
     * Exact Radiobutton Function
     * @param event
     */
    @FXML
    void handleexactSelected(ActionEvent event) {
        type = 3;
        if (!(this.toTime.isDisable() && this.toDate.isDisable())) {
            this.toTime.setDisable(true);
            this.toDate.setDisable(true);
        }
    }

    /**
     * After Radiobutton Function
     * @param event
     */
    @FXML
    void handleafterSelected(ActionEvent event) {
        type = 1;
        if (!(this.toTime.isDisable() && this.toDate.isDisable())) {
            this.toTime.setDisable(true);
            this.toDate.setDisable(true);
        }
    }

    /**
     * Cancel Button Function
     * @param event
     */
    @FXML
    void handlecancelButton(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Choose File Button Function
     * @param event
     */
    @FXML
    void handlechooseButton(ActionEvent event) {
        utils.loadOpenDialog(filepath, chooseButton);
    }

    /**
     * Trigger Filtering Strings: Save Button Function
     * @param event
     */
    @FXML
    void handleSaveButton(ActionEvent event) {
        //file Path
        String p = filepath.getText();
        //Date & Time
        Date from;
        Date to = null;
        LocalTime fromtime = this.fromTime.getValue();
        LocalTime totime = this.toTime.getValue();
        LocalDate fromdate = this.fromDate.getValue();
        LocalDate todate = this.toDate.getValue();

        //Handle Range Options
        if (group.getSelectedToggle().equals(range)) {

            try {
                from = inputformatter.parse(fromdate.toString() + " " + fromtime.toString());
                to = inputformatter.parse(todate.toString() + " " + totime.toString());
                Document doc = utils.loadDocument(p);
                utils.savedocument(p, utils.FilterStrings(doc, from, to, type));
                utils.triggerAlert("Info","Done!");

            } catch (NullPointerException e) {
                utils.HandleExceptions(e, "Choose date and time");
            } catch (Exception e) {
                utils.HandleExceptions(e, null);
            }
        }
        //Handle one Date options
        else {

            try {
                from = inputformatter.parse(fromdate.toString() + " " + fromtime.toString());
                Document doc = utils.loadDocument(p);
                utils.savedocument(p, utils.FilterStrings(doc, from, to, type));
                utils.triggerAlert("Info","Done!");
            } catch (NullPointerException e) {
                utils.HandleExceptions(e, "Choose date and time");
            } catch (Exception e) {
                utils.HandleExceptions(e, null);
            }
        }
    }


    /* ID exchange Tool Functions */

    /**
     * Safe mode checkbox Function
     * @param event
     */
    @FXML
    void handleSafeModeSelected(ActionEvent event) {

        if (!this.safeMode.isSelected()) {
            this.chooseButtonComp.setDisable(true);
            this.filepathComp.setDisable(true);
        } else {
            this.chooseButtonComp.setDisable(false);
            this.filepathComp.setDisable(false);

        }
    }

    /**
     * Compare only checkbox
     * @param event
     */
    @FXML
    void handleCompareOnlySelected(ActionEvent event) {

        if (this.compareOnly.isSelected()) {
            //Remove Selection from safe mode: no safe mode involved
            if(this.safeMode.isSelected()){
                this.safeMode.setSelected(false);

            }
            this.safeMode.setDisable(true);
           //Disable chooser Destination
            this.chooseButtonDest.setDisable(true);
            this.filepathDest.setDisable(true);
        } else {
            //Enable chooser Destination
            this.safeMode.setDisable(false);
            this.chooseButtonDest.setDisable(false);
            this.filepathDest.setDisable(false);

        }
    }

    /**
     * open the file to copy the Ids from
     * @param event
     */
    @FXML
    void handlechooseButtonSrc(ActionEvent event) {
        utils.loadOpenDialog(filepathSrc, chooseButtonSrc);
    }

    /**
     * Open the file to copy the Ids into
     * @param event
     */
    @FXML
    void handlechooseButtonDest(ActionEvent event) {
        utils.loadOpenDialog(filepathDest, chooseButtonDest);
    }

    /**
     * Open the File to compare with: Same language/texts / Different IdS
     * @param event
     */
    @FXML
    void handlechooseButtonComp(ActionEvent event) {
        utils.loadOpenDialog(filepathComp, chooseButtonComp);
    }

    /**
     * Trigger Changing ID : Start Button Function
     * @param event
     */
    @FXML
    void handleStartButton(ActionEvent event) {
        //Src file Path
        String src = filepathSrc.getText();
        String dest = filepathDest.getText();
        String comp = filepathComp.getText();

        try {
            Document srcDoc = utils.loadDocument(src);
            Document destDoc = utils.loadDocument(dest);
            Document destComp = utils.loadDocument(comp);
            utils.savedocument(dest, utils.changeId(srcDoc, destDoc, destComp,safeMode.isSelected()));
        } catch (NullPointerException e) {
            utils.HandleExceptions(e, "One of the Files could not be found");
        } catch (Exception e) {
            utils.HandleExceptions(e, null);
        }
    }







}








