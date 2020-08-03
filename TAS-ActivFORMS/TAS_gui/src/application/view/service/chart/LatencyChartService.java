package application.view.service.chart;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import application.utility.BufferedReaderUtil;
import javafx.scene.chart.XYChart;

public class LatencyChartService {
	
	public Map<String,XYChart.Series<String,Number>> 
				getDelays(String resultFilePath, int maxSteps) {
		
		BufferedReader br = 
				BufferedReaderUtil.createBufferedReader(resultFilePath);
		
		Map<String,XYChart.Series<String,Number>> delays
				= new LinkedHashMap<>();
		try {
			String line;
			String invocationNum;
			String invisible = new String();
			int tickUnit = maxSteps/20;
			
			while ((line = br.readLine()) != null) {
				String[] str = line.split(",");
			
				if (str.length == 6) {
					if(maxSteps >= 100 && 
							(Integer.parseInt(str[0]) % tickUnit != 0) && 
							(Integer.parseInt(str[0])!= maxSteps)){
						invisible += (char) 29;
						invocationNum = invisible;
					} else 
						invocationNum = str[0];
						
					String service = str[1];
					
					if(!service.equals("AssistanceService")){
						
						
						if(!delays.containsKey(service)){
							XYChart.Series<String,Number> delaySeries
									= new XYChart.Series<>();
							delaySeries.setName(service);
							delays.put(service, delaySeries);
						}
						
						Double delay = Double.parseDouble(str[5]);
						
						delays.get(service)
								.getData()
								.add(new XYChart.Data<String,Number>(invocationNum, delay));
						
						//XYChart.Series<String,Number> delaySeries = delays.get(service);						
						//delaySeries.getData().add(new XYChart.Data<String,Number>(invocationNum, delay));
					}
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return delays;
	}
	
	public List<String> getCategories(int width){
		
		List<String> categories = new ArrayList<>();
        String invisible = new String();
        int tickUnit = width/20;
    	
        for(int i = 1; i <= width; i++){
    		if(width >= 100 
    				&& i % tickUnit !=0 
    				&& (i != width)){
				invisible += (char)29;
        		categories.add(invisible);
    		} else {
        		categories.add(String.valueOf(i));
    		}
    	}

        return categories;
	}
}
