package com.merantory.task.tracker.service.impl;

import com.merantory.task.tracker.api.exception.BadRequestException;
import com.merantory.task.tracker.model.EmailMessage;
import com.merantory.task.tracker.model.Project;
import com.merantory.task.tracker.repository.ProjectRepository;
import com.merantory.task.tracker.service.ProjectService;
import com.merantory.task.tracker.service.helper.ProjectServiceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {

	private final ProjectRepository projectRepository;

	private final ProjectServiceHelper projectServiceHelper;

	private final KafkaTemplate<String, EmailMessage> emailMessageKafkaTemplate;

	@Value("${kafka.email-topic}")
	private String emailTopic;


	@Override
	public List<Project> getAll() {
		return projectRepository.findAllBy();
	}

	@Override
	public List<Project> fetchAllWithPrefixName(String name) {
		return projectRepository.findAllByNameStartsWithIgnoreCase(name);
	}

	@Override
	@Transactional
	public void deleteById(Long projectId) {
		Project projectForDelete = projectServiceHelper.getProjectOrThrowException(projectId);
		projectRepository.deleteById(projectId);
		emailMessageKafkaTemplate.send(emailTopic, new EmailMessage(projectForDelete.getCreatorEmail(),
				"Project \"" + projectForDelete.getName() + "\" was deleted.",
				"Project was deleted at " + Instant.now()));
		log.info("Project with id \"{}\" was deleted.", projectId);
	}

	@Override
	@Transactional
	public Project saveAndFlush(String name, String creatorEmail) {
		projectRepository
				.findByName(name)
				.ifPresent(project -> {
					throw new BadRequestException(String.format("Project \"%s\" with this name already exists.", name));
				});

		Project project = new Project();
		project.setName(name);
		project.setCreatorEmail(creatorEmail);

		return projectRepository.saveAndFlush(project);
	}

	@Override
	@Transactional
	public Project update(Long projectId, String projectName) {
		Project project = projectServiceHelper.getProjectOrThrowException(projectId);

		projectRepository
				.findByName(projectName)
				.filter(anotherProject -> !Objects.equals(anotherProject.getId(), projectId))
				.ifPresent(anotherProject -> {
					throw new BadRequestException(String.format("Project \"%s\" with this name already exists.",
							projectName));
				});

		project.setName(projectName);

		return projectRepository.saveAndFlush(project);
	}
}
