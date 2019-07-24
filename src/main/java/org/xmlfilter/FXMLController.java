package org.xmlfilter;


import com.jfoenix.controls.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.dom4j.*;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLController implements Initializable {

    private SimpleDateFormat inputformatter =
            new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @FXML
    private TabPane TabPane;

    /*XML FILTER CONTROLLER*/
    private int type;
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
    @FXML
    private JFXCheckBox tsState;
    @FXML
    private Tab XmlFilterTab;

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
    private CheckBox compareOnly;


    /*Audio Filter CONTROLLER*/
    private int type_audio;
    @FXML
    private Tab AudioFilterTab;
    @FXML
    private TextField stringdb_path;
    @FXML
    private TextField audio_report_path;
    @FXML
    private JFXButton chooseStringdbButton;
    @FXML
    private JFXButton chooseAudioReportButton;
    @FXML
    private ToggleGroup group_audio;
    @FXML
    private JFXRadioButton audio_before;
    @FXML
    private JFXRadioButton audio_after;
    @FXML
    private JFXRadioButton audio_exact;
    @FXML
    private JFXRadioButton audio_range;
    @FXML
    private JFXTimePicker fromTime_audio;
    @FXML
    private JFXDatePicker fromDate_audio;
    @FXML
    private JFXDatePicker toDate_audio;
    @FXML
    private JFXTimePicker toTime_audio;
    @FXML
    private JFXButton saveButton_audio;

    /**
     * Initialize the tabs
     *
     * @param location:  URL
     * @param resources: resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TabPane.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Tab>() {
                    @Override
                    public void changed(ObservableValue<? extends Tab> ov,
                                        Tab idChTab, Tab XmlFilterTab) {
                        if (TabPane.getSelectionModel().getSelectedItem().getId()
                                .equals("idChTab")) {
                            //clear first tab
                            tsState.setSelected(false);
                            filepath.setText("");
                            range.setSelected(true);
                            fromTime.setValue(null);
                            fromDate.setValue(null);
                            toDate.setDisable(false);
                            toTime.setDisable(false);
                            toDate.setValue(null);
                            toTime.setValue(null);
                            //clear third tab
                            stringdb_path.setText("");
                            audio_report_path.setText("");
                            audio_range.setSelected(true);
                            fromTime_audio.setValue(null);
                            fromDate_audio.setValue(null);
                            toDate_audio.setDisable(false);
                            toTime_audio.setDisable(false);
                            toDate_audio.setValue(null);
                            toTime_audio.setValue(null);
                        } else if (TabPane.getSelectionModel().getSelectedItem()
                                .getId().equals("XmlFilterTab")) {
                            //clear second tab
                            filepathSrc.setText("");
                            filepathDest.setText("");
                            filepathComp.setText("");
                            safeMode.setSelected(false);
                            compareOnly.setSelected(false);
                            filepathDest.setDisable(false);
                            filepathComp.setDisable(true);
                            chooseButtonComp.setDisable(true);
                            //clear third tab
                            stringdb_path.setText("");
                            audio_report_path.setText("");
                            audio_range.setSelected(true);
                            fromTime_audio.setValue(null);
                            fromDate_audio.setValue(null);
                            toDate_audio.setDisable(false);
                            toTime_audio.setDisable(false);
                            toDate_audio.setValue(null);
                            toTime_audio.setValue(null);
                        }
                        else if (TabPane.getSelectionModel().getSelectedItem()
                                .getId().equals("AudioFilterTab")) {
                            //clear second tab
                            filepathSrc.setText("");
                            filepathDest.setText("");
                            filepathComp.setText("");
                            safeMode.setSelected(false);
                            compareOnly.setSelected(false);
                            filepathDest.setDisable(false);
                            filepathComp.setDisable(true);
                            chooseButtonComp.setDisable(true);
                            //clear first tab
                            tsState.setSelected(false);
                            filepath.setText("");
                            range.setSelected(true);
                            fromTime.setValue(null);
                            fromDate.setValue(null);
                            toDate.setDisable(false);
                            toTime.setDisable(false);
                            toDate.setValue(null);
                            toTime.setValue(null);
                        }
                    }
                }
        );


    }

    /*Audio tool Functions*/

    /**
     * Range Radiobutton Function
     *
     * @param event
     */
    @FXML
    void handlerangeSelectedAudio(ActionEvent event) {

        type_audio = 0;
        if (this.toTime_audio.isDisable() && this.toDate_audio.isDisable()) {
            this.toTime_audio.setDisable(false);
            this.toDate_audio.setDisable(false);
        }
    }

    /**
     * Before Radiobutton function
     *
     * @param event
     */
    @FXML
    void handlebeforeSelectedAudio(ActionEvent event) {
        type_audio = 2;
        resetTotime_audio();

    }

    /**
     * Exact Radiobutton Function
     *
     * @param event
     */
    @FXML
    void handleexactSelectedAudio(ActionEvent event) {
        type_audio = 3;
        resetTotime_audio();
    }

    /**
     * After Radiobutton Function
     *
     * @param event
     */
    @FXML
    void handleafterSelectedAudio(ActionEvent event) {
        type_audio = 1;
        resetTotime_audio();
    }

    /**
     * Reset the to time and date fields
     */
    private void resetTotime_audio() {
        if (!(this.toTime_audio.isDisable() && this.toDate_audio.isDisable())) {
            this.toTime_audio.setDisable(true);
            this.toDate_audio.setDisable(true);
            this.toTime_audio.setValue(null);
            this.toDate_audio.setValue(null);
        }
    }



    /**
     * Choose File Button Function
     *
     * @param event
     */
    @FXML
    void handleChooseStringdbButton(ActionEvent event) {
        utils.loadOpenDialog(stringdb_path, chooseStringdbButton);
    }

    /**
     * Choose File Button Function
     *
     * @param event
     */
    @FXML
    void handleChooseAudioReportButton(ActionEvent event) {
        utils.loadOpenDialog(audio_report_path, chooseAudioReportButton);
    }

    /**
     * Trigger Filtering Strings: Save Button Function
     *
     * @param event
     */
    @FXML
    void handleSaveButtonAudio(ActionEvent event) {
        //file Path
        String stringdb_path = this.stringdb_path.getText();
        String audioReportPath = audio_report_path.getText();
        //Date & Time
        Date from = null;
        Date to = null;
        LocalTime fromtime = this.fromTime_audio.getValue();
        LocalTime totime = this.toTime_audio.getValue();
        LocalDate fromdate = this.fromDate_audio.getValue();
        LocalDate todate = this.toDate_audio.getValue();




        Document StringdbXML = null;
        List<String> audioReportList = null;
        try {
            from = inputformatter.parse(fromdate.toString() +
                    " " + fromtime.toString());

        if (group_audio.getSelectedToggle().equals(range)) {
                to = inputformatter.parse(todate.toString() +
                        " " + totime.toString());
            }
        } catch (Exception e) {
            utils.HandleExceptions(e, "Please Enter the Date and Time");
            e.printStackTrace();
        }



        try {
            StringdbXML = utils.loadDocument(stringdb_path);
            File file = new File(audioReportPath);
            audioReportList  = FileUtils.readLines(file,"UTF-8");
        } catch (Exception e) {
            utils.HandleExceptions(e, "Error when loading files");
            e.printStackTrace();
        }


        try {
            utils.saveaudioReport(utils.FilterAudioStrings(audioReportList,StringdbXML, from, to,
                   type_audio),stringdb_path);
        } catch (Exception e) {
            utils.HandleExceptions(e, "Error when Processing Audio Strings");
            e.printStackTrace();
        }

        utils.triggerAlert("Info", "Done!");


    }


    /* ID exchange Tool Functions */

    /**
     * Safe mode checkbox Function
     *
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
     *
     * @param event
     */
    @FXML
    void handleCompareOnlySelected(ActionEvent event) {

        if (this.compareOnly.isSelected()) {
            //Remove Selection from safe mode: no safe mode involved
            if (this.safeMode.isSelected()) {
                this.safeMode.setSelected(false);

            }
            this.safeMode.setDisable(true);
            //Disable chooser Destination
            this.chooseButtonDest.setDisable(true);
            this.filepathDest.setDisable(true);
            if (this.chooseButtonComp.isDisabled() &&
                    this.filepathComp.isDisabled()) {
                this.chooseButtonComp.setDisable(false);
                this.filepathComp.setDisable(false);
            }
        } else {
            //Enable chooser Destination
            this.chooseButtonComp.setDisable(true);
            this.filepathComp.setDisable(true);
            this.safeMode.setDisable(false);
            this.chooseButtonDest.setDisable(false);
            this.filepathDest.setDisable(false);

        }
    }

    /**
     * open the file to copy the Ids from
     *
     * @param event
     */
    @FXML
    void handlechooseButtonSrc(ActionEvent event) {

        utils.loadOpenDialog(filepathSrc, chooseButtonSrc);
    }

    /**
     * Open the file to copy the Ids into
     *
     * @param event
     */
    @FXML
    void handlechooseButtonDest(ActionEvent event) {

        utils.loadOpenDialog(filepathDest, chooseButtonDest);
    }

    /**
     * Open the File to compare with: Same language/texts / Different IdS
     *
     * @param event
     */
    @FXML
    void handlechooseButtonComp(ActionEvent event) {

        utils.loadOpenDialog(filepathComp, chooseButtonComp);
    }

    /**
     * Trigger Changing ID : Start Button Function
     *
     * @param event
     */
    @FXML
    void handleStartButton(ActionEvent event) {
        //Src file Path
        String src = filepathSrc.getText();
        String dest = filepathDest.getText();
        String comp = filepathComp.getText();


            Document srcDoc = null;
            Document destDoc = null;
            Document compDoc = null;
            if (this.compareOnly.isSelected()) {
                try {
                    srcDoc = utils.loadDocument(src);
                    compDoc = utils.loadDocument(comp);
                } catch (Exception e) {
                    e.printStackTrace();
                    utils.HandleExceptions(e, "Error when loading files");
                }
                Path path = Paths.get(src);
                String  outputPath;
                outputPath = src.substring(0,
                        src.indexOf(path.getFileName().toString()));
                try {
                ArrayList<String[]> strings=
                        utils.checkMultipleTranslations(srcDoc, compDoc);

                utils.writeDataInCsv(outputPath,strings);
                }catch (Exception e) {
                    e.printStackTrace();
                    utils.HandleExceptions(e, "Error when Comparing the files");
                }
            } else if (this.safeMode.isSelected()) {
                try {srcDoc = utils.loadDocument(src);
                destDoc = utils.loadDocument(dest);
                compDoc = utils.loadDocument(comp);
                } catch (Exception e) {
                    e.printStackTrace();
                    utils.HandleExceptions(e, "Error when loading files");
                }
                try {utils.saveOnedocument(dest, "_new.xml",
                        utils.changeId(srcDoc, destDoc, compDoc,
                                safeMode.isSelected()));
                }catch (Exception e) {
                    e.printStackTrace();
                    utils.HandleExceptions(e, "Error when Changing ids");
                }
            } else {
                try { srcDoc = utils.loadDocument(src);
                destDoc = utils.loadDocument(dest);
                } catch (Exception e) {
                    e.printStackTrace();
                    utils.HandleExceptions(e, "Error when loading files");
                }
                try {utils.saveOnedocument(dest, "_new.xml",
                        utils.changeId(srcDoc, destDoc, compDoc,
                                safeMode.isSelected()));
                } catch (Exception e) {
                    e.printStackTrace();
                    utils.HandleExceptions(e, "Error when changing ids");
                }
            }
            utils.triggerAlert("Info", "Done!");


    }

    /*Filter tool Functions*/

    /**
     * Range Radiobutton Function
     *
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
     *
     * @param event
     */
    @FXML
    void handlebeforeSelected(ActionEvent event) {
        type = 2;
        resetTotime();

    }

    /**
     * Exact Radiobutton Function
     *
     * @param event
     */
    @FXML
    void handleexactSelected(ActionEvent event) {
        type = 3;
        resetTotime();
    }

    /**
     * After Radiobutton Function
     *
     * @param event
     */
    @FXML
    void handleafterSelected(ActionEvent event) {
        type = 1;
        resetTotime();
    }

    /**
     * Reset the to time and date fields
     */
    private void resetTotime() {
        if (!(this.toTime.isDisable() && this.toDate.isDisable())) {
            this.toTime.setDisable(true);
            this.toDate.setDisable(true);
            this.toTime.setValue(null);
            this.toDate.setValue(null);
        }
    }

    /**
     * Cancel Button Function
     *
     * @param event
     */
    @FXML
    void handlecancelButton(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Choose File Button Function
     *
     * @param event
     */
    @FXML
    void handlechooseButton(ActionEvent event) {
        utils.loadOpenDialog(filepath, chooseButton);
    }

    /**
     * Trigger Filtering Strings: Save Button Function
     *
     * @param event
     */
    @FXML
    void handleSaveButton(ActionEvent event) {
        //file Path
        String p = filepath.getText();
        //Date & Time
        Date from = null;
        Date to = null;
        LocalTime fromtime = this.fromTime.getValue();
        LocalTime totime = this.toTime.getValue();
        LocalDate fromdate = this.fromDate.getValue();
        LocalDate todate = this.toDate.getValue();
        boolean translationState = tsState.isSelected();




        Document doc = null;

        try {from = inputformatter.parse(fromdate.toString() +
                " " + fromtime.toString());
            if (group.getSelectedToggle().equals(range)) {
                to = inputformatter.parse(todate.toString() +
                        " " + totime.toString());
            }
        }catch (Exception e) {
            e.printStackTrace();
            utils.HandleExceptions(e, "Please Enter the Date and Time");
        }
        try {doc = utils.loadDocument(p);
            utils.savedocument(p, utils.FilterStrings(doc, from, to,
                    type, translationState));
        }catch (Exception e) {
            e.printStackTrace();
            utils.HandleExceptions(e, "Error Filtering the Strings");
        }

        utils.triggerAlert("Info", "Done!");


    }
}








