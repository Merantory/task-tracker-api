package com.merantory.task.tracker.api.convertor;

import com.merantory.task.tracker.api.dto.TaskDto;
import com.merantory.task.tracker.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskConvertor {

	public TaskDto toDto(Task entity) {
		TaskDto taskDto = new TaskDto();
		taskDto.setId(entity.getId());
		taskDto.setName(entity.getName());
		taskDto.setDescription(entity.getDescription());
		taskDto.setTaskStateId(entity.getTaskState().getId());
		taskDto.setCreatedAt(entity.getCreatedAt());

		return taskDto;
	}
}
