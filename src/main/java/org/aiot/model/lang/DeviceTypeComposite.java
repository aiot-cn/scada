package org.aiot.model.lang;

import org.aiot.model.table.DeviceCommand;
import org.aiot.model.table.DeviceProperty;
import org.aiot.model.table.DeviceType;

import java.util.ArrayList;
import java.util.List;

public class DeviceTypeComposite extends DeviceType {

	List<DeviceCommand> command = new ArrayList<>();
	List<DeviceProperty> property = new ArrayList<>();

	public List<DeviceCommand> getCommand() {
		return command;
	}

	public void setCommand(List<DeviceCommand> command) {
		this.command = command;
	}

	public List<DeviceProperty> getProperty() {
		return property;
	}

	public void setProperty(List<DeviceProperty> property) {
		this.property = property;
	}
}
