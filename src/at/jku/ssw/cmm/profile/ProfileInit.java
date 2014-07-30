package at.jku.ssw.cmm.profile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
		  
		  System.out.println("\n----------Packages--------------");		  
		  for(int i = 0; i < handler.getFolderNames().size(); i++)
			  System.out.println(handler.getFolderNames().get(i));
		   
		  folder = handler.getFolderNames().get(0);	
		  quests = handler.ReadAllPackageQuests(folder);
		  
		  System.out.println("\n----------Quests-List-------------");  
		  for(int i = 0; i < quests.size(); i++)
			  System.out.println(quests.get(i).getTitle() + "  "+ quests.get(i).getPath());
		  
		  
		  System.out.println("\n---------Profile Read-----------------");	
		  profileNames = handler.ReadAllProfileNames();
		  Profile profilecurrent = new Profile();
		  
		  
		  for(int i = 0; i < profileNames.size(); i++){
			  profilecurrent = handler.ProfileRead(profileNames.get(i));
			  System.out.println("Nick name: " + profilecurrent.getNick());
		  }
			  
		  
		  System.out.println("\n---------Testing Profile-----------------");		  
		  profile = handler.ProfileRead(profileNames.get(0));
		  profile.addXP(20);
		  profile.AddFinished(quests.get(0).getPath());
		  handler.ProfileStore(profile.getPath(), profile);
		  System.out.println(profile.getXp());
		  
		 
		 Quest currentQuest = handler.ReadAllFinishedQuests(profile).get(0);
		 Quest selectableQ = handler.ReadAllSelectableQuests(profile).get(0);

		  System.out.println("\n---------Testing Reward-----------------");
		  reward = handler.ReadReward(currentQuest);

		  System.out.println("Reward: " + reward.getTitle() );
		  System.out.println("Selectable: " + selectableQ.getTitle());
		  
		  System.out.println("---------Testing Reward ArrayList-------------");	  
		  ArrayList<Reward> rewards = handler.ReadAllFinishedRewards(profile);
		  System.out.println(rewards.get(0).getTitle());
		  
		  DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy/ HH:mm:ss");
		  Date date = new Date();
		  System.out.println(dateFormat.format(date));
		  String i = dateFormat.format(date);
		  
		  ArrayList<Quest> readallquests = handler.ReadAllQuests();
		  
		  //For Reading all Quests all Packages 
		  for(int a = 0; a < readallquests.size();a++)
			  System.out.println("Quest: " + a + " "+readallquests.get(a).getTitle());
		  
		  readallquests = handler.SortQuestList(readallquests);
		  
		  //For Reading all Quests all Packages 
		  for(int a = 0; a < readallquests.size();a++)
			  System.out.println("Quest: " + a + " "+readallquests.get(a).getTitle());		  
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
