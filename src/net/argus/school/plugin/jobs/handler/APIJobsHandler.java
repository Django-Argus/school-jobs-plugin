package net.argus.school.plugin.jobs.handler;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import net.argus.cjson.CJSON;
import net.argus.school.api.http.APIHandler;
import net.argus.school.plugin.jobs.Attribution;
import net.argus.school.plugin.jobs.Job;
import net.argus.school.plugin.jobs.Jobs;
import net.argus.school.plugin.jobs.handler.pack.PackagePrefabJobs;

public class APIJobsHandler extends APIHandler {

	public APIJobsHandler() {
		super("jobs");
	}

	@Override
	public void doGet(HttpExchange exchange) throws IOException {
		send(exchange, PackagePrefabJobs.getJobsPackage());
	}

	@Override
	public void doPost(HttpExchange exchange) throws IOException {
		CJSON parameters = getCJSONParameters(exchange);
		
		switch(parameters.getString("action").toLowerCase()) {
			case "get":
				int id = parameters.getInt("id");
				Job job = Jobs.getJob(id);
				job.setCapability(Attribution.getCapability(id));
				send(exchange, PackagePrefabJobs.getJobPackage(job));
				break;
				
			case "add":
				Jobs.addJobs(parameters.getString("name"), parameters.getInt("capability"));
				sendEmptyPackage(exchange);
				break;
				
			case "update":
				id = parameters.getInt("id");
				String newName = parameters.getString("name");
				boolean success = Jobs.updateJobName(id, newName);
				
				if(success) sendEmptyPackage(exchange);
				else send500(exchange);
				break;
				
				
			case "remove":
				id = parameters.getInt("id");
				if(Jobs.removeMaterial(id))
					if(Attribution.removeJob(id)) {
						sendEmptyPackage(exchange);
						break;
					}
				send500(exchange);
				break;
		}
	}

}
