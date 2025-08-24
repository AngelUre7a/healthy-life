package org.una.progra3.healthy_life.security.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.core.publisher.Mono;

@Configuration
public class GraphQLContextConfig implements WebGraphQlInterceptor {
    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest servletRequest = attrs.getRequest();
            if (servletRequest != null) {
                request.configureExecutionInput((executionInput, builder) ->
                    builder.graphQLContext(contextBuilder ->
                        contextBuilder.put("httpServletRequest", servletRequest)
                    ).build()
                );
            }
        }
        return chain.next(request);
    }
}
