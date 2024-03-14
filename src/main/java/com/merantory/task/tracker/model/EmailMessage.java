package com.merantory.task.tracker.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class EmailMessage {

	@NonNull
	private String receiver;

	@NonNull
	private String subject;

	@NonNull
	private String text;
}
