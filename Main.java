import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class Main {
	public static void main(String[] args) throws NoSuchAlgorithmException, SQLException, IOException, JSONException {

		
	//	System.out.printf("%.2f",123.234);
		Web w=new Web("Test",8020);
		BufferedReader br = null;
		 br = new BufferedReader(new InputStreamReader(System.in));
		
		do {
			String c=br.readLine();
			//System.out.println(c);
			if(c.equals("3")) {
				System.out.println("Reloading Source Folder...");
				w.reload();
				System.out.println("Done.");
			}
		}while(true);

	}
	public static int[] getTimeFormat(int input) {
		int Days = input / 86400;
		int Hours = (input % 86400 ) / 3600 ;
		int Minutes = ((input % 86400 ) % 3600 ) / 60 ;
		int Seconds = ((input % 86400 ) % 3600 ) % 60  ;
		
		int[] result={Seconds,Minutes,Hours,Days};
		return result;
	}
}
