package application.utility.strings;

import java.io.File;

public class PathUtil {
	
	private static final String BASE_DIR = "";
	
	public static final String RESOURCES_DIR = BASE_DIR + "resources" + File.separator;
	public static final String WORKFLOW_PATH = RESOURCES_DIR + "TeleAssistanceWorkflow.txt";
	public static final String PROFILE_DIR_PATH = RESOURCES_DIR + "files" + File.separator;
	
	public static final String RESULT_DIR  = BASE_DIR + "results" + File.separator;
	public static final String RESULT_FILE_PATH = RESULT_DIR + "result.csv";
	public static final String RESULTS_FILE_PATH = RESULT_DIR + "results.csv";
	public static final String LOGFILE_PATH = RESULT_DIR + "log.csv";
	
}
