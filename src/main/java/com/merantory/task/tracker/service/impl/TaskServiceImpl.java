package com.merantory.task.tracker.service.impl;

import com.merantory.task.tracker.model.Task;
import com.merantory.task.tracker.model.TaskState;
import com.merantory.task.tracker.repository.TaskRepository;
import com.merantory.task.tracker.service.TaskService;
import com.merantory.task.tracker.service.helper.ProjectServiceHelper;
import com.merantory.task.tracker.service.helper.TaskServiceHelper;
import com.merantory.task.tracker.service.helper.TaskStateServiceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {

	private final TaskRepository taskRepository;

	private final TaskServiceHelper taskServiceHelper;

	private final ProjectServiceHelper projectServiceHelper;

	private final TaskStateServiceHelper taskStateServiceHelper;

	@Override
	public List<Task> fetchAllWithProjectId(Long projectId) {
		return projectServiceHelper
				.getProjectOrThrowException(projectId)
				.getTaskStates()
				.stream()
				.map(TaskState::getTasks)
				.flatMap(Collection::stream)
				.toList();
	}

	@Override
	public List<Task> fetchAllWithTaskStateId(Long taskStateId) {
		return taskStateServiceHelper.getTaskStateOrThrowException(taskStateId).getTasks();
	}

	@Override
	@Transactional
	public Task saveAndFlush(Long taskStateId, String name, String description) {
		TaskState taskState = taskStateServiceHelper.getTaskStateOrThrowException(taskStateId);

		Task taskForSave = new Task();
		taskForSave.setName(name);
		taskForSave.setDescription(description);
		taskForSave.setTaskState(taskState);

		taskForSave = taskRepository.saveAndFlush(taskForSave);

		log.info("Task with id \"{}\" was saved for task state with id \"{}\".",
				taskForSave.getId(), taskForSave.getTaskState().getId());

		return taskForSave;
	}

	@Override
	@Transactional
	public Task update(Long taskId, String name, String description) {
		Task taskForUpdate = taskServiceHelper.getTasksOrThrowException(taskId);

		if (name != null) {
			taskForUpdate.setName(name);
		}

		if (description != null) {
			taskForUpdate.setDescription(description);
		}

		return taskRepository.saveAndFlush(taskForUpdate);
	}

	@Override
	@Transactional
	public Task changeTaskState(Long taskId, Long taskStateId) {
		Task task = taskServiceHelper.getTasksOrThrowException(taskId);
		TaskState newTaskState = taskStateServiceHelper.getTaskStateOrThrowException(taskStateId);

		task.setTaskState(newTaskState);

		return taskRepository.saveAndFlush(task);
	}

	@Override
	@Transactional
	public void deleteById(Long taskId) {
		taskServiceHelper.getTasksOrThrowException(taskId);
		taskRepository.deleteById(taskId);

		log.info("Task with id \"{}\" was deleted.", taskId);
	}
}

