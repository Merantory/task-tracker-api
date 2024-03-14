package com.merantory.task.tracker.api.controller;

import com.merantory.task.tracker.api.convertor.TaskConvertor;
import com.merantory.task.tracker.api.dto.AckDto;
import com.merantory.task.tracker.api.dto.TaskDto;
import com.merantory.task.tracker.api.exception.BadRequestException;
import com.merantory.task.tracker.model.Task;
import com.merantory.task.tracker.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TaskController {

	private final TaskService taskService;

	private final TaskConvertor taskConvertor;

	@GetMapping("/projects/{project_id}/tasks")
	public ResponseEntity<List<TaskDto>> getProjectTasks(@PathVariable("project_id") Long projectId) {
		List<Task> tasks = taskService.fetchAllWithProjectId(projectId);

		return new ResponseEntity<>(tasks.stream().map(taskConvertor::toDto).toList(), HttpStatus.OK);
	}

	@GetMapping("/task-states/{task_state_id}/tasks")
	public ResponseEntity<List<TaskDto>> getTaskStateTasks(@PathVariable("task_state_id") Long taskStateId) {
		List<Task> tasks = taskService.fetchAllWithTaskStateId(taskStateId);

		return new ResponseEntity<>(tasks.stream().map(taskConvertor::toDto).toList(), HttpStatus.OK);
	}

	@PostMapping("/task-states/{task_state_id}/tasks")
	public ResponseEntity<TaskDto> createTask(@PathVariable("task_state_id") Long taskStateId,
											  @RequestParam("task_name") String taskName,
											  @RequestParam("task_description") String taskDescription) {
		if (taskName.isBlank()) {
			throw new BadRequestException("Task name can't be empty");
		}

		if (taskDescription.isBlank()) {
			throw new BadRequestException("Task description can't be empty");
		}

		Task task = taskService.saveAndFlush(taskStateId, taskName, taskDescription);
		return new ResponseEntity<>(taskConvertor.toDto(task), HttpStatus.CREATED);
	}

	@PatchMapping("/tasks/{task_id}")
	public ResponseEntity<TaskDto> updateTask(@PathVariable("task_id") Long taskId,
											  @RequestParam("task_name") Optional<String> optionalTaskName,
											  @RequestParam("task_description") Optional<String> optionalTaskDescription) {

		if (optionalTaskName.isEmpty() && optionalTaskDescription.isEmpty()) {
			throw new BadRequestException("Parameters for update not received");
		}

		if (optionalTaskName.isPresent() && optionalTaskName.get().isBlank()) {
			throw new BadRequestException("Task name should not be blank");
		}

		if (optionalTaskDescription.isPresent() && optionalTaskDescription.get().isBlank()) {
			throw new BadRequestException("Task description should not be blank");
		}

		Task task = taskService
				.update(taskId, optionalTaskName.orElse(null), optionalTaskDescription.orElse(null));

		return new ResponseEntity<>(taskConvertor.toDto(task), HttpStatus.OK);
	}

	@PatchMapping("/tasks/{task_id}/change/task-state")
	public ResponseEntity<TaskDto> changeTaskSateForTask(@PathVariable("task_id") Long taskId,
														 @RequestParam("task_state_id") Long newTaskStateId) {
		Task task = taskService.changeTaskState(taskId, newTaskStateId);

		return new ResponseEntity<>(taskConvertor.toDto(task), HttpStatus.OK);
	}

	@DeleteMapping("/tasks/{task_id}")
	public ResponseEntity<AckDto> deleteTask(@PathVariable("task_id") Long taskId) {
		taskService.deleteById(taskId);

		return new ResponseEntity<>(AckDto.makeDefault(true), HttpStatus.OK);
	}
}
