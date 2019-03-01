package org.xmlfilter;

import com.jfoenix.controls.JFXButton;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.nio.charset.Charset.forName;

public class utils {
    private static SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");


    /**
     * Configure the filechooser Window
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
        try {//TODO: check Special characters.
            File fXmlFile = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            String str = FileUtils.readFileToString(fXmlFile, "UTF-8");

            // System.out.println(fXmlFile.toString());
            return dBuilder.parse(fXmlFile);


        }catch (NullPointerException e) {
            //TODO: check why this is appearing sometimes
            utils.HandleExceptions(e, "File Could not Be Loaded");
        }
        catch (Exception e) {
            //TODO: check why this is appearing sometimes
            utils.HandleExceptions(e, null);
        }


        return null;
    }

    static void savedocument(String p, ArrayList<Document> output) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();



            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("http://www.oracle.com/xml/is-standalone", "yes");
            Path path = Paths.get(p);
            String filename, outputPath;
            filename = path.getFileName().toString();
            outputPath = p.substring(0, p.indexOf(filename));

            //output.getDocumentElement().normalize();
            if(output.size()==2){
                TransformerFactory transformerFactory1 = TransformerFactory.newInstance();
                Transformer transformer1 = transformerFactory1.newTransformer();
                DOMSource sourceAllTrue = new DOMSource(output.get(0));
                DOMSource source = new DOMSource(output.get(1));
                StreamResult result = new StreamResult(new File(outputPath + filename.substring(0, filename.indexOf('.')) + "_new.xml"));
                StreamResult resultTrue = new StreamResult(new File(outputPath + filename.substring(0, filename.indexOf('.')) + "_Trues.xml"));
                transformer.transform(source, result);
                transformer1.transform(sourceAllTrue, resultTrue);
            }else{
                DOMSource source = new DOMSource(output.get(0));
                StreamResult result = new StreamResult(new File(outputPath + filename.substring(0, filename.indexOf('.')) + "_new.xml"));
                transformer.transform(source, result);
            }
      


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


     static void triggerAlert(String title,String msg)
     {
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
     * Filter Strings
     *
     * @param doc:source  file
     * @param after:Time  restriction
     * @param before:Time restriction
     * @return Document
     */
    static  ArrayList<Document> FilterStrings(Document doc, Date after, Date before, int type,boolean translationState) {

        try {
            ArrayList<Document> documents = new ArrayList<Document>();

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
                 //String Childrens
                if (nNode.hasChildNodes()) {   //String Childrens
                    NodeList stringChildrens = nNode.getChildNodes();
                    for (int x = 0; x < stringChildrens.getLength(); x++) {
                        Node childNode = stringChildrens.item(x);
                        if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement = (Element) childNode;
                             //Check the timestamps node
                            if (eElement.getTagName().equals("timestamps")) {
                                Date date = formatter.parse(eElement.getElementsByTagName("timestamp").item(0).getAttributes().item(0).getTextContent());
                                if (testDate(after, before, date, type)) {
                                    Element stringElement = (Element) nNode;
                                    if(translationState && stringElement.getAttribute("requirestranslation").equals("False")) {
                                        Document output1 = dBuilder.newDocument();
                                        Element rootElement1 = output1.createElement("strings");
                                        rootElement1.setAttribute("version", "1.0");
                                        output1.appendChild(rootElement);
                                        stringElement.setAttribute("requirestranslation","True");
                                        Node firstDocImportedNode1 = output1.importNode(nNode.cloneNode(true), true);
                                        rootElement1.appendChild(firstDocImportedNode1);
                                        documents.add(output1);
                                    }
                                    Node firstDocImportedNode = output.importNode(nNode.cloneNode(true), true);
                                    rootElement.appendChild(firstDocImportedNode);
                                }//end inner if
                            }
                        }
                    }
                }
            }
            documents.add(output);
            return documents;
            //end outer for
        } catch (Exception e) {
            utils.HandleExceptions(e, null);
        }
        return null;
    }


    /**
     * Change Id function
     * @param srcDoc : the document to check compare the text / Take Ids  from
     * @param destDoc: the destination file in which the Strings should be written
     * @param destComp : the file to compare the texts with
     * @return the output
     */
    static ArrayList<Document> changeId(Document srcDoc, Document destDoc, Document destComp,boolean sm) {
        int nbr = 0;
        String skippedIDs="";
        ArrayList<Document> documents = new ArrayList<Document>();
        try {
            //Create the new output file
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document output = dBuilder.newDocument();
            Element rootElement = output.createElement("strings");
            rootElement.setAttribute("version", "1.0");
            output.appendChild(rootElement);


            //Check if Strings should be compared (safe mode checkbox)
            if (sm) {
                //Get the Strings Nodelist
                srcDoc.getDocumentElement().normalize();
                destDoc.getDocumentElement().normalize();
                destComp.getDocumentElement().normalize();
                NodeList srcnList = srcDoc.getElementsByTagName("string");
                NodeList destnList = destDoc.getElementsByTagName("string");
                NodeList compList = destComp.getElementsByTagName("string");

                //get the number of elements in each file
                boolean srcCompStrNbr = srcnList.getLength() == compList.getLength();
                boolean destCompStrNbr = destnList.getLength() == compList.getLength();

                DOM.getUniqueStrings(srcnList);
                //check if the number of elements in each XML Document is the same
                if (srcCompStrNbr && destCompStrNbr) {
                    //TODO:get all element and filter comments
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
                            skippedIDs+=eSrcElement.getAttribute("id")+"\n";
                            nbr++;
                        }
                    }

                    FileUtils.writeStringToFile(new File("ids.txt"), skippedIDs, forName("UTF-8"));

                    utils.triggerAlert("Info","Done!\nString Skipped: " + nbr+"\n");
                } else {
                    utils.triggerAlert("Info","One of the Files has more Elements than the other. \n Src: " + srcnList.getLength()
                            + "\n File to Compare With: " + srcnList.getLength() + "\n Dest: " + srcnList.getLength());
                }

            }

            else {
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
                    utils.triggerAlert("Info","One of the Files has more Elements than the other. \n Src: " + srcnList.getLength()
                            + "\n Dest: " + srcnList.getLength());
                }

            }
            documents.add(output);

            return documents;
        } catch (Exception e) {
            utils.HandleExceptions(e, "Something Went Wrong when Changing IDs");
        }
        return null;
    }







}

