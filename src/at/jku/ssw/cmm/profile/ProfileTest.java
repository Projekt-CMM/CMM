package at.jku.ssw.cmm.profile;

import java.util.List;

public class ProfileTest {

	public static void main(String[] args) {
		//Profile profile = new Profile();
		Quest quest = new Quest();
	
		System.out.println(Quest.ReadFileNames("profileTest"));
		System.out.println(Quest.ReadFolderNames("packages"));
		
		
		Profile profile = new Profile();
		try {
			profile = Profile.ReadProfile("profileTest", "packages");
		} catch (XMLReadingException e) {
			e.printStackTrace();
		}
		List<Quest> qlist = Profile.ReadPackageQuests(profile, "packages", "default").getQuestList();
		
		//System.out.println(profile.getXp() + " " +  profile.getProfileQuests().get(0).getTitle() + " " + profile.getProfileQuests().get(0).getPackagePath() + " " +profile.getProfileQuests().get(0).getStringDate());
		
		if(qlist != null)
		for(Quest x : qlist)
			System.out.println(x.getState());
		//Profile.ReadPackageQuests(profile, allPackagesPath, packagePath)
		
		profile.setName("testname");
		profile.setXp(200);
		profile.setInitPath("profileTest");
		
		
		try {
			Profile.writeProfile(profile);
			profile = Profile.changeQuestState(profile, qlist.get(0), Quest.STATE_INPROGRESS);
		} catch (XMLWriteException e) {
			e.printStackTrace();
		}
		
		for(Quest q: profile.getProfileQuests())
			System.out.println(q.getStringDate());
	}

}
