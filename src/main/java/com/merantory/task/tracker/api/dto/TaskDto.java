package com.merantory.task.tracker.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

	@NonNull
	private Long id;

	@NonNull
	private String name;

	@NonNull
	private String description;

	@NonNull
	@JsonProperty("task_state_id")
	private Long taskStateId;

	@NonNull
	@JsonProperty("created_at")
	private Instant createdAt;
}
