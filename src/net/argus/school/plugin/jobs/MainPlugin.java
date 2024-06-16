package net.argus.school.plugin.jobs;

import net.argus.instance.Instance;
import net.argus.plugin.Plugin;
import net.argus.plugin.PluginEvent;
import net.argus.plugin.annotation.PluginInfo;
import net.argus.school.api.MainAPI;
import net.argus.school.plugin.jobs.handler.APIAttributionHandler;
import net.argus.school.plugin.jobs.handler.APIJobsHandler;
import net.argus.school.plugin.jobs.handler.APIUploadJobHandler;
import net.argus.web.http.APIServer;
import net.argus.web.http.FileHandler;

@PluginInfo(authors = {MainPlugin.AUTHORS}, description = MainPlugin.DESCRIPTION, name = MainPlugin.NAME,
pluginId = MainPlugin.PLUGIN_ID, pluginRequested = {MainPlugin.REQUESTED}, version = MainPlugin.VERSION)
public class MainPlugin extends Plugin {
	
	public static final String AUTHORS = "Argus";
	public static final String DESCRIPTION = "Jobs plugin for SchoolAPI.";
	public static final String NAME = "Jobs";
	public static final String PLUGIN_ID = "jobs";
	public static final String REQUESTED = "";
	public static final String VERSION = "1.2.0";
	
	@Override
	public void preInit(PluginEvent e) {
		Jobs.init();
		Attribution.init();
	}
	
	@Override
	public void init(PluginEvent e) {
		MainAPI.addSchoolListener(new JobsSchoolListener());
		
		APIServer srv = (APIServer) e.getParent();
		
		srv.addHandle(new APIJobsHandler());
		srv.addHandle(new APIUploadJobHandler());
		srv.addHandle(new APIAttributionHandler());
		
		srv.addHandle(new FileHandler("/school/" + PLUGIN_ID +"/", Instance.currentInstance().getRootPath() + "/www/"));
	}

	@Override
	public void postInit(PluginEvent e) {}
	
}
