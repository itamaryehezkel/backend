import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.util.HashMap;



public class RamFile {
	
	HashMap<String, byte[]> files= new HashMap<String, byte[]>();
	private static String jarPath;
	private static String jarFolder;
	public static File src;
	Long freeRAM= Runtime.getRuntime().freeMemory();
	Long totalMemo = Runtime.getRuntime().totalMemory();
	Long maxMemo = Runtime.getRuntime().maxMemory();
	
	RamFile(String srcFolder,long mm) throws IOException{
		long maxmemory=mm*1024*1024;  //user allocated memory in MB
		if(maxmemory>maxMemo) {
			throw new RuntimeException("Requested volume of RAM exceeds the maximum available: "+getMemoFormat(freeRAM));
		}
		System.out.println("Initializing File Handling");
		src=new File(MainFolder()+"/"+srcFolder);
		
		if(src.exists()&&src.isDirectory()) {
			
			long foldervolume=getFolderSize(src);
			if(foldervolume>maxmemory) {
				throw new RuntimeException("Not enough Memory allocated for the source Folder.");
			}
		
			//	System.out.println("------Leaving a total of "+getMemoFormat(Runtime.getRuntime().freeMemory())+" of JVM Random Access Memory.");
			update(src,files);
			
			System.out.println("Volume of src folder("+src.getName()+"): "+getMemoFormat(foldervolume)+". Max JVM RAM: "+getMemoFormat(maxMemo));
			System.out.println("Leaving a total of "+getMemoFormat(maxMemo-foldervolume)+" of JVM Random Access Memory.");
			System.out.println("Source Folder: "+src.getAbsolutePath());
			
			
		}else {
			System.err.println("Invalid Folder Path: "+jarFolder+"/"+srcFolder);
			System.err.println("Creating new....");
			src.mkdir();
			System.out.println("New Source Folder created: '"+srcFolder+"'");
		}
	}
	private long getFolderSize(File folder) {
	    long length = 0;
	    File[] files = folder.listFiles();
	    int count = files.length;
	 
	    for (int i = 0; i < count; i++) {
	        if (files[i].isFile()) {
	            length += files[i].length();
	        }
	        else {
	            length += getFolderSize(files[i]);
	        }
	    }
	    return length;
	}
	

	public static void update(File file,HashMap<String,byte[]> hm) throws IOException {
		File[] subFiles=file.listFiles();
		for(int i=0;i<subFiles.length;i++) {
			InputStream inputstream;
			if(subFiles[i].isFile()) {
				String filepath=subFiles[i].getAbsolutePath();
				String key="/"+filepath.substring(src.getAbsolutePath().length()+1,filepath.length());
				System.out.println("File: "+key);
							
				try {
					inputstream = new FileInputStream(filepath);
					byte[] data      = inputstream.readAllBytes();
					inputstream.close();
					hm.put(key, data);
					} catch (FileNotFoundException e) {			
						e.printStackTrace();
					}
			}else if(subFiles[i].isDirectory()) {
				update(subFiles[i],hm);
			}
		}
	}
	
	
	
	
	public static String MainFolder() throws IOException{

		try {
			 File t= new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			 
			 jarPath=t.getAbsolutePath();
			 jarPath = jarPath.replace("\\", "/");
			 
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		jarFolder=jarPath.substring(0,jarPath.lastIndexOf("/"));	
		System.out.println("Jar Path = "+jarPath);

		return jarFolder;
	}
	
	public static String getMemoFormat(long input) {
		double result = 0;
		String unit="";
		if(input > 1073741824) { //1GB and above
			result = input/1073741824.00;
			unit = " GB";
		}
		if(input < 1073741824 && input >  1048576) { //1MB thru 1GB exclusive
			result = input/1048576.00;
			unit = " MB";
		}
		if(input < 1048576 && input > 1024){ //1KB thru 1MB exclusive
			result = input/1024.00;
			unit = " KB";
		}
		if(input < 1024 && input>=0) { //0KB thru 1KB exclusive(include 0)
			unit = " Bytes";
		}
		if(input < 0) {
			return "";
		}
		BigDecimal bd = new BigDecimal( result ) ;
		BigDecimal bdRounded = bd.setScale( 2 , RoundingMode.CEILING ) ;
		return bdRounded.toString()+unit;
	}
			
}
