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
public class ProjectDto {

	@NonNull
	private Long id;

	@NonNull
	private String name;

	@NonNull
	@JsonProperty("creator_email")
	private String creatorEmail;

	@NonNull
	@JsonProperty("created_at")
	private Instant createdAt;
}
