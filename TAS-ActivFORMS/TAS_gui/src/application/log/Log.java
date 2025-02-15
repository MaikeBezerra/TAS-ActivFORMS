package application.log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Log {
	
	public static ObservableList<LogEntry> logData = FXCollections.observableArrayList();
	private static PrintWriter out; 
	private static String logFile;
	
	public static void initialize(String file){
		try {
			logFile = file;
			out = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)));
			readFromFile(logFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void stop(){
		out.close();
	}
	
	public static void addLog(String title,String message){
		SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();  
        String time = dataFormat.format(date).toString();

		logData.add(new LogEntry(time, title, message));
		out.println(time + "," + title + "," + message);
	}
	
	@SuppressWarnings("resource")
	public static void clear(){
		try{
			File file = new File(logFile);
			FileLock lock = new RandomAccessFile(file, "rw").getChannel().lock();

			if(lock != null){
				System.out.println("file locked");
				file.delete();
				file.createNewFile();
				lock.release();
				System.out.println("file released");
				logData.clear();
			}
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static void readFromFile(String logFile){
		try{
			File file = new File(logFile);
			if(!file.exists()) {
				file.createNewFile();
			} 
			
			BufferedReader br = new BufferedReader(new FileReader(logFile));
			String line;
	        while ((line = br.readLine()) != null) {
				String[] strs=line.split(",");
				if(strs.length == 3){
					logData.add(new LogEntry(strs[0], strs[1], strs[2]));
				}
			}
			br.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("read from file " + logFile);
	}
	
}
