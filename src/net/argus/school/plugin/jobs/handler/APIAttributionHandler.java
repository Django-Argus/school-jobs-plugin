package net.argus.school.plugin.jobs.handler;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import com.sun.net.httpserver.HttpExchange;

import net.argus.cjson.CJSON;
import net.argus.cjson.value.CJSONArray;
import net.argus.school.api.Students;
import net.argus.school.api.handler.pack.SchoolPackagePrefab;
import net.argus.school.plugin.jobs.Attribution;
import net.argus.school.plugin.jobs.handler.pack.PackagePrefabJobs;
import net.argus.web.http.pack.PackagePrefab;

public class APIAttributionHandler extends JobsAPIHandler {

	public APIAttributionHandler() {
		super("attribution");
	}

	@Override
	public void doGet(HttpExchange exchange) throws IOException {
		send(exchange, PackagePrefab.get405Package());
	}

	@Override
	public void doPost(HttpExchange exchange) throws IOException {
		CJSON parameters = getCJSONParameters(exchange);
		try {

		switch(parameters.getString("action").toLowerCase()) {	
			case "get":
				int id = parameters.getInt("id");
				int[] att = Attribution.getAttributions(id);

				if(att.length >= 0)
					send(exchange, PackagePrefabJobs.getAttributionsPackage(att));
				else
					send500(exchange);
				
				break;
				
			case "add":
				id = parameters.getInt("id");
				int uid = parameters.getInt("user_id");
				
				if(Attribution.addStudent(id, uid))
					send(exchange, SchoolPackagePrefab.getStudentPackage(Students.getStudent(uid).getString("name"), uid));		
				else
					send500(exchange);
				
				break;
				
			case "update_capability":
				id = parameters.getInt("id");
				int cap = parameters.getInt("capability");
				
				if(cap <= 0) {
					send500(exchange);
					return;
				}
				
				Attribution.clearAll();

				if(Attribution.updateCapability(id, cap))
					sendEmptyPackage(exchange);
				else
					send500(exchange);
				
				break;
				
			case "list_students":
				CJSONArray arr = Attribution.getStudentsList();
				
				send(exchange, PackagePrefabJobs.getStudentAttributionPackage(arr));
				break;
				
			case "list_random_students":
				arr = Attribution.getRandomStudentsList();
				
				send(exchange, PackagePrefabJobs.getStudentAttributionPackage(arr));
				break;
				
			case "clear_attributions":
				Attribution.clearAttributions();
					
				sendEmptyPackage(exchange);
				break;
				
			case "clear_all":
				Attribution.clearAll();
					
				sendEmptyPackage(exchange);
				break;
				
			case "remove":
				id = parameters.getInt("id");
				uid = parameters.getInt("user_id");
				
				if(Attribution.removeStudent(id, uid))
					sendEmptyPackage(exchange);
				else
					send500(exchange);
				
				break;
				
			case "randomize":
				
				List<Entry<Integer, Integer>> result = Attribution.genRandomAttribution();

				if(result == null) {
					sendEmptyPackage(exchange);
					break;
				}
				
				send(exchange, PackagePrefabJobs.getRandomizePackage(result));
				break;
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
