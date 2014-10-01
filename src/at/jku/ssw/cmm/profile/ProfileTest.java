package at.jku.ssw.cmm.profile;

import java.util.List;

public class ProfileTest {

	public static void main(String[] args) {
		//Profile profile = new Profile();
		Quest quest = new Quest();
	
		System.out.println(Quest.ReadFileNames("profileTest"));
		System.out.println(Quest.ReadFolderNames("packages"));
		
		
		for(Quest x : Quest.ReadAllQuests("packages"))
			System.out.println(x.getState());
		
		Profile profile = Profile.ReadProfile("profileTest", "packages");
		List<Quest> qlist = Profile.ReadPackageQuests(profile, "packages", "default");
		
		//System.out.println(profile.getXp() + " " +  profile.getProfileQuests().get(0).getTitle() + " " + profile.getProfileQuests().get(0).getPackagePath() + " " +profile.getProfileQuests().get(0).getStringDate());
		
		for(Quest x : qlist)
			System.out.println(x.getState());
		//Profile.ReadPackageQuests(profile, allPackagesPath, packagePath)
		
		Profile.writeProfile(profile, "profileTest");
		profile = Profile.changeQuestState(profile, qlist.get(0), Quest.STATE_INPROGRESS);
		
		for(Quest q: profile.getProfileQuests())
			System.out.println(q.getStringDate());
	}

}
