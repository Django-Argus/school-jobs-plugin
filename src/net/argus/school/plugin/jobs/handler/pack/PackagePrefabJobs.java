package net.argus.school.plugin.jobs.handler.pack;

import java.io.IOException;

import net.argus.cjson.value.CJSONArray;
import net.argus.cjson.value.CJSONInteger;
import net.argus.cjson.value.CJSONObject;
import net.argus.cjson.value.CJSONString;
import net.argus.cjson.value.CJSONValue;
import net.argus.school.api.Students;
import net.argus.school.api.http.pack.APIPackage;
import net.argus.school.api.http.pack.PackageBuilder;
import net.argus.school.plugin.jobs.Job;
import net.argus.school.plugin.jobs.Jobs;

public class PackagePrefabJobs {
	
	public static APIPackage getJobsPackage() {
		CJSONObject obj = new CJSONObject();
		
		obj.addItem("jobs", Jobs.JOBS.getValue("jobs"));
		return PackageBuilder.getSucessPackage(obj);
	}
	
	public static APIPackage getStudentAttributionPackage(CJSONArray array) {
		if(array == null)
			return null;
		
		CJSONObject obj = new CJSONObject();
		
		obj.addItem("students", array);
		return PackageBuilder.getSucessPackage(obj);
	}
	
	public static APIPackage getJobPackage(Job job) {
		if(job == null)
			return null;
		
		CJSONObject obj = new CJSONObject();
		
		obj.addItem("name", new CJSONString(job.getName()));
		obj.addItem("capability", new CJSONInteger(job.getCapability()));
		return PackageBuilder.getSucessPackage(obj);
	}
	
	public static APIPackage getAttributionsPackage(int[] ids) {
		if(ids == null)
			return null;
		
		CJSONObject obj = new CJSONObject();
		
		CJSONValue[] array = new CJSONValue[ids.length];
		
		for(int i = 0; i < array.length; i++)
			array[i] = new CJSONInteger(ids[i]);
		
		obj.addItem("attributions", new CJSONArray(array));
		return PackageBuilder.getSucessPackage(obj);
	}
	
	public static APIPackage getRandomPackage(int[] uids) throws IOException {
		if(uids == null)
			return null;
		
		CJSONObject obj = new CJSONObject();
		
		CJSONValue[] array = new CJSONValue[uids.length];
		
		for(int i = 0; i < array.length; i++) {
			CJSONObject o = new CJSONObject();
			o.addItem("name", new CJSONString(Students.getStudent(uids[i]).getString("name")));
			o.addItem("id", new CJSONInteger(uids[i]));
			
			array[i] = o;
		}
		
		obj.addItem("capability", new CJSONInteger(uids.length));
		obj.addItem("random", new CJSONArray(array));
		return PackageBuilder.getSucessPackage(obj);
	}

}
