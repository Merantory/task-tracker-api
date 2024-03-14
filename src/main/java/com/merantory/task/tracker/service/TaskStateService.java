package com.merantory.task.tracker.service;

import com.merantory.task.tracker.model.TaskState;

import java.util.List;
import java.util.Optional;

public interface TaskStateService {

	List<TaskState> fetchAllWithProjectId(Long projectId);

	TaskState saveAndFlush(Long projectId, String taskStateName);

	TaskState update(Long taskStateId, String taskStateName);

	TaskState changePosition(Long taskStateId, Optional<Long> leftTaskState);

	void deleteById(Long taskStateId);
}
