package at.jku.ssw.cmm.quests.file;


import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import at.jku.ssw.cmm.quests.datastructs.Quest;

public class QuestContentHandler  {

	Quest quest;
	NodeList nodeList;
	
	public Quest Parse(FileInputStream file) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
        
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        
        String expression;
        
        DocumentBuilder builder;

		builder = builderFactory.newDocumentBuilder();

         
        Document xmlDocument = builder.parse(file);

        XPath xPath =  XPathFactory.newInstance().newXPath();
        
        quest= new Quest();
        
        expression = "/quest/title";
        nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
        quest.setTitle(nodeList.item(0).getFirstChild().getNodeValue());
        
        expression = "/quest/description";
        nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
        quest.setDescription(nodeList.item(0).getFirstChild().getNodeValue());
        
        expression = "/quest/image";
        nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
        quest.setImage(nodeList.item(0).getFirstChild().getNodeValue());
        
        expression = "/quest/cmmprogramm";
        nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
        quest.setCmmProgramm(nodeList.item(0).getFirstChild().getNodeValue());
        
        expression = "/quest/reward";
        nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
        quest.setRewardPath(nodeList.item(0).getFirstChild().getNodeValue());

        expression = "/quest/level";
        nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
        quest.setLevel(Integer.parseInt(nodeList.item(0).getFirstChild().getNodeValue()));
        
        expression = "/quest/nextquest";
        nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
        quest.setNextQuest(nodeList.item(0).getFirstChild().getNodeValue());
        
		return quest;
		
	}
	  
}
