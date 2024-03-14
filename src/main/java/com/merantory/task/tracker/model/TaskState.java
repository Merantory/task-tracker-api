package com.merantory.task.tracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "task_state")
public class TaskState {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@OneToOne
	private TaskState leftTaskState;

	@OneToOne
	private TaskState rightTaskState;

	@Column(name = "created_at")
	private Instant createdAt = Instant.now();

	@ManyToOne
	private Project project;

	@OneToMany
	@JoinColumn(name = "task_state_id", referencedColumnName = "id")
	private List<Task> tasks = new ArrayList<>();

	public Optional<TaskState> getLeftTaskState() {
		return Optional.ofNullable(leftTaskState);
	}

	public Optional<TaskState> getRightTaskState() {
		return Optional.ofNullable(rightTaskState);
	}
}

