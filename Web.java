import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;

import org.json.JSONException;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
//isp5247059biz


public class Web {
	static RamFile FileMap=null;
	 String srcFolder=null;
	 long MaxMemory=1024*2; //Mb
	
	static HashMap<String, String> ContentTypes=null;
	
	Web(String src,int port) throws IOException{
		System.out.println("Initializing Web Services. Mapping file system resources:");
		try {
			FileMap=new RamFile(src,MaxMemory); //source folder name, memory in MB to allocate in available JVM Ram for files.
			
		} catch (IOException e) {
			System.err.println("Source folder not found");
			e.printStackTrace();
		}finally {
			
		}
		srcFolder=src;
		
		System.out.println("Initializing web interface...");
		ContentTypes = new HashMap<String, String>();
		
		ContentTypes.put(".json", "application/json;charset=UTF-8");
		ContentTypes.put(".css","text/css");
		ContentTypes.put(".js","application/javascript;charset=UTF-8");
		ContentTypes.put(".ico","image/x-icon");
		ContentTypes.put(".jpeg","image/jpeg");
		ContentTypes.put(".jpg","image/jpeg");
		ContentTypes.put(".png", "image/png");
		ContentTypes.put(".txt","text/plain;charset=UTF-8");
		ContentTypes.put(".html","text/html;charset=UTF-8");                                      
		ContentTypes.put(".xml","application/xhtml+xml");
		
		System.setProperty("java.net.preferIPv4Stack" , "true");
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
	       System.out.println("Attempting web server.start() method after configuration is done");
        server.createContext("/", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Web Services: Done, Server listens to "+server.getAddress().toString().replace("/",""));
		
		
	}
	
	public void reload() throws IOException {
		FileMap=new RamFile(srcFolder,MaxMemory);
		
	}
	 static class MyHandler implements HttpHandler {
	        @Override
	        public void handle(HttpExchange t) throws IOException {
	        	byte[] out=null; 
	        	String uri=t.getRequestURI().toString();
	            OutputStream os = t.getResponseBody();  
	            switch (t.getRequestMethod()) {
	            
	            case "GET":
	            	
	            		switch(uri) {
	            		
	            			case "/":
	            				
	            				t.getResponseHeaders().add("Content-Type", ContentTypes.get(".html"));
	            				System.out.println("LOOKING FOR INDEX");
	            				out=FileMap.files.get("/index.html");
	            				System.out.println(t.getRemoteAddress()+" Connected "+t.getRequestURI().toString());
	            				t.sendResponseHeaders(200, out.length);	//ok
	            				break;
	            				
	            			case "favicon.ico":
	            				out=FileMap.files.get("/favicon.ico");
	            				t.getResponseHeaders().add("Content-Type", ContentTypes.get(".ico"));
	            				System.out.println(t.getRemoteAddress()+" 200 GET /favicon.ico");
	            				t.sendResponseHeaders(200, out.length);	//ok
	            				break;
	            				
	            			default:
	        
	            				String fileSuffix=uri.substring(uri.lastIndexOf("."), uri.length());
	            				if(ContentTypes.containsKey(fileSuffix)) {
	            					if(FileMap.files.containsKey(uri)){
	            						/////////////////////////////////////////////////////////////////////////////
	            						System.out.println(t.getRemoteAddress()+" 200 GET "+t.getRequestURI().toString());
	            						/////////////////////////////////////////////////////////////////////////////
	            						t.getResponseHeaders().add("Content-Type", ContentTypes.get(fileSuffix));
	            						out=FileMap.files.get(uri);
	            						t.sendResponseHeaders(200, out.length);	//ok
	            					}else {		
	            						/////////////////////////////////////////////////////////////////////////////
	            						System.out.println(t.getRemoteAddress()+" 404 GET "+t.getRequestURI().toString());
	            						/////////////////////////////////////////////////////////////////////////////
	            						out="Not Found:   404.".getBytes();
	            						t.sendResponseHeaders(404, out.length);	 //not found
	            					}
	            				}else {
	            					/////////////////////////////////////////////////////////////////////////////
	            					System.out.println(t.getRemoteAddress()+" GET "+t.getRequestURI().toString());
	            					/////////////////////////////////////////////////////////////////////////////
	            					String u="415: Unsupported Media Type Suffix '"+fileSuffix+"'";
	            					out=u.getBytes();
	            					t.sendResponseHeaders(415, out.length);// Unsupported Media Type	
	            				}
	            				break;
	            		}
	            		
	            		os.write(out);
	            		os.close();	
	            		break;
	            	
	            case "POST":	
	            	System.out.println("----------------------------------------------------");
	            	System.out.println(t.getRemoteAddress()+" POST "+t.getRequestURI().toString());
	            	System.out.println("^^^^^^^^^^^^^^ RequestBody: ");//+t.getRequestBody().read());
	            	byte[] body=t.getRequestBody().readAllBytes();
	            	String ou=new String(body);
	            	System.out.println(ou);
	            	System.out.println("----------------------------------------------------");
	            	break;
	            	
	            default:
	            	System.out.println(t.getRemoteAddress()+" "+t.getRequestMethod()+" "+t.getRequestURI().toString());
	            	String err="Request Method \""+t.getRequestMethod()+"\" is Unsupported";
	            	t.sendResponseHeaders(405, err.length());
	            	os.write(err.getBytes());
	            	os.close();
	            	break;
	            }

	        } 
	 }

}