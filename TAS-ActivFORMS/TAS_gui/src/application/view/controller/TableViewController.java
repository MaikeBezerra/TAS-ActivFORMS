package application.view.controller;
import java.util.ArrayList;
import java.util.List;

import application.model.CostEntry;
import application.model.PerformanceEntry;
import application.model.ReliabilityEntry;
import application.model.TimeEntry;
import application.view.TimeTableView;
import application.view.service.CostTableService;
import application.view.service.PerformanceTableService;
import application.view.service.ReliabilityTableService;
import javafx.scene.control.TableView;

public class TableViewController {
	
	private TimeTableView timeTableView;
	private List<TimeEntry> timeEntries;
	
	private CostTableService costTableService;
	private PerformanceTableService performanceTableService;
	private ReliabilityTableService reliabilityTableService;
		
	public TableViewController(TableView<ReliabilityEntry> reliabilityTableView, TableView<CostEntry> costTableView,
			TableView<PerformanceEntry> performanceTableView, TableView<TimeEntry> timeTableView) {
		
		this.reliabilityTableService = new ReliabilityTableService(reliabilityTableView);
		this.costTableService = new CostTableService(costTableView);
		this.performanceTableService = new PerformanceTableService(performanceTableView);
		
		this.timeTableView = new TimeTableView(timeTableView);
		this.timeEntries = new ArrayList<>();
		
	}

	public TimeTableView getTimeTableView() {
		return timeTableView;
	}

	
	public void fillReliabilityDate(String resultFilePath){
		this.reliabilityTableService.fillReliabilityDate(resultFilePath);
	}
	
	public void fillCostData(String resultFilePath){
		this.costTableService.addCostData(resultFilePath);
	}
	
	public void fillPerformanceData(String resultFilePath) {
		this.performanceTableService.fillPerformanceData(resultFilePath);
	}
	
	public void clear(){
	    reliabilityTableService.clearData();
	    costTableService.clearData();
	    performanceTableService.clearData();
	    timeTableView.clearTable();
	    this.timeEntries.clear();
	}
		
	public void fillTimeData() {
		this.timeTableView.fillTimeData(this.timeEntries);
	}
	
	public void addTimeEntry(TimeEntry entry) {
		this.timeEntries.add(entry);
	}

}
