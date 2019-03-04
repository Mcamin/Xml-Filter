package org.xmlfilter;

import com.jfoenix.controls.JFXButton;
import com.opencsv.CSVWriter;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.nio.charset.Charset.forName;


public class utils {
    private static SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");


    /**
     * Configure the filechooser Window
     *
     * @param fileChooser : a file Chooser
     */
    static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("Open XML");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Xml", "*.xml")
        );
    }

    /**
     * Load open dialog window
     *
     * @param t   the textfield to set the path to
     * @param btn the btn to open the window
     */
    static void loadOpenDialog(TextField t, JFXButton btn) {
        Stage stage = (Stage) btn.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            t.setText(selectedFile.getAbsolutePath());
        }
    }

    /**
     * Load Xml File
     *
     * @param path:file path
     * @return Document
     */
    static Document loadDocument(String path) {
        try {
            File fXmlFile = new File(path);
            SAXReader reader = new SAXReader();
            Document document = reader.read(fXmlFile);
            return document;
        } catch (NullPointerException e) {
            utils.HandleExceptions(e, "File Could not Be Loaded");
        } catch (Exception e) {
            HandleExceptions(e, null);
        }
        return null;
    }


    /**
     * Save the documents in the specified path
     *
     * @param p:         the path of the documents
     * @param documents: arraylist of  resulting documents
     */

    static void savedocument(String p, ArrayList<Document> documents) {
        try {

            saveOnedocument(p, "_new.xml", documents.get(0));
            if (documents.get(1) != null) {
                saveOnedocument(p, "_Trues.xml", documents.get(1));

            }

        } catch (Exception e) {
            HandleExceptions(e, null);
        }


    }

    static void saveOnedocument(String p, String ext, Document document) {
        try {
            Path path = Paths.get(p);
            String filename, outputPath;
            filename = path.getFileName().toString();
            outputPath = p.substring(0, p.indexOf(filename));
            String file = outputPath + filename.substring(0, filename.indexOf('.')) + ext;

            XMLWriter writer = new XMLWriter(new FileWriter(file));
            writer.write(document);
            writer.close();

        } catch (Exception e) {
            HandleExceptions(e, null);
        }


    }

    /**
     * Trigger Alerts
     *
     * @param title : alert title
     * @param msg   : message to display
     */
    static void triggerAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**
     * Display The Exception to the user
     *
     * @param e       Exception
     * @param Message The Message to display
     */
    static void HandleExceptions(Exception e, String Message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        if (Message == null)
            alert.setContentText(e.toString());
        else alert.setContentText(Message);
        alert.showAndWait();
    }

    /**
     * Test the dates
     *
     * @param afterDate
     * @param beforeDate
     * @param currentDate
     * @param type
     * @return
     */
    static boolean testDate(Date afterDate, Date beforeDate, Date currentDate, int type) {

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


    /**
     * Create an output document
     *
     * @return output document
     */
    public static Document createDocument() {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("strings")
                .addAttribute("version", "1.0");
        return document;

    }

    /**
     * Filter Strings
     *
     * @param doc:source  file
     * @param after:Time  restriction
     * @param before:Time restriction
     * @return Document
     */
    static ArrayList<Document> FilterStrings(Document doc, Date after, Date before, int type, boolean translationState) {

        //Initialize Xml Output
        ArrayList<Document> documents = new ArrayList<Document>();
        Document output = createDocument();
        List<Node> list = doc.selectNodes("//string");
        Document output1 = null;
        if (translationState) {
            output1 = createDocument();
        }
        System.out.println(list.size());
        for (int i = 0; i < list.size(); i++) {
            Element temp = (Element) list.get(i);
            Date date = null;
            try {
                date = formatter.parse(temp.selectSingleNode("timestamps/timestamp").valueOf("@actiondateliteral"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (testDate(after, before, date, type)) {
                if (translationState && temp.valueOf("@requirestranslation").equals("False")) {
                    temp.addAttribute("requirestranslation", "True");
                    output1.getRootElement().addText("\n\t");
                    output1.getRootElement().add(temp.createCopy());

                }
                output.getRootElement().addText("\n\t");
                output.getRootElement().add(temp.createCopy());
            }
        }
        output.getRootElement().addText("\n\t");
        if (output1 != null) {
            output1.getRootElement().addText("\n\t");
        }
        documents.add(output);
        documents.add(output1);
        return documents;

    }


    /**
     * Change Id function
     *
     * @param srcDoc   : the document to check compare the text / Take Ids  from
     * @param destDoc: the destination file in which the Strings should be written
     * @param compDoc  : the file to compare the texts with
     * @return the output
     */
    static Document changeId(Document srcDoc, Document destDoc, Document compDoc, boolean sm) {
        int nbr = 0;
        String skippedIDs = "";
        try {
            //Create the new output file
            Document output = createDocument();
            //Check if Strings should be compared (safe mode checkbox)
            List<Node> srcnList = srcDoc.selectNodes("//string");
            List<Node> destnList = destDoc.selectNodes("//string");
            if (sm) {

                List<Node> compList = compDoc.selectNodes("//string");

                //get the number of elements in each file
                boolean srcCompStrNbr = srcnList.size() == compList.size();
                boolean destCompStrNbr = destnList.size() == compList.size();

                //DOM.getUniqueStrings(srcnList);

                //check if the number of elements in each XML Document is the same
                if (srcCompStrNbr && destCompStrNbr) {

                    checkMultipleTranslations(compDoc, destDoc);
                    for (int temp = 0; temp < srcnList.size(); temp++) {
                        boolean found = false;
                        Element nSrcNode = (Element) srcnList.get(temp);
                        String srcText = nSrcNode.getText();
                        //check if the src text and the text in the file to compare with are identical
                        for (int temp1 = 0; temp1 < compList.size(); temp1++) {

                            Element eCompElement = (Element) compList.get(temp1);
                            String compText = eCompElement.getText();
                            if (srcText.equals(compText)) {
                                found = true;
                                Element eDestElement = (Element) destnList.get(temp1);
                                eDestElement.addAttribute("id", nSrcNode.attributeValue("id"));
                                output.getRootElement().add(eDestElement.createCopy());
                                break;
                            }
                        }
                        if (!found) {
                            skippedIDs += nSrcNode.attributeValue("id") + "\n";
                            nbr++;
                        }
                    }

                    FileUtils.writeStringToFile(new File("ids.txt"), skippedIDs, forName("UTF-8"));

                    utils.triggerAlert("Info", "Done!\nString Skipped: " + nbr + "\n");
                } else {
                    utils.triggerAlert("Info", "One of the Files has more Elements than the other. \n Src: " + srcnList.size()
                            + "\n File to Compare With: " + srcnList.size() + "\n Dest: " + srcnList.size());
                }

            } else {


                boolean srcDestStrNbr = srcnList.size() == destnList.size();
                //check if the number of elements in each XML Document is the same
                if (srcDestStrNbr) {

                    for (int temp = 0; temp < srcnList.size(); temp++) {
                        //String Node

                        Element eSrcElement = (Element) srcnList.get(temp);
                        Element eDestElement = (Element) destnList.get(temp);
                        eDestElement.addAttribute("id", eSrcElement.attributeValue("id"));
                        System.out.println(eDestElement.attributeValue("id"));
                        output.getRootElement().addText("\n\t");
                        output.getRootElement().add(eDestElement.createCopy());
                    }

                } else {
                    triggerAlert("Info", "One of the Files has more Elements than the other. \n Src: " + srcnList.size()
                            + "\n Dest: " + srcnList.size());
                }

            }

            output.getRootElement().addText("\n\t");
            return output;
        } catch (Exception e) {
            e.printStackTrace();
            HandleExceptions(e, "Something Went Wrong when Changing IDs");
        }
        return null;
    }

    static  ArrayList<String[]> checkMultipleTranslations(Document src, Document dest) {
        Map<String, List<String>> UniqueStrings = new HashMap<>();
        //Translated Strings
        ArrayList<String[]> mt = new ArrayList<String[]>();

        //get the unique strings from the src file
        UniqueStrings = getUniqueStrings(src.selectNodes("//string"));
        //loop through the entries
        for (Map.Entry<String, List<String>> entry : UniqueStrings.entrySet()) {
            List<String> ids = entry.getValue();

            Map<String, List<String>> UniqueTsStrings = new HashMap<>();
            //loop through the ids
            for (int i = 0; i < ids.size(); i++) {
                Node temp = dest.selectSingleNode("//string[@id='" + ids.get(i) + "']");
                Element eSrcElement = (Element) temp;
                String srcText = eSrcElement.getText();
                List<String> idsList = new ArrayList<String>();
                if (UniqueStrings.containsKey(srcText)) {
                    idsList = UniqueStrings.get(srcText);
                }
                idsList.add(eSrcElement.attributeValue("id"));
                UniqueTsStrings.put(srcText, idsList);

            }
            if(UniqueTsStrings.size()>1){
;                mt.add(new String[]{entry.getKey()});
                for (Map.Entry<String, List<String>> ent : UniqueTsStrings.entrySet()) {
                 mt.add(new String[]{ent.getKey()});
                    String[] x = {};
                    List<String>  mid = entry.getValue();
                    for (int i = 0; i < mid.size(); i++) {
                       x[i]=mid.get(i);
                    }
                    mt.add(x);
                }

            }
        }return mt;

    }

    private static Map getUniqueStrings(List<Node> srcnList) {
        Map<String, List<String>> UniqueStrings = new HashMap<>();
        for (int temp = 0; temp < srcnList.size(); temp++) {

            Element eSrcElement = (Element) srcnList.get(temp);
            String srcText = eSrcElement.getText();
            List<String> idsList = new ArrayList<String>();
            if (UniqueStrings.containsKey(srcText)) {
                idsList = UniqueStrings.get(srcText);
            }
            idsList.add(eSrcElement.attributeValue("id"));
            UniqueStrings.put(srcText, idsList);

        }
        return UniqueStrings;
    }
    public static void writeDataInCsv(String filePath,ArrayList<String[]> strings)
    {
        // first create file object for file placed at location
        // specified by filepath
        File file = new File(filePath+"multipletranslation.csv");
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);

            // adding data to csv
            for(int i=0;i<strings.size();i++)
            writer.writeNext(strings.get(i));

            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}

