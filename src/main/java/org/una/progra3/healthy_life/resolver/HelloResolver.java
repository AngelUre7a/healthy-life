package org.una.progra3.healthy_life.resolver;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class HelloResolver {

    @QueryMapping
    public String hello() {
        return "¡Hola desde GraphQL!";
    }
}
