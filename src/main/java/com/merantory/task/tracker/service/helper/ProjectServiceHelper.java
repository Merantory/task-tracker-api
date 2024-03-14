package com.merantory.task.tracker.service.helper;

import com.merantory.task.tracker.api.exception.NotFoundException;
import com.merantory.task.tracker.model.Project;
import com.merantory.task.tracker.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectServiceHelper {

	private final ProjectRepository projectRepository;

	public Project getProjectOrThrowException(Long projectId) {
		return projectRepository
				.findById(projectId)
				.orElseThrow(
						() -> new NotFoundException(
								String.format("Project with id \"%d\" not found", projectId)));
	}
}
