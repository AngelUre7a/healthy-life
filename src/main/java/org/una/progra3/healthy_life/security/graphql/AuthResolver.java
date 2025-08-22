package org.una.progra3.healthy_life.security.graphql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.una.progra3.healthy_life.security.graphql.responses.LoginResponse;
import org.una.progra3.healthy_life.service.AuthenticationService;

@Controller
public class AuthResolver {


	@Autowired
	private AuthenticationService authenticationService;

	//Metodo de login en GraphQL
	@MutationMapping("login")
	public LoginResponse login(@Argument String email, @Argument String password) {
		return authenticationService.login(email, password);
	}
}
