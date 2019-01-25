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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.ResourceBundle;


public class FXMLController implements Initializable {
    private int type;
    private SimpleDateFormat inputformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");

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

    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("Open XML");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Xml", "*.xml")
        );
    }

    /**
     * Display The Exception to the user
     *
     * @param e       Exception
     * @param Message The Message to display
     */
    private void HandleExceptions(Exception e, String Message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        if (Message == null)
            alert.setContentText(e.toString());
        else alert.setContentText(Message);
        alert.showAndWait();
    }

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

    @FXML
    void handlerangeSelected(ActionEvent event) {

        type = 0;
        if (this.toTime.isDisable() && this.toDate.isDisable()) {
            this.toTime.setDisable(false);
            this.toDate.setDisable(false);
        }
    }

    @FXML
    void handlebeforeSelected(ActionEvent event) {
        type = 2;
        if (!(this.toTime.isDisable() && this.toDate.isDisable())) {
            this.toTime.setDisable(true);
            this.toDate.setDisable(true);
        }

    }

    @FXML
    void handleexactSelected(ActionEvent event) {
        type = 3;
        if (!(this.toTime.isDisable() && this.toDate.isDisable())) {
            this.toTime.setDisable(true);
            this.toDate.setDisable(true);
        }
    }

    @FXML
    void handleafterSelected(ActionEvent event) {
        type = 1;
        if (!(this.toTime.isDisable() && this.toDate.isDisable())) {
            this.toTime.setDisable(true);
            this.toDate.setDisable(true);
        }
    }

    @FXML
    void handlecancelButton(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void handlechooseButton(ActionEvent event) {
        loadOpenDialog(filepath, chooseButton);
    }

    @FXML
    void handlechooseButtonSrc(ActionEvent event) {
        loadOpenDialog(filepathSrc, chooseButtonSrc);
    }

    @FXML
    void handlechooseButtonDest(ActionEvent event) {
        loadOpenDialog(filepathDest, chooseButtonDest);
    }

    @FXML
    void handlechooseButtonComp(ActionEvent event) {
        loadOpenDialog(filepathComp, chooseButtonComp);
    }

    void loadOpenDialog(TextField t, JFXButton btn) {
        Stage stage = (Stage) btn.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            t.setText(selectedFile.getAbsolutePath());
        }
    }

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
                Document doc = loadDocument(p);
                savedocument(p, FilterStrings(doc, from, to, type));
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Info");
                alert.setHeaderText(null);
                alert.setContentText("Done!");
                alert.showAndWait();
            } catch (NullPointerException e) {
                HandleExceptions(e, "Choose date and time");
            } catch (Exception e) {
                HandleExceptions(e, null);
            }
        }
        //Handle one Date options
        else {

            try {
                from = inputformatter.parse(fromdate.toString() + " " + fromtime.toString());
                Document doc = loadDocument(p);
                savedocument(p, FilterStrings(doc, from, to, type));
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Info");
                alert.setHeaderText(null);
                alert.setContentText("Done!");
                alert.showAndWait();
            } catch (NullPointerException e) {
                HandleExceptions(e, "Choose date and time");
            } catch (Exception e) {
                HandleExceptions(e, null);
            }
        }
    }

    @FXML
    void handleStartButton(ActionEvent event) {
        //Src file Path
        String src = filepathSrc.getText();
        String dest = filepathDest.getText();
        String comp = filepathComp.getText();

        try {
            Document srcDoc = loadDocument(src);
            Document destDoc = loadDocument(dest);
            Document destComp = loadDocument(comp);
            savedocument(dest, changeId(srcDoc, destDoc, destComp));
        } catch (NullPointerException e) {
            HandleExceptions(e, "Error0");
        } catch (Exception e) {
            HandleExceptions(e, "Error1");
        }
    }

    /**
     * Load Xml File
     *
     * @param path:file path
     * @return Document
     */
    private Document loadDocument(String path) {
        try {
            File fXmlFile = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            return dBuilder.parse(fXmlFile);


        } catch (Exception e) {
            HandleExceptions(e, "File Could not Be Loaded");
        }


        return null;
    }

    /**
     * Filter Strings
     *
     * @param doc:source  file
     * @param after:Time  restriction
     * @param before:Time restriction
     * @return Document
     */
    private Document FilterStrings(Document doc, Date after, Date before, int type) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document output = dBuilder.newDocument();
            Element rootElement = output.createElement("strings");
            rootElement.setAttribute("version", "1.0");
            output.appendChild(rootElement);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("string");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                //String Node
                Node nNode = nList.item(temp);
                if (nNode.hasChildNodes()) {   //String Childrens
                    NodeList timestamps = nNode.getChildNodes();
                    for (int x = 0; x < timestamps.getLength(); x++) {
                        Node timestamp = timestamps.item(x);
                        if (timestamp.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement = (Element) timestamp;
                            if (eElement.getTagName().equals("timestamps")) {
                                Date date = formatter.parse(eElement.getElementsByTagName("timestamp").item(0).getAttributes().item(0).getTextContent());
                                if (testDate(after, before, date, type)) {
                                    Node firstDocImportedNode = output.importNode(nNode, true);
                                    rootElement.appendChild(firstDocImportedNode);
                                }//end inner if
                            }
                        }
                    }
                }
            }
            return output;
            //end outer for
        } catch (Exception e) {
            HandleExceptions(e, null);
        }
        return null;
    }


    private Document changeId(Document srcDoc, Document destDoc, Document destComp) {
        int nbr = 0;
        String skippedIDs="";
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document output = dBuilder.newDocument();
            Element rootElement = output.createElement("strings");
            rootElement.setAttribute("version", "1.0");
            output.appendChild(rootElement);

            if (safeMode.isSelected()) {
                srcDoc.getDocumentElement().normalize();
                destDoc.getDocumentElement().normalize();
                destComp.getDocumentElement().normalize();
                NodeList srcnList = srcDoc.getElementsByTagName("string");
                NodeList destnList = destDoc.getElementsByTagName("string");
                NodeList compList = destComp.getElementsByTagName("string");
                boolean srcCompStrNbr = srcnList.getLength() == compList.getLength();
                boolean destCompStrNbr = destnList.getLength() == compList.getLength();
                //check if the number of elements in each XML Document is the same
                if (srcCompStrNbr && destCompStrNbr) {

                    for (int temp = 0; temp < srcnList.getLength(); temp++) {
                        boolean found = false;
                        Node nSrcNode = srcnList.item(temp);
                        Element eSrcElement = (Element) nSrcNode;
                        String srcText = eSrcElement.getTextContent();
                        //check if the src text and the text in the file to compare with are identical
                        for (int temp1 = 0; temp1 < compList.getLength(); temp1++) {
                            Node nCompNode = compList.item(temp1);
                            Element eCompElement = (Element) nCompNode;
                            String compText = eCompElement.getTextContent();
                            if (srcText.equals(compText)) {
                                found = true;
                                Node nDestNode = destnList.item(temp1);
                                Element eDestElement = (Element) nDestNode;
                                eDestElement.setAttribute("id", eSrcElement.getAttribute("id"));
                                Node firstDocImportedNode = output.importNode(nDestNode, true);
                                rootElement.appendChild(firstDocImportedNode);
                                break;
                            }
                        }
                        if (!found) {
                            skippedIDs=eSrcElement.getAttribute("id")+"\n";
                            nbr++;
                        }
                    }
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Info");
                    alert.setHeaderText(null);
                    alert.setContentText("Done!\nString Skipped: " + nbr+"\n"+skippedIDs);
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Info");
                    alert.setHeaderText(null);
                    alert.setContentText("One of the Files has more Elements than the other. \n Src: " + srcnList.getLength()
                            + "\n File to Compare With: " + srcnList.getLength() + "\n Dest: " + srcnList.getLength());
                    alert.showAndWait();
                }
            } else {
                srcDoc.getDocumentElement().normalize();
                destDoc.getDocumentElement().normalize();
                NodeList srcnList = srcDoc.getElementsByTagName("string");
                NodeList destnList = destDoc.getElementsByTagName("string");
                boolean srcDestStrNbr = srcnList.getLength() == destnList.getLength();
                //check if the number of elements in each XML Document is the same
                if (srcDestStrNbr) {

                    for (int temp = 0; temp < srcnList.getLength(); temp++) {
                        //String Node
                        Node nSrcNode = srcnList.item(temp);
                        Node nDestNode = destnList.item(temp);
                        Element eSrcElement = (Element) nSrcNode;
                        Element eDestElement = (Element) nDestNode;
                        eDestElement.setAttribute("id", eSrcElement.getAttribute("id"));
                        Node firstDocImportedNode = output.importNode(nDestNode, true);
                        rootElement.appendChild(firstDocImportedNode);
                    }

                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Info");
                    alert.setHeaderText(null);
                    alert.setContentText("One of the Files has more Elements than the other. \n Src: " + srcnList.getLength()
                            + "\n Dest: " + srcnList.getLength());
                    alert.showAndWait();
                }

            }


            return output;
        } catch (Exception e) {
            HandleExceptions(e, "Something Went Wrong when Changing IDs");
        }
        return null;
    }

    private boolean testDate(Date afterDate, Date beforeDate, Date currentDate, int type) {

        switch (type) {
            //Range
            case 0:
                if (beforeDate != null && currentDate.after(afterDate) && currentDate.before(beforeDate))
                    return true;
                break;
            //after
            case 1:
                if (currentDate.after(afterDate)) return true;
                break;
            //before
            case 2:
                if (currentDate.before(afterDate)) return true;
                break;
            //Exact
            case 3:

                if (currentDate.equals(afterDate)) return true;
                break;

        }
        return false;
    }

    private void savedocument(String p, Document output) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(output);
            Path path = Paths.get(p);
            String filename, outputPath;
            filename = path.getFileName().toString();
            outputPath = p.substring(0, p.indexOf(filename));
            StreamResult result = new StreamResult(new File(outputPath + filename.substring(0, filename.indexOf('.')) + "_new.xml"));
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}








