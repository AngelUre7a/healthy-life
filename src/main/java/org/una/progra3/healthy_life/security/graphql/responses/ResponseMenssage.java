package org.una.progra3.healthy_life.security.graphql.responses;

import java.time.Instant;
import java.util.Map;

/**
 * Generic response payload for GraphQL mutations/operations that only need to return
 * a message and status instead of a full entity. Intentionally mirrors lightweight
 * style like LoginResponse in this package.
 *
 * Note: The class name preserves the existing file name ("Menssage"). If desired,
 * it can be renamed to ResponseMessage in a follow-up change.
 */
public class ResponseMenssage {
	private final boolean success;
	private final String message;
	private final String code;
	private final Instant timestamp;
	private final Map<String, Object> details;

	private ResponseMenssage(boolean success, String message, String code, Instant timestamp, Map<String, Object> details) {
		this.success = success;
		this.message = message;
		this.code = code;
		this.timestamp = timestamp == null ? Instant.now() : timestamp;
		this.details = details == null ? Map.of() : Map.copyOf(details);
	}

	public static ResponseMenssage ok(String message) {
		return new ResponseMenssage(true, message, null, Instant.now(), Map.of());
	}

	public static ResponseMenssage ok(String message, Map<String, Object> details) {
		return new ResponseMenssage(true, message, null, Instant.now(), details);
	}

	/**
	 * Success response with an explicit code, useful for i18n keys or client-side routing of success scenarios.
	 */
	public static ResponseMenssage okWithCode(String code, String message) {
		return new ResponseMenssage(true, message, code, Instant.now(), Map.of());
	}

	/**
	 * Success response with an explicit code and details payload.
	 */
	public static ResponseMenssage okWithCode(String code, String message, Map<String, Object> details) {
		return new ResponseMenssage(true, message, code, Instant.now(), details);
	}

	public static ResponseMenssage error(String code, String message) {
		return new ResponseMenssage(false, message, code, Instant.now(), Map.of());
	}

	public static ResponseMenssage error(String code, String message, Map<String, Object> details) {
		return new ResponseMenssage(false, message, code, Instant.now(), details);
	}

	// Common convenience responses for CRUD operations
	public static ResponseMenssage created(String message) {
		return okWithCode("CREATED", message);
	}

	public static ResponseMenssage created(String message, Map<String, Object> details) {
		return okWithCode("CREATED", message, details);
	}

	public static ResponseMenssage updated(String message) {
		return okWithCode("UPDATED", message);
	}

	public static ResponseMenssage updated(String message, Map<String, Object> details) {
		return okWithCode("UPDATED", message, details);
	}

	public static ResponseMenssage deleted(String message) {
		return okWithCode("DELETED", message);
	}

	public static ResponseMenssage deleted(String message, Map<String, Object> details) {
		return okWithCode("DELETED", message, details);
	}

	public boolean isSuccess() { return success; }
	public String getMessage() { return message; }
	public String getCode() { return code; }
	public Instant getTimestamp() { return timestamp; }
	public Map<String, Object> getDetails() { return details; }
}
