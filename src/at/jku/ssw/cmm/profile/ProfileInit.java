package at.jku.ssw.cmm.profile;

import java.util.ArrayList;

import at.jku.ssw.cmm.filemanagenment.Handler;
import at.jku.ssw.cmm.quests.datastructs.Quest;
import at.jku.ssw.cmm.quests.datastructs.Reward;

public class ProfileInit {
	static Profile profile = new Profile();
	Reward reward = new Reward();
	
	static ArrayList<Quest> quests = new ArrayList<>();  
	
	public static void init(String profileName , String profileFile){
		
	}
	
	 public static void main(String[] args) {
		 String questPath = "quests";
		 String rewardPath = "rewards";
		 String profilePath = "profiles";
		 String initPath = "packages";
		 
		 String folder;
		 ArrayList<Quest> quests = new ArrayList<>();
		 ArrayList<String> profileNames = new ArrayList<>();
		 Reward reward;
		 
		  Handler handler = new Handler( initPath, questPath, rewardPath, profilePath);
		  handler.init();
		  
		  System.out.println("----------Packages--------------");		  
		  for(int i = 0; i < handler.getFolderNames().size(); i++)
			  System.out.println(handler.getFolderNames().get(i));
		  
		  folder = handler.getFolderNames().get(1);	
		  quests = handler.ReadAllQuests(folder);
		  
		  System.out.println("----------Quests-List-------------");  
		  for(int i = 0; i < quests.size(); i++)
			  System.out.println(quests.get(i).getTitle() + "  "+ quests.get(i).getPath());
		  
		  
		  System.out.println("---------Profile Read-----------------");	
		  //handler.ReadFileNames(handler.getProfilePath(), subfolder)
		  profileNames = handler.ReadAllProfileNames();
		  
		  for(int i = 0; i < profileNames.size(); i++)
			  System.out.println(
					  "Nick: " + handler.ProfileRead(profileNames.get(i)).getNick() +
					  "\nselectedimage: " + handler.ProfileRead(profileNames.get(i)).getSelectedImage() +
					  "\nXP: " + handler.ProfileRead(profileNames.get(i)).getXp() +
					  "\nPath: " + handler.ProfileRead(profileNames.get(i)).getPath()
					  
					  );
		  
		  System.out.println("---------Testing Profile-----------------");		  
		  profile = handler.ProfileRead(profileNames.get(0));
		  profile.addXP(20);
		  profile.AddFinished(quests.get(0).getPath());
		  handler.ProfileStore(profile.getPath(), profile);
		  System.out.println(profile.getXp());
		  
		 String sep = System.getProperty("file.separator");
		 
		 Quest currentQuest = handler.ReadAllQuests(profile).get(0);
			
		  System.out.println("---------Testing Reward-----------------");
		  reward = handler.ReadReward(currentQuest);
		  System.out.println(reward.getTitle());
	 }

	/**
	 * @return the profile
	 */
	public static Profile getProfile() {
		return profile;
	}

	/**
	 * @param profile the profile to set
	 */
	public static void setProfile(Profile profile) {
		ProfileInit.profile = profile;
	}

	/**
	 * @return the quests
	 */
	public static ArrayList<Quest> getQuests() {
		return quests;
	}

	/**
	 * @param quests the quests to set
	 */
	public static void setQuests(ArrayList<Quest> quests) {
		ProfileInit.quests = quests;
	}
	   
	   
}
