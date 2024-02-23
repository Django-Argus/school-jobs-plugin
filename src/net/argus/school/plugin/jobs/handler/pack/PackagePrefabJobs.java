package net.argus.school.plugin.jobs.handler.pack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import net.argus.cjson.value.CJSONArray;
import net.argus.cjson.value.CJSONInteger;
import net.argus.cjson.value.CJSONObject;
import net.argus.cjson.value.CJSONString;
import net.argus.cjson.value.CJSONValue;
import net.argus.school.api.Students;
import net.argus.school.plugin.jobs.Job;
import net.argus.school.plugin.jobs.Jobs;
import net.argus.web.http.pack.APIPackage;
import net.argus.web.http.pack.PackageBuilder;

public class PackagePrefabJobs {
	
	public static APIPackage getJobsPackage() {
		CJSONObject obj = new CJSONObject();
		
		obj.addItem("jobs", Jobs.JOBS.getValue("jobs"));
		return PackageBuilder.getSucessPackage(obj);
	}
	
	public static APIPackage getRandomJobsPackage() {
		CJSONObject obj = new CJSONObject();
		
		List<CJSONValue> vals = new ArrayList<CJSONValue>(Jobs.JOBS.getArray("jobs").getArray());
		List<CJSONValue> rand = new ArrayList<CJSONValue>();

		while(vals.size() > 0) {
			int r = new Random().nextInt(vals.size());
			rand.add(vals.get(r));
			vals.remove(r);
		}
		
		obj.addItem("jobs", new CJSONArray(rand));
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
	
	public static APIPackage getRandomizePackage(List<Entry<Integer, Integer>> set) {
		if(set == null)
			return null;
		
		CJSONObject obj = new CJSONObject();
		
		CJSONObject attr = new CJSONObject();
		
		List<Integer> used = new ArrayList<Integer>();
		int jId = 0;
		while(set.size() > 0) {
			List<CJSONValue> vals = new ArrayList<CJSONValue>();
			jId = set.get(0).getKey();
			if(!used.contains(jId)) {
				used.add(jId);
				
				for(int i = 0; i < set.size(); i++) {
					if(set.get(i).getKey() == jId) {
						vals.add(new CJSONInteger(set.get(i).getValue()));
					}
				}
				
				attr.addItem(Integer.toString(jId), new CJSONArray(vals));
			}
			set.remove(0);
		}
		
		obj.addItem("attributions", attr);
		return PackageBuilder.getSucessPackage(obj);
	}

}
