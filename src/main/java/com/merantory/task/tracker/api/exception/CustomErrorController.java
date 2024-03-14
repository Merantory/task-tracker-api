package com.merantory.task.tracker.api.exception;

import com.merantory.task.tracker.api.dto.ErrorDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@RequiredArgsConstructor
@Controller
public class CustomErrorController implements ErrorController {

	private final ErrorAttributes errorAttributes;

	@RequestMapping("/error")
	public ResponseEntity<ErrorDto> error(WebRequest request) {
		Map<String, Object> attributes = errorAttributes.getErrorAttributes(
				request,
				ErrorAttributeOptions.of(
						ErrorAttributeOptions.Include.EXCEPTION,
						ErrorAttributeOptions.Include.MESSAGE)
		);

		ErrorDto errorDto = new ErrorDto();
		errorDto.setError((String) attributes.get("error"));
		errorDto.setErrorDescription((String) attributes.get("message"));

		return ResponseEntity
				.status((Integer) attributes.get("status"))
				.body(errorDto);
	}
}
