package com.merantory.task.tracker.service.helper;

import com.merantory.task.tracker.api.exception.NotFoundException;
import com.merantory.task.tracker.model.TaskState;
import com.merantory.task.tracker.repository.TaskStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskStateServiceHelper {

	private final TaskStateRepository taskStateRepository;

	public TaskState getTaskStateOrThrowException(Long taskStateId) {
		return taskStateRepository
				.findById(taskStateId)
				.orElseThrow(
						() -> new NotFoundException(
								String.format("Task state with id \"%d\" not found", taskStateId)));
	}
}
