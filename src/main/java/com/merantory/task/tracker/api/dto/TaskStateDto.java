package com.merantory.task.tracker.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskStateDto {

	@NonNull
	private Long id;

	@NonNull
	private String name;

	@JsonProperty("left_task_state_id")
	private Long leftTaskStateId;

	@JsonProperty("right_task_state_id")
	private Long rightTaskStateId;

	@NonNull
	@JsonProperty("created_at")
	private Instant createdAt;

	@NonNull
	List<TaskDto> tasks;

	@NonNull
	@JsonProperty("project_id")
	private Long projectId;
}
