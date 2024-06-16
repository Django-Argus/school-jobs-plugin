package net.argus.school.plugin.jobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.argus.school.api.event.SchoolEvent;
import net.argus.school.api.event.SchoolListener;
import net.argus.school.api.event.SchoolResetEntry;

public class JobsSchoolListener implements SchoolListener {
	
	public static final String RM_JOBS = "rm-jobs";

	@Override
	public List<SchoolResetEntry> resetRequested(SchoolEvent e) {
		List<SchoolResetEntry> entries = new ArrayList<SchoolResetEntry>();
		
		entries.add(new SchoolResetEntry(RM_JOBS, "Remove Jobs", "delete all jobs from the database"));
		
		return entries;
	}
	
	@Override
	public void reset(SchoolEvent e) {
		switch(e.getSchoolEntryId()) {
			case RM_JOBS:
				while(Jobs.ID_REGISTER.getIds().size() != 0) {
					int id = Jobs.ID_REGISTER.getIds().get(0);
					try {
						Jobs.removeJob(id);
						Attribution.clearAll();
						Attribution.removeJob(id);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				break;
		}
	}

}
