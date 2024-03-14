package com.merantory.task.tracker.repository;

import com.merantory.task.tracker.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

	Optional<Project> findByName(String name);

	List<Project> findAllBy();

	List<Project> findAllByNameStartsWithIgnoreCase(String prefixName);
}