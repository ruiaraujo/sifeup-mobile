package pt.up.beta.mobile.datatypes;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Schedule {
	@SerializedName("horario")
	private final List<ScheduleBlock> blocks;

	public Schedule(List<ScheduleBlock> blocks) {
		super();
		this.blocks = blocks;
	}

	public List<ScheduleBlock> getBlocks() {
		return blocks;
	}
}
