package com.merantory.task.tracker.service.impl;

import com.merantory.task.tracker.api.exception.BadRequestException;
import com.merantory.task.tracker.model.Project;
import com.merantory.task.tracker.model.TaskState;
import com.merantory.task.tracker.repository.TaskStateRepository;
import com.merantory.task.tracker.service.TaskStateService;
import com.merantory.task.tracker.service.helper.ProjectServiceHelper;
import com.merantory.task.tracker.service.helper.TaskStateServiceHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskStateServiceImpl implements TaskStateService {

	@PersistenceContext
	private final EntityManager entityManager;

	private final TaskStateRepository taskStateRepository;

	private final ProjectServiceHelper projectServiceHelper;

	private final TaskStateServiceHelper taskStateServiceHelper;

	@Override
	public List<TaskState> fetchAllWithProjectId(Long projectId) {
		return projectServiceHelper.getProjectOrThrowException(projectId).getTaskStates();
	}

	@Override
	@Transactional
	public TaskState saveAndFlush(Long projectId, String taskStateName) {
		Project project = projectServiceHelper.getProjectOrThrowException(projectId);

		project.getTaskStates()
				.stream()
				.map(TaskState::getName)
				.filter(name -> name.equalsIgnoreCase(taskStateName))
				.findAny()
				.ifPresent(it -> {
					throw new BadRequestException(String.format("Task state \"%s\" already exists.", taskStateName));
				});

		Optional<TaskState> optionalLastTaskState = Optional.empty();

		for (TaskState taskState : project.getTaskStates()) {
			if (taskState.getRightTaskState().isEmpty()) {
				optionalLastTaskState = Optional.of(taskState);
				break;
			}
		}

		TaskState taskStateForSave = new TaskState();
		taskStateForSave.setProject(project);
		taskStateForSave.setName(taskStateName);

		if (optionalLastTaskState.isPresent()) {
			TaskState lastTaskState = optionalLastTaskState.get();
			log.info("Last task with taskId={} for projectId={} founded.", lastTaskState.getId(), projectId);
			lastTaskState.setRightTaskState(taskStateForSave);
			taskStateForSave.setLeftTaskState(lastTaskState);
			taskStateRepository.save(lastTaskState);
		}

		return taskStateRepository.saveAndFlush(taskStateForSave);
	}

	@Override
	@Transactional
	public TaskState update(Long taskStateId, String taskStateName) {
		TaskState taskState = taskStateServiceHelper.getTaskStateOrThrowException(taskStateId);

		List<TaskState> projectTaskStates = taskState.getProject().getTaskStates();
		projectTaskStates.stream()
				.filter(projectTaskState -> Objects.equals(projectTaskState.getName(), taskStateName))
				.filter(projectTaskState -> !Objects.equals(projectTaskState.getId(), taskState.getId()))
				.findAny()
				.ifPresent(it -> {
					log.info("Task state with name \"{}\" already exists for project {}.",
							taskStateName, taskState.getProject().getId());
					throw new BadRequestException(String.format(
							"Task state with this name \"%s\" already exists for project \"%s\" with id=%d.",
							taskStateName, taskState.getProject().getName(), taskState.getProject().getId()
					));
				});

		taskState.setName(taskStateName);
		return taskStateRepository.saveAndFlush(taskState);
	}

	@Override
	@Transactional
	public TaskState changePosition(Long taskStateId, Optional<Long> optionalLeftTaskStateId) {
		Query query = entityManager.createNativeQuery("SET CONSTRAINTS ALL DEFERRED");
		query.executeUpdate();

		TaskState changeTaskState = taskStateServiceHelper.getTaskStateOrThrowException(taskStateId);

		Project project = changeTaskState.getProject();

		Optional<Long> optionalOldLeftTaskStateId = changeTaskState
				.getLeftTaskState()
				.map(TaskState::getId);

		if (optionalOldLeftTaskStateId.equals(optionalLeftTaskStateId)) {
			return changeTaskState;
		}

		Optional<TaskState> optionalNewLeftTaskState = optionalLeftTaskStateId
				.map(leftTaskStateId -> {

					if (taskStateId.equals(leftTaskStateId)) {
						throw new BadRequestException("Left task state id equals changed task state.");
					}

					TaskState leftTaskStateEntity = taskStateServiceHelper.getTaskStateOrThrowException(leftTaskStateId);

					if (!project.getId().equals(leftTaskStateEntity.getProject().getId())) {
						throw new BadRequestException("Task state position can be changed within the same project.");
					}

					return leftTaskStateEntity;
				});

		Optional<TaskState> optionalNewRightTaskState;
		if (optionalNewLeftTaskState.isEmpty()) {

			optionalNewRightTaskState = project
					.getTaskStates()
					.stream()
					.filter(anotherTaskState -> anotherTaskState.getLeftTaskState().isEmpty())
					.findAny();
		} else {

			optionalNewRightTaskState = optionalNewLeftTaskState
					.get()
					.getRightTaskState();
		}

		replaceOldTaskStatePosition(changeTaskState);

		if (optionalNewLeftTaskState.isPresent()) {

			TaskState newLeftTaskState = optionalNewLeftTaskState.get();

			newLeftTaskState.setRightTaskState(changeTaskState);

			changeTaskState.setLeftTaskState(newLeftTaskState);
		} else {
			changeTaskState.setLeftTaskState(null);
		}

		if (optionalNewRightTaskState.isPresent()) {

			TaskState newRightTaskState = optionalNewRightTaskState.get();

			newRightTaskState.setLeftTaskState(changeTaskState);

			changeTaskState.setRightTaskState(newRightTaskState);
		} else {
			changeTaskState.setRightTaskState(null);
		}

		changeTaskState = taskStateRepository.saveAndFlush(changeTaskState);

		optionalNewLeftTaskState
				.ifPresent(taskStateRepository::saveAndFlush);

		optionalNewRightTaskState
				.ifPresent(taskStateRepository::saveAndFlush);

		return changeTaskState;
	}

	@Override
	@Transactional
	public void deleteById(Long taskStateId) {
		Query query = entityManager.createNativeQuery("SET CONSTRAINTS ALL DEFERRED");
		query.executeUpdate();

		TaskState taskStateForDelete = taskStateServiceHelper.getTaskStateOrThrowException(taskStateId);
		replaceOldTaskStatePosition(taskStateForDelete);

		taskStateRepository.deleteById(taskStateId);

		log.info("Task state with id \"{}\" was deleted.", taskStateId);
	}

	private void replaceOldTaskStatePosition(TaskState changeTaskState) {

		Optional<TaskState> optionalOldLeftTaskState = changeTaskState.getLeftTaskState();
		Optional<TaskState> optionalOldRightTaskState = changeTaskState.getRightTaskState();

		optionalOldLeftTaskState
				.ifPresent(it -> {

					it.setRightTaskState(optionalOldRightTaskState.orElse(null));

					taskStateRepository.saveAndFlush(it);

					if (optionalOldRightTaskState.isPresent()) {
						log.info("Right task state for task state with id \"{}\" is task state with id \"{}\"",
								it.getId(), optionalOldRightTaskState.get().getId());
					} else {
						log.info("Right task state for task state with id \"{}\" is null", it.getId());
					}
				});

		optionalOldRightTaskState
				.ifPresent(it -> {

					it.setLeftTaskState(optionalOldLeftTaskState.orElse(null));

					taskStateRepository.saveAndFlush(it);

					if (optionalOldLeftTaskState.isPresent()) {
						log.info("Left task state for task state with id \"{}\" is task state with id \"{}\".",
								it.getId(), optionalOldLeftTaskState.get().getId());
					} else {
						log.info("Left task state for task state with id \"{}\" is null.", it.getId());
					}
				});
	}
}
