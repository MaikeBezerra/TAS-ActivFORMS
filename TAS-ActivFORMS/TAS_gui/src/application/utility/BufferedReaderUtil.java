package application.utility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class BufferedReaderUtil {

	public static BufferedReader createBufferedReader(String resultFilePath) {
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(resultFilePath));
			return br;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
