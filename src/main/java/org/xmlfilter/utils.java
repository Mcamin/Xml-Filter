package org.xmlfilter;

import com.jfoenix.controls.JFXButton;
import com.opencsv.CSVWriter;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jsoup.Jsoup;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


import static java.nio.charset.Charset.forName;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;


public class utils {
    private static SimpleDateFormat formatter =
            new SimpleDateFormat("dd.MM.yyyy HH:mm");


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
       /* fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Xml", "*.xml"),
                new FileChooser.ExtensionFilter("Txt", "*.txt")
        );*/
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
    static Document loadDocument(String path) throws DocumentException {

        File fXmlFile = new File(path);
        SAXReader reader = new SAXReader();
        Document document = reader.read(fXmlFile);
        return document;
    }


    /**
     * Save the documents in the specified path
     *
     * @param p:         the path of the documents
     * @param documents: arraylist of  resulting documents
     */
    static void savedocument(String p, ArrayList<Document> documents)
            throws IOException {
        saveOnedocument(p, "_new.xml", documents.get(0));
        if (documents.get(1) != null) {
            saveOnedocument(p, "_Trues.xml", documents.get(1));

        }
    }

    /**
     * Save one document
     *
     * @param p        path
     * @param ext      name_extension
     * @param document document to write
     * @throws IOException
     */
    static void saveOnedocument(String p, String ext, Document document)
            throws IOException {

        Path path = Paths.get(p);
        String filename, outputPath;
        filename = path.getFileName().toString();
        outputPath = p.substring(0, p.indexOf(filename));

        String file = outputPath + filename.substring(0,
                filename.indexOf('.')) + ext;
        OutputStream out = new FileOutputStream(file);
        OutputFormat outFormat = OutputFormat.createPrettyPrint();
        outFormat.setEncoding("UTF-8");
        XMLWriter writer = new XMLWriter(out, outFormat);
        writer.write(document);
        writer.close();
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
    static boolean testDate(Date afterDate, Date beforeDate,
                            Date currentDate, int type) {

        switch (type) {
            //Range
            case 0:
                if (beforeDate != null && currentDate.after(afterDate)
                        && currentDate.before(beforeDate))
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
    static ArrayList<Document> FilterStrings(Document doc, Date after,
                                             Date before, int type,
                                             boolean translationState) throws ParseException {
        //Initialize Xml Output
        ArrayList<Document> documents = new ArrayList<Document>();
        Document output = createDocument();
        List<Node> list = doc.selectNodes("//string");
        Document output1 = null;
        if (translationState) {
            output1 = createDocument();
        }

        for (int i = 0; i < list.size(); i++) {
            Element temp = (Element) list.get(i);
            Date date = null;
            date = formatter.parse(
                    temp.selectSingleNode("timestamps/timestamp").valueOf("@actiondateliteral"));
            if (testDate(after, before, date, type)) {
                if (translationState &&
                        temp.valueOf("@requirestranslation").equals("False")) {
                    temp.addAttribute("requirestranslation", "True");
                    output1.getRootElement().addText("\n\t");
                    output1.getRootElement().add(temp.createCopy());

                }
                if (!translationState) {
                    output.getRootElement().addText("\n\t");
                    output.getRootElement().add(temp.createCopy());
                }

            }
            if (translationState) {
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
    static Document changeId(Document srcDoc, Document destDoc,
                             Document compDoc, boolean sm) throws IOException {
        int nbr = 0;
        String skippedIDs = "";


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

            //check if the number of elements in each XML
            // Document is the same
            if (srcCompStrNbr && destCompStrNbr) {

                //checkMultipleTranslations(compDoc, destDoc);
                for (int temp = 0; temp < srcnList.size(); temp++) {
                    boolean found = false;
                    Element nSrcNode = (Element) srcnList.get(temp);
                    String srcText = nSrcNode.selectSingleNode("content/langstring").getText();
                    //check if the src text and the text in the file to
                    // compare with are identical
                    for (int temp1 = 0; temp1 < compList.size(); temp1++) {

                        Element eCompElement = (Element) compList.get(temp1);
                        String compText = eCompElement.selectSingleNode("content/langstring").getText();
                        if (srcText.equals(compText)) {
                            found = true;
                            Element eDestElement = (Element) destnList.get(temp1);
                            eDestElement.addAttribute("id", nSrcNode.attributeValue("id"));
                            output.getRootElement().add(eDestElement.createCopy());
                            break;
                        }
                    }
                    if (!found) {
                        skippedIDs += nSrcNode.attributeValue("id") + " : " + srcText + "\n";
                        nbr++;
                    }
                }

                FileUtils.writeStringToFile(new File("ids.txt"), skippedIDs, forName("UTF-8"));

                utils.triggerAlert("Info", "Done!\nString Skipped: " + nbr + "\n");
            }
            //File have different elements number
            else {
                utils.triggerAlert("Info", "One of the Files has more Elements than the other. \n Src: " + srcnList.size()
                        + "\n File to Compare With: " + compList.size() + "\n Dest: " + destnList.size());

                if (srcnList.size() > compList.size()) {

                    for (Node temp : findDifference(srcnList, compList)) {
                        Element tempElement = (Element) temp;
                        output.getRootElement().addText("\n\t");
                        output.getRootElement().add(tempElement.createCopy());
                    }
                } else {
                    for (Node temp : findDifference(compList, srcnList)) {
                        Element tempElement = (Element) temp;
                        output.getRootElement().addText("\n\t");
                        output.getRootElement().add(tempElement.createCopy());
                    }
                }
            }
            //End elements mismatch
        }
        //End Safe Mode

        // No Safe Mode : copy ids one to one
        else {
            //check if the number of elements in each XML Document
            // is the same
            boolean srcDestStrNbr = srcnList.size() == destnList.size();
            if (srcDestStrNbr) {
                for (int temp = 0; temp < srcnList.size(); temp++) {
                    //String Node
                    Element eSrcElement = (Element) srcnList.get(temp);
                    Element eDestElement = (Element) destnList.get(temp);
                    eDestElement.addAttribute("id", eSrcElement.attributeValue("id"));
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


    }

    /**
     * return the multiple translation of a string element
     *
     * @param src  source file
     * @param dest destination file
     * @return array list of the multiple translation strings
     */
    static ArrayList<String[]> checkMultipleTranslations(Document src,
                                                         Document dest) {
        // Unique Source Strings / IDS
        Map<String, List<String>> UniqueStrings;

        //Translated Strings
        ArrayList<String[]> mt = new ArrayList<String[]>();

        //get the unique strings from the src file
        UniqueStrings = getUniqueStrings(src.selectNodes("//string"));


        //loop through the entries
        for (Map.Entry<String, List<String>> entry : UniqueStrings.entrySet()) {
            //get the ids for each string

            List<String> ids = entry.getValue();
            //to get the translated string in hashmap
            Map<String, List<String>> UniqueTsStrings = new HashMap<>();
            //List to put the ids gathered from the translated file


            //loop through the original file's ids
            for (int i = 0; i < ids.size(); i++) {
                //get the text from the destination file
                List<String> idsList = new ArrayList<String>();
                Node temp = dest.selectSingleNode("strings/string[@id='" + ids.get(i) + "']");
                Element edestElement = (Element) temp;

                String destText = edestElement.selectSingleNode("content/langstring").getText();
                //add it to the hashmap

                if (UniqueTsStrings.containsKey(destText)) {
                    idsList = UniqueTsStrings.get(destText);

                }

                idsList.add(edestElement.attributeValue("id"));
                UniqueTsStrings.put(destText, idsList);

            }

            if (UniqueTsStrings.size() > 1) {

                mt.add(new String[]{"Source Text:", entry.getKey()});
                System.out.println(UniqueTsStrings.size());
                for (Map.Entry<String, List<String>> ent :
                        UniqueTsStrings.entrySet()) {
                    mt.add(new String[]{"Translation:", ent.getKey()});

                    List<String> mid = ent.getValue();
                    System.out.println("mid size: " + mid.size());
                    String[] stringslist = new String[mid.size() + 1];
                    stringslist[0] = "IDS:";
                    for (int k = 0; k < mid.size(); k++) {
                        stringslist[k + 1] = mid.get(k);
                    }
                    mt.add(stringslist);
                }

            }
        }
        return mt;

    }

    /**
     * create K,V mapping Unique String and ids
     *
     * @param srcnList list of strings elements
     * @return hashmap
     */
    private static Map getUniqueStrings(List<Node> srcnList) {
        Map<String, List<String>> UniqueStrings = new HashMap<>();

        for (int temp = 0; temp < srcnList.size(); temp++) {

            Element eSrcElement = (Element) srcnList.get(temp);
            String srcText = eSrcElement.selectSingleNode("content/langstring").getText();
            List<String> idsList = new ArrayList<String>();
            if (UniqueStrings.containsKey(srcText)) {
                idsList = UniqueStrings.get(srcText);
            }
            idsList.add(eSrcElement.attributeValue("id"));
            UniqueStrings.put(srcText, idsList);

        }
        return UniqueStrings;
    }


    /**
     * Write results in csv file
     *
     * @param filePath : where to write the csv
     * @param strings  the multiple translation strings
     * @throws IOException
     */
    public static void writeDataInCsv(String filePath,
                                      ArrayList<String[]> strings)
            throws IOException {
        if (strings.size() > 0) {
            Writer writer = Files.newBufferedWriter(Paths.get(filePath + "multipletranslation.csv"));

            CSVWriter csvWriter = new CSVWriter(writer,
                    ';',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);


            // adding data to csv
            for (int i = 0; i < strings.size(); i++)
                csvWriter.writeNext(strings.get(i));
            // closing writer connection
            writer.close();
        }

    }


    /**
     * Find the difference between 2  String xml file
     * by eliminating the similar nodes
     *
     * @param srcnList
     * @param compList
     */
    static List<Node> findDifference(List<Node> srcnList, List<Node> compList) {
        for (int i = 0; i < compList.size(); i++) {
            Element nCompNode = (Element) compList.get(i);
            String compString = nCompNode.selectSingleNode("content/langstring").getText();
            for (int j = 0; j < srcnList.size(); j++) {
                Element nSrcNode = (Element) compList.get(j);
                String srcString = nSrcNode.selectSingleNode("content/langstring").getText();
                if (compString.equals(srcString)) {
                    srcnList.remove(j);
                    break;
                }
            }
        }
        return srcnList;
    }


    /**
     * Filter Audio Strings
     *
     * @param dexdoc:     audio file
     * @param stringdoc:  strings file
     * @param after:Time  restriction
     * @param before:Time restriction
     * @return Document
     */
    static ArrayList<LinkedHashMap> FilterAudioStrings(List<String> dexdoc, Document stringdoc, Date after,
                                             Date before, int type) throws ParseException {
        //Initialize Xml Output
        Document output = createDocument();
        List<Node> list = stringdoc.selectNodes("//string");
        ArrayList<LinkedHashMap>audionamemap;

        for (int i = 0; i < list.size(); i++) {
            Element temp = (Element) list.get(i);
            Date date = null;
            date = formatter.parse(
                    temp.selectSingleNode("timestamps/timestamp").valueOf("@actiondateliteral"));
            if (testDate(after, before, date, type)) {

                if (temp.valueOf("@audiotext").equals("true")) {
                    output.getRootElement().add(temp.createCopy());
                }

            }
        }
        audionamemap = matchStringstonames(output, dexdoc);


        return audionamemap;

    }

    /**
     * Map the ids from String export to the filenames from dex export
     *
     * @param dexdoc: audio file
     * @param output: string file
     * @return Document
     */
    static ArrayList<LinkedHashMap> matchStringstonames(Document output, List<String> dexdoc) {

        //Map<String, String> Report = new HashMap<>();
        LinkedHashMap<String,String> Report = new LinkedHashMap<>();
        LinkedHashMap<String,String> doubleEntries = new LinkedHashMap<>();
        List<Node> list = output.selectNodes("//string");
        Map<String, String> us = getUniqueStrings(list);
        ArrayList<LinkedHashMap>Outputs = new ArrayList<>();

        for (int j = 0; j < dexdoc.size(); j++) {
            for (Map.Entry<String, String> ent : us.entrySet()) {
                //for (int i = 0; i < list.size(); i++) {
                //Element temp = (Element) list.get(i);
                String srcTxt = Jsoup.parse(ent.getKey()).text();

                if (dexdoc.get(j).length() > 3) {
                    String text = Jsoup.parse(dexdoc.get(j).substring(dexdoc.get(j).indexOf("###") + 3)).text();
                    String filename = dexdoc.get(j).substring(0, dexdoc.get(j).indexOf("###")).trim();
                    //System.out.println(text+"\n");
                    //System.out.println(srcTxt+"\n\n");
                    if (text.replace(" ","").contains(srcTxt.replace(" ",""))) {
                        if(!Report.containsKey(filename))
                            Report.put(filename,text);
                        else
                            //TODO:this should be an arraylist
                            doubleEntries.put(filename,text);
                    }

                }

            }
        }
        Outputs.add(Report);
        Outputs.add(doubleEntries);

        return Outputs;
    }

    static void saveaudioReport(ArrayList<LinkedHashMap> outputs, String Path) throws IOException {
        String output = "";
        String doubleEntriesOutput = "";
        Path path = Paths.get(Path);
        String outputPath;
        outputPath = Path.substring(0,
                Path.indexOf(path.getFileName().toString()));
        LinkedHashMap<String,String> reportStringMap=outputs.get(0);
        LinkedHashMap<String,String> doubleEntriesStringMap=outputs.get(1);
        for (Map.Entry<String,String> x: reportStringMap.entrySet()) {
            output += x.getKey() + "###" + x.getValue() + "\n";
        }
        for (Map.Entry<String,String> y: doubleEntriesStringMap.entrySet()) {
            doubleEntriesOutput += y.getKey() + "###" + y.getValue() + "\n";
        }
        FileUtils.writeStringToFile(new File(outputPath + "AudioTextChangesReport.txt"), output, forName("UTF-8"));
        FileUtils.writeStringToFile(new File(outputPath + "DoubleEntries.txt"), doubleEntriesOutput, forName("UTF-8"));

    }


}

