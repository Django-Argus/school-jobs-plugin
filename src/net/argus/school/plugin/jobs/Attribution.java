package net.argus.school.plugin.jobs;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import net.argus.cjson.Array;
import net.argus.cjson.CJSON;
import net.argus.cjson.CJSONItem;
import net.argus.cjson.CJSONParser;
import net.argus.cjson.value.CJSONArray;
import net.argus.cjson.value.CJSONInteger;
import net.argus.cjson.value.CJSONObject;
import net.argus.cjson.value.CJSONValue;
import net.argus.file.CJSONFile;
import net.argus.instance.Instance;
import net.argus.school.api.Students;
import net.argus.util.debug.Debug;

public class Attribution {
	
	public static final CJSONFile FILE = new CJSONFile("attribution", "", Instance.currentInstance());
	public static final CJSON ATTRIBUTION = CJSONParser.getCJSON(FILE);

	
	public static CJSONObject getMainObject() {
		return ATTRIBUTION.getMainObject();
	}
	
	public static int[] getAttributions(int id) throws IOException {
		CJSONObject obj = Attribution.getObject(id);
		if(obj == null)
			return null;
		
		List<CJSONValue> attrs = obj.getArray("attributions").getArray();
		int[] att = new int[attrs.size()];
		
		for(int i = 0; i < attrs.size(); i++)
			att[i] = attrs.get(i).getInt();
		
		return att;
	}
	
	public static int[] getLogs(int id) throws IOException {
		CJSONObject obj = Attribution.getObject(id);
		if(obj == null)
			return null;
		
		List<CJSONValue> attrs = obj.getArray("logs").getArray();
		int[] att = new int[attrs.size()];
		
		for(int i = 0; i < attrs.size(); i++)
			att[i] = attrs.get(i).getInt();
		
		return att;
	}

	public static void addJob(int id, int capability) throws IOException {
		CJSONObject obj = new CJSONObject();
		
		obj.addItem("capability", new CJSONInteger(capability));
		obj.addItem("attributions", new CJSONArray());
		getMainObject().addItem(Integer.toString(id), obj);
		
		writeFile();
	}
	
	public static void logOffset(int offset) throws IOException {
		if(getMainObject().getValue("logs") == null) {
			List<CJSONValue> intgs = new ArrayList<CJSONValue>();
			
			intgs.add(new CJSONInteger(offset));
			
			CJSONArray obj = new CJSONArray(intgs);
			getMainObject().addItem("logs", obj);
		}else {
			CJSONObject obj = getMainObject();
			
			Array arr = obj.getArray("logs");
			arr.addValue(new CJSONInteger(offset));
		}
		writeFile();
	}
	
	public static void clearOffset() throws IOException {
		Array logs = getMainObject().getArray("logs");
		logs.getArray().clear();
		writeFile();
	}
	
	public static boolean addStudent(int id, int uid) throws IOException {
		int[] att = getAttributions(id);
		
		int cap = getCapability(id);
		if(att.length >= cap)
			return false;
		
		for(int i : att)
			if(i == uid)
				return false;
		
		if(isAlreadyUse(uid))
			return false;
		
		CJSONObject obj = getMainObject().getObject(Integer.toString(id));
		
		Array arr = obj.getArray("attributions");
		arr.addValue(new CJSONInteger(uid));
		
		writeFile();
		return true;
	}
	
	public synchronized static boolean updateCapability(int id, int nCap) throws IOException {
		CJSONObject obj = Attribution.getObject(id);
		if(obj == null)
			return false;
		
		List<CJSONItem> items = obj.getValue();
		for(int i = 0; i < items.size(); i++) {
			if(items.get(i).getName().equals("capability")) {
				items.set(i, new CJSONItem("capability", new CJSONInteger(nCap)));		
				writeFile();
				return true;
			}
		}
		return false;
	}
	
	public static void clearLog(int uid) throws IOException {
		List<CJSONItem> jobs = getMainObject().getValue();
		for(CJSONItem job : jobs) {
			if(job.getName().equals("logs"))
				continue;
			
			Array logs = job.getValue().getArray("logs");
			for(int i = 0; i < logs.length(); i++)
				if(logs.get(i).getInt() == uid)
					logs.getArray().remove(i);
		}
		writeFile();
	}
	
	public static void clearAll() throws IOException {
		List<CJSONItem> jobs = getMainObject().getValue();
		for(CJSONItem job : jobs) {
			if(job.getName().equals("logs"))
				continue;
			Array logs = job.getValue().getArray("logs");
			Array attributions = job.getValue().getArray("attributions");
			
			logs.getArray().clear();
			attributions.getArray().clear();
		}
		writeFile();
	}
	
	public static void clearLogs() throws IOException {
		List<CJSONItem> jobs = getMainObject().getValue();
		for(CJSONItem job : jobs) {
			Array logs = job.getValue().getArray("logs");
			
			logs.getArray().clear();
		}
		writeFile();
	}
	
	public static void clearAttributions() throws IOException {
		List<CJSONItem> jobs = getMainObject().getValue();
		for(CJSONItem job : jobs) {
			if(job.getName().equals("logs"))
				continue;
			Array attributions = job.getValue().getArray("attributions");
			
			attributions.getArray().clear();
		}
		writeFile();
	}
	
	
	public static boolean isAleardyDo(int id, int uid) {
		List<CJSONItem> jobs = getMainObject().getValue();
		
		int cap = getCapability(id);
		
		for(CJSONItem job : jobs) {
			if(!job.getName().equals(Integer.toString(id)))
				continue;
			
			Array logs = job.getValue().getArray("logs");
			
			int i = 0;
			for(CJSONValue val : logs.getArray())
				if(val.getInt() == uid)
					i++;
			if(i < cap)
				return false;
			else
				return true;
					
		}
		return false;
	}
	
	public static boolean isAlreadyUse(int uid) {
		List<CJSONItem> jobs = getMainObject().getValue();
		for(CJSONItem job : jobs) {
			if(job.getName().equals("logs"))
				continue;
			
			Array logs = job.getValue().getArray("attributions");
			for(CJSONValue val : logs.getArray())
				if(val.getInt() == uid)
					return true;
			
		}
		return false;
	}
	
	public static CJSONArray getStudentsList() {
		Array students = Students.STUDENTS.getArray("students");
		List<CJSONValue> vals = new ArrayList<CJSONValue>();
		
		for(CJSONValue val : students.getArray()) {
			int id = val.getInt("id");
			if(isAlreadyUse(id))
				continue;
			vals.add(val);
		}
		
		return new CJSONArray(vals);
	}
	
	public static CJSONArray getRandomStudentsList() {
		List<CJSONValue> students = new ArrayList<CJSONValue>();
		for(CJSONValue val :  Students.STUDENTS.getArray("students").getArray())
			students.add(val);
		
		List<CJSONValue> vals = new ArrayList<CJSONValue>();
		int size = students.size();
				
		while(vals.size() != size) {
			int rand = new Random().nextInt(students.size());
			vals.add(students.get(rand));
			students.remove(rand);
		}
		
		return new CJSONArray(vals);
	}
	
	public static int countStudentAssigned() {
		List<CJSONItem> jobs = getMainObject().getValue();
		int c = 0;
		for(CJSONItem job : jobs) {
			Array attributions = job.getValue().getArray("attributions");
			c += attributions.length();
		}
		return c;
	}
	
	public static List<CJSONValue> getStudentAvaliableForJob(int id) {
		List<CJSONValue> rets = new ArrayList<CJSONValue>();
		
		List<CJSONValue> black = new ArrayList<CJSONValue>();
		
		Array studs = Students.getStudentsArray().getArray();
		
		
		for(int i = indexJob(id); i < ATTRIBUTION.getMainObject().getValue().size(); i++) {
			CJSONItem val = ATTRIBUTION.getMainObject().getValue().get(i);
			
			CJSONObject obj = (CJSONObject) val.getValue();
			Array logs = obj.getArray("logs");
			int cap = getCapability(Integer.valueOf(val.getName()));
			
			if(Integer.valueOf(val.getName()) != id) {
				if(logs.length() >= studs.length() * cap - cap) {
					for(CJSONValue s : studs.getArray()) {
						if(!isAleardyDo(Integer.valueOf(val.getName()), s.getInt("id"))) {
							if(rets.contains(s))
								rets.remove(s);
							black.add(s);
						}
					}
				}
			}else {
				for(CJSONValue s2 : studs.getArray()) {
					if(!isAlreadyUse(s2.getInt("id")) && !isAleardyDo(Integer.valueOf(val.getName()), s2.getInt("id")) && !black.contains(s2))
						rets.add(s2);
				}
			}
		}
		
	
		if(rets.size() < getCapability(id)) {
			List<CJSONValue> last = new ArrayList<CJSONValue>();
			for(CJSONValue v : studs.getArray())
				if(!isAlreadyUse(v.getInt("id")))
					last.add(v);
			
			int[] uids = new int[getCapability(id)];
			int i = 0;
			do {
				int uid = 0;
				int rand = new Random().nextInt(last.size());
				if(!rets.contains(last.get(rand))) {
					uids[i] = last.get(rand).getInt("id");
					Debug.log("Student with id " + uid + " was exceptionally authorised");
					i++;
				}
				last.remove(rand);
				
				
				
				
			}while(uids.length != getCapability(id) || last.size() <= 0);
			
		}
		
		
		System.out.println();
		System.out.println(rets);
		System.out.println(black);
		
		return rets;
	}
	
	public static int getJobId(int index) {
		Array jobs = Jobs.getJOBSArray().getArray();
		int jIndex = 0;
		for(int i = 0; i < index; jIndex++) {
			CJSONObject attr = Attribution.getObject(jobs.get(jIndex).getInt("id"));
			int cap = attr.getInt("capability");
			if(i + cap <= index) {
				i += cap;
			}else 
				return jobs.get(jIndex).getInt("id");
		}
		
		return jobs.get(jIndex).getInt("id");
	}
	
	public static int getSizeJob() {
		CJSONObject attr = getMainObject();
		
		Array jobs = Jobs.getJOBSArray().getArray();

		int size = 0;
		for(int i = 0; i < jobs.length(); i++) {
			size += attr.getObject(Integer.toString(jobs.get(i).getInt("id"))).getInt("capability");
		}
		
		return size;
	}
	
	public static List<Entry<Integer, Integer>> genRandomAttribution() throws IOException {
		List<Entry<Integer, Integer>> list = new ArrayList<Entry<Integer,Integer>>();
		
		Array studs = Students.getStudentsArray().getArray();
		int sizeJob = getSizeJob();
		
		if(studs.length() != sizeJob)
			return null;
		
		int offset = genOffset(studs.length());
		for(int i = 0; i < studs.length(); i++) {
			AttributionEntry<Integer, Integer> ent = new AttributionEntry<Integer, Integer>();
			ent.setKey(getJobId((i + offset) % sizeJob));
			ent.setValue(studs.get(i).getInt("id"));
			list.add(ent);
		}
		
		logOffset(offset);

		return list;
	}
	
	public static int genOffset(int max) throws IOException {
		if(getMainObject().getValue("logs") == null)
			return new Random().nextInt(max);
		
		Array arr = getMainObject().getArray("logs");
		
		if(arr.length() == max) {
			clearOffset();
			return new Random().nextInt(max);
		}
		
		List<Integer> possible = new ArrayList<Integer>();
		
main:	for(int i = 0; i < max; i++) {
			for(int j = 0; j < arr.length(); j++) 
				if(arr.get(j).getInt() == i) 
					continue main;
			possible.add(i);
		}
		int offset = possible.get(new Random().nextInt(possible.size()));
		
		
		return offset;
	}
	
	public static int indexJob(int id) {
		List<CJSONItem> it = getMainObject().getValue();
		for(int i = 0; i < it.size(); i ++)
			if(Integer.valueOf(it.get(i).getName()) == id)
				return i;
		return -1;
	}
	
	public static CJSONObject getObject(int id) {
		CJSONObject obj = getMainObject();
		
		for(CJSONItem item : obj.getValue())
			if(item.getName().equals(Integer.toString(id)))
				return (CJSONObject) item.getValue();
		
		return null;
	}
	
	public static int getCapability(int id) {
		CJSONObject obj = getObject(id);
		if(obj == null)
			return -1;
		
		return obj.getInt("capability");
	}
	
	public synchronized static boolean removeJob(int id) throws IOException {
		CJSONObject mainObj = getMainObject();
		for(CJSONItem item : mainObj.getValue()) {
			if(item.getName().equals(Integer.toString(id))) {
				List<CJSONItem> it = mainObj.getValue();
				it.remove(item);
								
				writeFile();
				return true;
			}
		}
		return false;
	}
	
	public synchronized static boolean removeStudent(int id, int uid) throws IOException {
		CJSONObject obj = Attribution.getObject(id);
		if(obj == null)
			return false;
		
		List<CJSONValue> arr = obj.getArray("attributions").getArray();
		for(int i = 0; i < arr.size(); i++) {
			if(arr.get(i).getInt() == uid) {
				arr.remove(i);
				break;
			}
		}
		
		writeFile();
		return true;
	}
	
	private static void writeFile() throws IOException {
        FileOutputStream fos = new FileOutputStream(FILE.getFile());
        
        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
        
        osw.write(ATTRIBUTION.toString());
        
        osw.flush();
        osw.close();
	}
	
	public static void init() {}
	
}
