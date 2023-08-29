package net.argus.school.plugin.jobs;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
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

	public static void addJob(int id, int capability) throws IOException {
		CJSONObject obj = new CJSONObject();
		
		obj.addItem("capability", new CJSONInteger(capability));
		obj.addItem("attributions", new CJSONArray());
		obj.addItem("logs", new CJSONArray());
		getMainObject().addItem(Integer.toString(id), obj);
		
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
		
		if(isFull(id, uid))
			clearLog(uid);
		
		CJSONObject obj = getMainObject().getObject(Integer.toString(id));
		
		Array arr = obj.getArray("attributions");
		arr.addValue(new CJSONInteger(uid));
		
		Array logs = obj.getArray("logs");
		int i = 0;
		for(CJSONValue val : logs.getArray())
			if(val.getInt() == uid)
				i++;

		
		if(i < cap)
			logs.addValue(new CJSONInteger(uid));
		
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
			Array attributions = job.getValue().getArray("attributions");
			
			attributions.getArray().clear();
		}
		writeFile();
	}
	
	public static boolean isFull(int exceptId, int uid) {
		List<CJSONItem> jobs = getMainObject().getValue();

		for(CJSONItem job : jobs) {
			
			int cap = getCapability(Integer.valueOf(job.getName()));
			Array logs = job.getValue().getArray("logs");
			
			int i = 0;
			for(CJSONValue val : logs.getArray())
				if(val.getInt() == uid)
					i++;
			
			if(job.getName().equals(Integer.toString(exceptId)))
				i++;
			
			if(i < cap)
				return false;
					
		}
		return true;
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
	
	public static int[] getRandomUIDForJob(int id, int cap) throws IOException {
		if(getObject(id) == null)
			return null;

		List<CJSONValue> val = Students.STUDENTS.getMainObject().getArray("students").getArray();
		if(val.size() == 0)
			return null;
		
		if(cap > val.size()  - countStudentAssigned())
			cap = val.size() - countStudentAssigned();
		
		if(getAttributions(id).length >= cap)
			cap = 0;

		int rand = 0;
		int[] uids = new int[cap];
		for(int i = 0; i < cap; i++) {
			int uid = 0;
			do {
				rand = new Random().nextInt(val.size());
				uid = val.get(rand).getInt("id");
				if(!isAlreadyUse(uid) && isAleardyDo(id, uid) && cap == 1) {
					Debug.log("Student with id " + uid + " was exceptionally authorised");
					break;
				}
			}while(isAlreadyUse(uid) || isAleardyDo(id, uid) || contentUid(uids, uid));
			uids[i] = uid;
		}
		
		return uids;
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
	
	private static boolean contentUid(int[] uids, int uid) {
		for(int u : uids)
			if(u == uid)
				return true;
		
		return false;
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
