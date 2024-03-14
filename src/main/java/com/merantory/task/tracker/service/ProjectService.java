package com.merantory.task.tracker.service;

import com.merantory.task.tracker.model.Project;

import java.util.List;

public interface ProjectService {

	List<Project> getAll();

	List<Project> fetchAllWithPrefixName(String name);

	Project saveAndFlush(String name, String creatorEmail);

	Project update(Long projectId, String projectName);

	void deleteById(Long projectId);
}
