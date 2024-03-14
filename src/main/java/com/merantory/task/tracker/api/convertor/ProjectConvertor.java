package com.merantory.task.tracker.api.convertor;

import com.merantory.task.tracker.api.dto.ProjectDto;
import com.merantory.task.tracker.model.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectConvertor {

	public ProjectDto toDto(Project entity) {
		ProjectDto projectDto = new ProjectDto();
		projectDto.setId(entity.getId());
		projectDto.setName(entity.getName());
		projectDto.setCreatedAt(entity.getCreatedAt());
		projectDto.setCreatorEmail(entity.getCreatorEmail());

		return projectDto;
	}
}
