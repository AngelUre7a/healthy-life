package org.una.progra3.healthy_life.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	private final JwtTokenProvider jwtTokenProvider;

	@Autowired
	public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain filterChain) throws ServletException, IOException {

		try {
			Optional<String> tokenOpt = jwtTokenProvider.resolveToken(request);
			if (tokenOpt.isPresent()) {
				String token = tokenOpt.get();

				// Only attach attributes if the token is parseable and not expired.
				String username = jwtTokenProvider.getUsernameFromToken(token);
				if (username != null && !jwtTokenProvider.isTokenExpired(token)) {
					request.setAttribute("auth.username", username);

					Long userId = jwtTokenProvider.getUserIdFromToken(token);
					if (userId != null) request.setAttribute("auth.userId", userId);

					String email = jwtTokenProvider.getEmailFromToken(token);
					if (email != null) request.setAttribute("auth.email", email);

					String role = jwtTokenProvider.getUserRoleFromToken(token);
					if (role != null) request.setAttribute("auth.role", role);
				}
			}
		} catch (Exception ex) {
			// Be lenient: never block the request here. Just log and continue.
			logger.debug("JWT filter continuing without auth context: {}", ex.getMessage());
		}

		filterChain.doFilter(request, response);
	}
}
