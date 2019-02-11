

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;



public class ExchangeHandler {
	public HashMap<String, String> map =null;
	private HttpExchange t=null;
	byte[] bodybuffer=null;
	String[] arg=null;
	static HashMap<String,HashMap<String,String>> results=null;
	ExchangeHandler(HttpExchange h) throws IOException{ //for parsing jsons from a post request
		t=h;
		bodybuffer=t.getRequestBody().readAllBytes();
		String c=new String(t.getRequestBody().readAllBytes(), "UTF-8");
		System.out.println(c+"------------------");	
		arg=new String(bodybuffer, "UTF-8").split("&");
		map = new HashMap<String, String>();
		System.out.println(arg.length + " <---->"+arg[0]);
		for(int i=0;i<arg.length;i++) {
			String[] cell=arg[i].split("=");
			if(cell.length==2) {
				map.put(cell[0], cell[1]);
			}else{
				System.err.println("Ignoring key set...");
			}
		}
	}
	public String get(String key) {
		return map.get(key);
	}
	
	public static String[] getKeys(JSONObject j) {	
		return JSONObject.getNames(j).clone();
	}
	
	public static JSONObject toJSON(String str) throws JSONException {
		return new JSONObject(str);
	}
	 
	public String getJsonString(String[] keys,String[] values) throws JSONException {
		if(keys.length != values.length) {
			throw new RuntimeException("keys' length is unequal to values' length that were attempted to be writen into a json");
		}else {
			JSONObject response=new JSONObject();
			for(int i=0;i<keys.length;i++) {
				if(keys[i].isEmpty()) {
					throw new RuntimeException("key index: "+i+" is null.");
				}
				else {
					response.append(keys[i], values[i]);
				}
			}
			return response.toString();
		}
	}
	
	public static byte[] getJsonBytes(ArrayList<HashMap<String,String>> m) throws JSONException {
		if(m==null) {
			return new JSONObject("{found:null}").toString().getBytes();
		}		
		JSONArray response=new JSONArray();
		for ( int i = 0; i < m.size(); i++ ) {	
			JSONObject C=new JSONObject();
			HashMap<String,String> result=m.get(i);
			
			for(HashMap.Entry<String,String> submap:result.entrySet()) {
				C.put(submap.getKey(), submap.getValue());
			}
			
			response.put(i, C);	
		}
		
		return response.toString().getBytes();
	}
	
	
	public static byte[] getJsonBytes(HashMap<String,HashMap<String,String>> m) throws JSONException {
		if(m==null) {
			return new JSONObject("{found:null}").toString().getBytes();
		}
		results=m;		
		JSONArray response=new JSONArray();
		for (HashMap.Entry<String,HashMap<String,String>> entry : results.entrySet()) {
			
			JSONObject C=new JSONObject();
			HashMap<String,String> result=entry.getValue();
			for(HashMap.Entry<String,String> submap:result.entrySet()) {
				C.put(submap.getKey(), submap.getValue());
			}
			response.put(C);
		}
		
		return response.toString().getBytes();
	}
	
	public static byte[] getJsonBytes(String[] keys,String[] values) throws JSONException {
		if(keys.length != values.length) {
			throw new RuntimeException("keys' length is unequal to values' length that were attempted to be writen into a json");
		}else {
			JSONObject response=new JSONObject();
			for(int i=0;i<keys.length;i++) {
				if(keys[i].equals(null)) {
					throw new RuntimeException("key index: "+i+" is null.");
				}
				else {
					response.append(keys[i], values[i]);	
				}	
			}
			return response.toString().getBytes();	
		}
	}
}
