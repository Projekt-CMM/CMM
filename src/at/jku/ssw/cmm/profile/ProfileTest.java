/*
 *  This file is part of C-Compact.
 *
 *  C-Compact is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  C-Compact is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with C-Compact. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright (c) 2014-2015 Fabian Hummer
 *  Copyright (c) 2014-2015 Thomas Pointhuber
 *  Copyright (c) 2014-2015 Peter Wassermair
 */
 
package at.jku.ssw.cmm.profile;

import java.util.List;

public class ProfileTest {

	public static void main(String[] args) {
	
		System.out.println(Quest.ReadFileNames("profileTest"));
		System.out.println(Quest.ReadFolderNames("packages"));
		
		
		Profile profile = new Profile();
		try {
			profile = Profile.ReadProfile("profileTest", "packages");
		} catch (XMLReadingException | ProfileNotFoundException e) {
			e.printStackTrace();
		}
		List<Quest> qlist = Profile.ReadPackageQuests(profile, "packages", "1 Erste Schritte").getQuestList();
		
		//System.out.println(profile.getXp() + " " +  profile.getProfileQuests().get(0).getTitle() + " " + profile.getProfileQuests().get(0).getPackagePath() + " " +profile.getProfileQuests().get(0).getStringDate());
		
		if(qlist != null)
			for(Quest x : qlist)
				System.out.println(x.getState());
		//Profile.ReadPackageQuests(profile, allPackagesPath, packagePath)
		
		profile.setName("testname");
		profile.setInitPath("profileTest");
		
		if(qlist != null) {
			System.out.println("Quest: " + qlist.get(0).getTitle());
			try {
				profile.writeProfile();
				profile = profile.changeQuestState(qlist.get(0), Quest.STATE_FINISHED);
			} catch (XMLWriteException e) {
				e.printStackTrace();
			}
		}
		
		for(Quest q: profile.getProfileQuests())
			System.out.println(q.getStringDate());
	}

}
