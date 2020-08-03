package application.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class TimeEntry {

	private SimpleStringProperty name;
	private SimpleDoubleProperty value;
	
	public TimeEntry() {}
	
	public TimeEntry(String name) {
		this.name = new SimpleStringProperty(name);
		this.value = new SimpleDoubleProperty(0);
	}
	
	public TimeEntry(String name, Double value) {
		this.name = new SimpleStringProperty(name);
		this.value = new SimpleDoubleProperty(value);
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public Double getValue() {
		return value.get();
	}

	public void setValue(Double value) {
		this.value.set(value);;
	}
}
