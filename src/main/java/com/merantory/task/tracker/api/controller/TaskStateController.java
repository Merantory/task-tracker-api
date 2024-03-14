package com.merantory.task.tracker.api.controller;

import com.merantory.task.tracker.api.convertor.TaskStateConvertor;
import com.merantory.task.tracker.api.dto.AckDto;
import com.merantory.task.tracker.api.dto.TaskStateDto;
import com.merantory.task.tracker.api.exception.BadRequestException;
import com.merantory.task.tracker.model.TaskState;
import com.merantory.task.tracker.service.TaskStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TaskStateController {

	private final TaskStateService taskStateService;

	private final TaskStateConvertor taskStateConvertor;

	@GetMapping("/projects/{project_id}/task-states")
	public ResponseEntity<List<TaskStateDto>> getTaskStates(@PathVariable("project_id") Long projectId) {
		List<TaskState> taskStates = taskStateService.fetchAllWithProjectId(projectId);

		return new ResponseEntity<>(taskStates
				.stream()
				.map(taskStateConvertor::toDto)
				.toList(), HttpStatus.OK);
	}

	@PostMapping("/projects/{project_id}/task-states")
	public ResponseEntity<TaskStateDto> createTaskState(@PathVariable("project_id") Long projectId,
														@RequestParam("task_state_name") String taskStateName) {

		if (taskStateName.isBlank()) {
			throw new BadRequestException("Task state name can't be empty");
		}

		TaskState taskState = taskStateService.saveAndFlush(projectId, taskStateName);

		return new ResponseEntity<>(taskStateConvertor.toDto(taskState), HttpStatus.CREATED);
	}

	@PatchMapping("/task-states/{task_state_id}")
	public ResponseEntity<TaskStateDto> updateTaskState(@PathVariable("task_state_id") Long taskStateId,
														@RequestParam("name") String taskStateName) {
		if (taskStateName.isBlank()) {
			throw new BadRequestException("Task state name can't be empty");
		}

		TaskState taskState = taskStateService.update(taskStateId, taskStateName);

		return new ResponseEntity<>(taskStateConvertor.toDto(taskState), HttpStatus.OK);
	}

	@PatchMapping("/task-states/{task_state_id}/position/change")
	public ResponseEntity<TaskStateDto> changeTaskStatePosition(@PathVariable("task_state_id") Long taskStateId,
																@RequestParam("left_task_state_id") Optional<Long> optionalLeftTaskStateId) {

		if (optionalLeftTaskStateId.isPresent() && optionalLeftTaskStateId.get().equals(taskStateId)) {
			throw new BadRequestException("task state id should not be equal left task state id");
		}

		TaskState taskState = taskStateService.changePosition(taskStateId, optionalLeftTaskStateId);
		return new ResponseEntity<>(taskStateConvertor.toDto(taskState), HttpStatus.OK);
	}

	@DeleteMapping("/task-states/{task_state_id}")
	public ResponseEntity<AckDto> deleteTaskState(@PathVariable("task_state_id") Long taskStateId) {
		taskStateService.deleteById(taskStateId);

		return new ResponseEntity<>(AckDto.makeDefault(true), HttpStatus.OK);
	}
}
