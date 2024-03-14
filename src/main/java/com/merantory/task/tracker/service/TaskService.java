package com.merantory.task.tracker.service;

import com.merantory.task.tracker.model.Task;

import java.util.List;

public interface TaskService {

	List<Task> fetchAllWithProjectId(Long projectId);

	List<Task> fetchAllWithTaskStateId(Long taskStateId);

	Task saveAndFlush(Long taskStateId, String name, String description);

	Task update(Long taskId, String name, String description);

	Task changeTaskState(Long taskId, Long taskStateId);

	void deleteById(Long taskId);
}
