package at.jku.ssw.cmm.quest.importpackage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import at.jku.ssw.cmm.gui.file.LoadStatics;
import at.jku.ssw.cmm.profile.Quest;

public class ImportQuests {
	
	/**
	 * Only for testing purposes
	 */
	public static void main(String[] args){
	/*	System.out.println(getPath());
		
		System.out.println(Quest.containsQuests("/home/peda/Dokumente",3));*/
		copyPackage();
	}
	
	private static void copyPackage(){
		File path = getPath();
		
		if(path.isDirectory()){
			if(Quest.containsQuests(path.getAbsolutePath(), 3)){
				System.out.println("Correct Quest Found!");
				
				//Copy Folder into Packages!
				try {
					copyFolder(path, new File("packages"));
					System.out.println("Copied Quests");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}/*else{
				
			}*/
		}
	}

	private static File getPath(){
		   JFileChooser chooser = new JFileChooser();
		    FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "ZIP-Files", "zip");
		    chooser.setFileFilter(filter);
		    
		    chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		    
		    int returnVal = chooser.showOpenDialog(chooser);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		       return chooser.getSelectedFile();
		    }else{
		    	return null;
		    }
	}
	
	
	/**
	 * Copying the whole Folder into another one!
	 * @param source
	 * @param destination
	 * @throws IOException
	 */
    public static void copyFolder(File source, File destination) throws IOException{
        	if(source.isDirectory()){
        		//if directory not exists, create it
        		if(!destination.exists()){
        		   destination.mkdir();
        		   System.out.println("Directory copied from " 
                                  + source + "  to " + destination);
        		}
     
        		//list all the directory contents
        		String files[] = source.list();
     
        		for (String file : files) {
        		   //construct the src and dest file structure
        		   File srcFile = new File(source, file);
        		   File destFile = new File(destination, file);
        		   //recursive copy
        		   copyFolder(srcFile,destFile);
        		}
     
        	}else{
        		LoadStatics.copyFileUsingStream(source, destination);
        	    System.out.println("File copied from " + source + " to " + destination);
        	}
        }
	
    /**
     * TODO Check if in there is a Quest in the ZIP File
     * Unzipping files
     * @param sourceFilePath
     * @throws IOException
     */
	private static void unzipFiles(String sourceFilePath,String destPath) throws IOException{
		FileInputStream fis = null;
		ZipInputStream zipIs = null;
		ZipEntry zEntry = null;
	        
	            fis = new FileInputStream(sourceFilePath);
	            zipIs = new ZipInputStream(new BufferedInputStream(fis));
	            
	            //Getting all Entry's
	            while((zEntry = zipIs.getNextEntry()) != null){
	            	try{
	                    byte[] tmp = new byte[4*1024];
	                    FileOutputStream fos = null;
	                    
	                    //Writing the unzipped Files into the located Destination
	                    fos = new FileOutputStream(destPath);
	                    int size = 0;
	                    while((size = zipIs.read(tmp)) != -1){
	                        fos.write(tmp, 0 , size);
	                    }
	                    fos.flush();
	                    fos.close();
	                } catch(Exception ex){
	                     
	                }
	            }
	            zipIs.close();
				}  
}
