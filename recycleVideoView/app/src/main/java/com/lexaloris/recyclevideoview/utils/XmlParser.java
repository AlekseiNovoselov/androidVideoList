package com.lexaloris.recyclevideoview.utils;

import com.lexaloris.recyclevideoview.models.Video;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XmlParser {

    final private static String ITEM_TAG = "item";
    final private static String GIF_TAG = "gif";
    final private static String URL_ATTRIBUTE = "url";
    final private static String HEADER = "header";
    final private static String FOOTER = "footer";

    public void getVideoUrls(ArrayList<Video> urls, InputStream inputStream)
            throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory
                = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Document doc = dBuilder.parse(inputStream);
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName(ITEM_TAG);
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                Element mp4 = (Element) eElement.getElementsByTagName(GIF_TAG).item(0);
                String header = eElement.getElementsByTagName(HEADER).item(0).getTextContent();
                String footer = eElement.getElementsByTagName(FOOTER).item(0).getTextContent();
                Video video = new Video(String.valueOf(temp), header, mp4.getAttribute(URL_ATTRIBUTE), footer);
                urls.add(video);
            }
        }
    }
}
