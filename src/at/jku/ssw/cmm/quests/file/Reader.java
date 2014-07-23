package at.jku.ssw.cmm.quests.file;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import at.jku.ssw.cmm.quests.datastructs.Quest;
import at.jku.ssw.cmm.quests.datastructs.Reward;
import at.jku.ssw.cmm.quests.datastructs.Settings;

public class Reader {
	private String questpath;
	private String rewardpath;
	private ArrayList<String> filenames = new ArrayList<>();
	private ArrayList<Quest> allQuests = new ArrayList<>();
	private Settings settings;

	
	public Reader(String questpath, String rewardpath){
		this.questpath = questpath;
		this.rewardpath = rewardpath;
	}
	
	public void init(){
		ReadFolderFilenames();
	}
	
	private void ReadFolderFilenames() {
		File folder = new File(questpath);
		File[] listOfFiles = folder.listFiles();
		
		for (int i = 0; i < listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".xml")){
		    	  filenames.add(listOfFiles[i].getName());	
		    	  
		    	  try {
					allQuests.add(ReadQuest(listOfFiles[i].getName()));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

		      }
		}
 
	}	
	
	private Quest ReadQuest(String filename)throws SAXException, FileNotFoundException, IOException{
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			QuestContentHandler handler = new QuestContentHandler();
			

			//Pfad zur XML Datei
		      FileReader reader = new FileReader(questpath + System.getProperty("file.separator") + filename);
		      InputSource inputSource = new InputSource(reader);
		      
		      //Handler wird übergeben
		      xmlReader.setContentHandler(handler );

		      //Parsen wird gestartet
		      xmlReader.parse(inputSource);

		      //Aktuelle Quest wird zurueckgegeben
		     return handler.getQuest();        
	}
	
	
	//Sobald die Aufgabe fertig ist
	public Reward getReward(String filename) {
		try {
		
		
			
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		RewardContentHandler handler = new RewardContentHandler();
		

		//Pfad zur XML Datei
	      FileReader reader = new FileReader(rewardpath + System.getProperty("file.separator") + filename);
	      InputSource inputSource = new InputSource(reader);
	      
	      //Handler wird übergeben
	      xmlReader.setContentHandler(handler);

	      //Parsen wird gestartet
			xmlReader.parse(inputSource);
			
			//Return Value
		     return handler.getReward();
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		return null;

	}
	/*
	 * Getters and Setters	
	 */

	/**
	 * @return the path
	 */
	public String getPath() {
		return questpath;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.questpath = path;
	}

	
	/**
	 * @return one Quest
	 */
	public Quest getQuest(int index) {
		return allQuests.get(index);
	}

	
	/**
	 * @return the allQuests
	 */
	public ArrayList<Quest> getAllQuests() {
		return allQuests;
	}

	/**
	 * @param allQuests the allQuests to set
	 */
	public void setAllQuests(ArrayList<Quest> allQuests) {
		this.allQuests = allQuests;
	}

	
}
