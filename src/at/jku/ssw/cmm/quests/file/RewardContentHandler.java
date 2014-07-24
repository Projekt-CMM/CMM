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

import at.jku.ssw.cmm.quests.datastructs.Reward;

public class RewardContentHandler  {

	Reward reward;
	NodeList nodeList;
	
	public Reward Parse(FileInputStream file) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
        
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        
        String expression;
        
        DocumentBuilder builder;

		builder = builderFactory.newDocumentBuilder();

         
        Document xmlDocument = builder.parse(file);

        XPath xPath =  XPathFactory.newInstance().newXPath();
        
        reward = new Reward();
        
        expression = "/reward/title";
        nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
        reward.setTitle(nodeList.item(0).getFirstChild().getNodeValue());

        expression = "/reward/type";
        nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
        reward.setType(nodeList.item(0).getFirstChild().getNodeValue());
        
        expression = "/reward/description";
        nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
        reward.setDescription(nodeList.item(0).getFirstChild().getNodeValue());
        
        expression = "/reward/image";
        nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
        reward.setImage(nodeList.item(0).getFirstChild().getNodeValue());
        
        expression = "/reward/xp";
        nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
        reward.setXp(Integer.parseInt(nodeList.item(0).getFirstChild().getNodeValue()));
        
       
        
		return reward;
		
	}
	  
}
