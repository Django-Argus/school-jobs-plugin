package net.argus.school.plugin.jobs.handler;

import net.argus.instance.Instance;
import net.argus.web.http.api.formdata.APIFormDataHandler;

public class APIUploadJobHandler extends APIFormDataHandler {

	public APIUploadJobHandler() {
		super("jobs/upload/job", Instance.SYSTEM.getRootPath() + "/www/images/job/");
	}

}
