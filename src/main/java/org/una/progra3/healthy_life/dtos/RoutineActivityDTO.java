package org.una.progra3.healthy_life.dtos;


import java.time.LocalTime;

public class RoutineActivityDTO {
	private Long id;
	private Long routineId;
	private Long habitId;
	private Integer duration;
	private LocalTime targetTime;
	private String notes;

	public RoutineActivityDTO() {}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Long getRoutineId() {
		return routineId;
	}
	public void setRoutineId(Long routineId) {
		this.routineId = routineId;
	}

	public Long getHabitId() {
		return habitId;
	}
	public void setHabitId(Long habitId) {
		this.habitId = habitId;
	}

	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public LocalTime getTargetTime() {
		return targetTime;
	}
	public void setTargetTime(LocalTime targetTime) {
		this.targetTime = targetTime;
	}

	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
}
