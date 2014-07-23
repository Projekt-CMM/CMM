package at.jku.ssw.cmm.quests;

import at.jku.ssw.cmm.quests.datastructs.Quest;
import at.jku.ssw.cmm.quests.datastructs.Reward;
import at.jku.ssw.cmm.quests.file.Reader;


public class Start {

   public static void main(String[] args) {
	   Reader reader = new Reader("quests", "rewards");
	   reader.init();
	   Quest quest = reader.getQuest(0);
	   Reward reward = reader.getReward(quest.getReward());
		
		System.out.println(reward.getImage());
		System.out.println("test");
		
   }
}