package at.jku.ssw.cmm.profile;

public class ProfileTest {

	public static void main(String[] args) {
		//Profile profile = new Profile();
		Quest quest = new Quest();
	
		System.out.println(Quest.ReadFileNames("profileTest"));
		System.out.println(Quest.ReadFolderNames("packages"));
		
		
		for(Quest x : Quest.ReadAllQuests("packages"))
			System.out.println(x.getState());
	}

}
