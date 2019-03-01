package org.xmlfilter;


import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DOM {


    static public Map getUniqueStrings(NodeList srcnList){
        Map<String, List<String>> UniqueStrings =  new HashMap<>();
        for (int temp = 0; temp < srcnList.getLength(); temp++) {
            
            Node nSrcNode = srcnList.item(temp);
            Element eSrcElement = (Element) nSrcNode;
            String srcText = eSrcElement.getTextContent();
            List<String> idsList = new ArrayList<String>();
            if(UniqueStrings.containsKey(srcText)){
            idsList =UniqueStrings.get(srcText);
            }
            idsList.add(eSrcElement.getAttribute("id"));
            UniqueStrings.put(srcText,idsList);

              }
        return UniqueStrings;
    }
    static public Map CheckMultipleTranslation(Map<String,List<String>> uniquesrcStrings,Map<String,List<String>>uniquedestStrings)
    {
        for (Map.Entry<String, List<String>> entry : uniquesrcStrings.entrySet())
        {
            System.out.println(entry.getKey() + "/" + entry.getValue());
        }
        return null;
    }
}
