package com.merantory.task.tracker.service.helper;

import com.merantory.task.tracker.api.exception.NotFoundException;
import com.merantory.task.tracker.model.Task;
import com.merantory.task.tracker.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskServiceHelper {

	private final TaskRepository taskRepository;

	public Task getTasksOrThrowException(Long taskId) {
		return taskRepository.findById(taskId)
				.orElseThrow(
						() -> new NotFoundException(
								String.format("Task with id \"%d\" not found", taskId)));
	}
}
