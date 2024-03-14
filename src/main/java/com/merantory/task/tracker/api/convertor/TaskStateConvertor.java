package com.merantory.task.tracker.api.convertor;

import com.merantory.task.tracker.api.dto.TaskStateDto;
import com.merantory.task.tracker.model.TaskState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskStateConvertor {

	private final TaskConvertor taskConvertor;

	public TaskStateDto toDto(TaskState entity) {
		TaskStateDto taskStateDto = new TaskStateDto();
		taskStateDto.setId(entity.getId());
		taskStateDto.setName(entity.getName());
		taskStateDto.setLeftTaskStateId(entity.getLeftTaskState().map(TaskState::getId).orElse(null));
		taskStateDto.setRightTaskStateId(entity.getRightTaskState().map(TaskState::getId).orElse(null));
		taskStateDto.setCreatedAt(entity.getCreatedAt());
		taskStateDto.setTasks(entity.getTasks().stream().map(taskConvertor::toDto).toList());
		taskStateDto.setProjectId(entity.getProject().getId());

		return taskStateDto;
	}
}
