package at.jku.ssw.cmm.quests;

import at.jku.ssw.cmm.quests.datastructs.Quest;
import at.jku.ssw.cmm.quests.datastructs.Reward;
import at.jku.ssw.cmm.quests.file.Reader;


public class Start {

   public static void main(String[] args) {
	   Reader reader = new Reader("quests", "rewards");
	   reader.init();
	   
	   String path = reader.getFolderNames().get(0);
	   
	   Quest quest = reader.ReadAllQuests(path).get(0);
	   Reward reward = reader.ReadReward(path, quest.getRewardPath());
		
		System.out.println(reward.getImage());
		System.out.println("test");
		System.out.println(Reward.BACKGROUND);
		
   }
}