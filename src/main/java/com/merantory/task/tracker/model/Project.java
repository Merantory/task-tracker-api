package com.merantory.task.tracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "project")
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", unique = true)
	private String name;

	@Column(name = "creator_email")
	private String creatorEmail;

	@Column(name = "created_at")
	private Instant createdAt = Instant.now();

	@OneToMany
	@JoinColumn(name = "project_id")
	private List<TaskState> taskStates = new ArrayList<>();
}
