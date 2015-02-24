package at.jku.ssw.cmm.quest.importexport;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

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
		
		/**
		 * Checks if there are Quests in the Folders
		 */
		if(path.isDirectory()){
			if(Quest.containsQuests(path.getAbsolutePath(), 3)){
				System.out.println("Correct Quest Found!");
				
				//Copy Folder into Packages!
				try {
					LoadStatics.copyFolder(path, new File("packages"));
					System.out.println("Copied Quests");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			/**
			 * Checks if the File is a Zip File
			 */
			}else if(path.isFile()){
				try {
					if(path.getName().endsWith(".zip") ){
						if(testZipFile(path.getPath()))	
							unzipFiles(path.getPath(), "packages");
						else
							System.out.println("Zip doesn't countains a Quest!");
					}
				} catch (ZipException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
     * TODO Check if in there is a Quest in the ZIP File
     * Unzipping files
     * @param sourceFilePath
     * @throws IOException
     */
	@SuppressWarnings("resource")
	private static void unzipFiles(String zipFile,String extractFolder) throws IOException{
	    try
	    {
	        int BUFFER = 2048;
	        File file = new File(zipFile);

	        ZipFile zip = new ZipFile(file);
	        String newPath = extractFolder;

	        new File(newPath).mkdir();
	        Enumeration<?> zipFileEntries = zip.entries();

	        // Process each entry
	        while (zipFileEntries.hasMoreElements())
	        {
	            // grab a zip file entry
	            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
	            String currentEntry = entry.getName();

	            File destFile = new File(newPath, currentEntry);
	            //destFile = new File(newPath, destFile.getName());
	            File destinationParent = destFile.getParentFile();

	            // create the parent directory structure if needed
	            destinationParent.mkdirs();

	            if (!entry.isDirectory())
	            {
	                BufferedInputStream is = new BufferedInputStream(zip
	                .getInputStream(entry));
	                int currentByte;
	                // establish buffer for writing file
	                byte data[] = new byte[BUFFER];

	                // write the current file to disk
	                FileOutputStream fos = new FileOutputStream(destFile);
	                BufferedOutputStream dest = new BufferedOutputStream(fos,
	                BUFFER);

	                // read and write until last byte is encountered
	                while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
	                    dest.write(data, 0, currentByte);
	                }
	                dest.flush();
	                dest.close();
	                is.close();
	            }


	        }
	    }
	    catch (Exception e) 
	    {
	    	e.printStackTrace();
	        //Log("ERROR: "+e.getMessage());
	    }
	}
	
	/**
	 * Returns true if the Zip file contains a Quest
	 * @return
	 * @throws IOException 
	 * @throws ZipException 
	 */
	@SuppressWarnings("resource")
	private static boolean testZipFile(String zipFile) throws ZipException, IOException{
		 File file = new File(zipFile);

	        ZipFile zip = new ZipFile(file);
	        Enumeration<?> zipFileEntries = zip.entries();

	        boolean ref = false;
	        boolean input = false;
	        
	        // Process each entry
	        while (zipFileEntries.hasMoreElements()){
	        	ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
	        	System.out.println(entry.getName());
	        	if(entry.getName().indexOf(Quest.FILE_REF)!= -1){
	        		ref = true;
	        	}else if(entry.getName().indexOf(Quest.FILE_INPUT_CMM)!= -1){
	        		input = true;
	        	}
	        	
	        	if(ref && input)
	        		return true;
	        }
	       	        
		return false;
	}
}
