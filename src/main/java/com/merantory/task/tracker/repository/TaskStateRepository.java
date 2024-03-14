package com.merantory.task.tracker.repository;

import com.merantory.task.tracker.model.TaskState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskStateRepository extends JpaRepository<TaskState, Long> {
}
