package com.merantory.task.tracker.api.controller;

import com.merantory.task.tracker.api.convertor.ProjectConvertor;
import com.merantory.task.tracker.api.dto.AckDto;
import com.merantory.task.tracker.api.dto.ProjectDto;
import com.merantory.task.tracker.api.exception.BadRequestException;
import com.merantory.task.tracker.model.Project;
import com.merantory.task.tracker.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {

	private final ProjectService projectService;

	private final ProjectConvertor projectConvertor;


	@GetMapping
	public ResponseEntity<List<ProjectDto>> fetchProjects(
			@RequestParam(value = "prefix_name", required = false) Optional<String> prefixNameOptional) {
		prefixNameOptional = prefixNameOptional.filter(prefixName -> !prefixName.isBlank());

		List<Project> projectList = prefixNameOptional
				.map(projectService::fetchAllWithPrefixName)
				.orElseGet(projectService::getAll);

		return new ResponseEntity<>(projectList.stream().map(projectConvertor::toDto).toList(), HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<ProjectDto> createProject(@RequestParam("name") String name,
													@RequestParam("creator_email") String creatorEmail) {
		if (name.isBlank()) {
			throw new BadRequestException("Name can't be empty.");
		}

		if (creatorEmail.isBlank()) {
			throw new BadRequestException("Creator email can't be empty.");
		}

		Project project = projectService.saveAndFlush(name, creatorEmail);

		return new ResponseEntity<>(projectConvertor.toDto(project), HttpStatus.CREATED);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<ProjectDto> updateProject(@PathVariable("id") Long projectId,
													@RequestParam("name") String name) {
		if (name.isBlank()) {
			throw new BadRequestException("Name can't be empty.");
		}

		Project project = projectService.update(projectId, name);

		return new ResponseEntity<>(projectConvertor.toDto(project), HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<AckDto> deleteProject(@PathVariable("id") Long projectId) {
		projectService.deleteById(projectId);

		return new ResponseEntity<>(AckDto.makeDefault(true), HttpStatus.OK);
	}
}
