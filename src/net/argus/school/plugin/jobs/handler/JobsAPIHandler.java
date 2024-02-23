package net.argus.school.plugin.jobs.handler;

import net.argus.web.http.APIHandler;

public abstract class JobsAPIHandler extends APIHandler {

	public JobsAPIHandler(String name) {
		super("jobs/" + name);
	}

}
