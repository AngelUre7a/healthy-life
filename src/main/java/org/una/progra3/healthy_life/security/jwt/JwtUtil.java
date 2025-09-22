package org.una.progra3.healthy_life.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;

public class JwtUtil {

	public static final String AUTH_HEADER = "Authorization";
	public static final String BEARER_PREFIX = "Bearer ";
	public static final String REFRESH_HEADER = "X-Refreshed-Token";

	public static final String ATTR_USERNAME = "auth.username";
	public static final String ATTR_EMAIL = "auth.email";
	public static final String ATTR_USER_ID = "auth.userId";
	public static final String ATTR_ROLE = "auth.role";

	private JwtUtil() {}

	public static Optional<String> getBearerToken(HttpServletRequest request) {
		String header = request.getHeader(AUTH_HEADER);
		if (header == null || !header.startsWith(BEARER_PREFIX)) return Optional.empty();
		return Optional.of(header.substring(BEARER_PREFIX.length()));
	}

	public static void setBearerToken(HttpServletResponse response, String token) {
		if (token != null && !token.isBlank()) {
			response.setHeader(AUTH_HEADER, BEARER_PREFIX + token);
		}
	}

	public static String maskTokenForLog(String token) {
		if (token == null) return "null";
		int len = token.length();
		if (len <= 20) return "***";
		String start = token.substring(0, 10);
		String end = token.substring(len - 6);
		return start + "..." + end;
	}

	public static void attachAuthAttributes(HttpServletRequest request, JwtTokenProvider provider, String token) {
		if (token == null || token.isBlank()) return;
		String username = provider.getUsernameFromToken(token);
		if (username == null || provider.isTokenExpired(token)) return;
		request.setAttribute(ATTR_USERNAME, username);

		Long userId = provider.getUserIdFromToken(token);
		if (userId != null) request.setAttribute(ATTR_USER_ID, userId);

		String email = provider.getEmailFromToken(token);
		if (email != null) request.setAttribute(ATTR_EMAIL, email);

		String role = provider.getUserRoleFromToken(token);
		if (role != null) request.setAttribute(ATTR_ROLE, role);
	}
}
