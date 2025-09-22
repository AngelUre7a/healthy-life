package org.una.progra3.healthy_life.security.config;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class GraphQLContextConfigTest {

    private final GraphQLContextConfig interceptor = new GraphQLContextConfig();

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void intercept_noRequestAttributes_callsChainOnly() {
        // No attributes set
        WebGraphQlRequest request = mock(WebGraphQlRequest.class);
        WebGraphQlResponse response = mock(WebGraphQlResponse.class);
        WebGraphQlInterceptor.Chain chain = mock(WebGraphQlInterceptor.Chain.class);
        when(chain.next(request)).thenReturn(Mono.just(response));

        WebGraphQlResponse result = interceptor.intercept(request, chain).block();

        assertNotNull(result);
        verify(chain, times(1)).next(request);
        verify(request, never()).configureExecutionInput(any());
    }

    @Test
    void intercept_attributesPresent_butServletRequestNull_doesNotConfigureExecutionInput() {
        ServletRequestAttributes attrs = mock(ServletRequestAttributes.class);
        when(attrs.getRequest()).thenReturn(null);
        RequestContextHolder.setRequestAttributes(attrs);

        WebGraphQlRequest request = mock(WebGraphQlRequest.class);
        WebGraphQlResponse response = mock(WebGraphQlResponse.class);
        WebGraphQlInterceptor.Chain chain = mock(WebGraphQlInterceptor.Chain.class);
        when(chain.next(request)).thenReturn(Mono.just(response));

        WebGraphQlResponse result = interceptor.intercept(request, chain).block();

        assertNotNull(result);
        verify(chain, times(1)).next(request);
        verify(request, never()).configureExecutionInput(any());
    }

    @Test
    void intercept_attributesAndServletRequestPresent_configuresExecutionInput() {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        ServletRequestAttributes attrs = mock(ServletRequestAttributes.class);
        when(attrs.getRequest()).thenReturn(httpServletRequest);
        RequestContextHolder.setRequestAttributes(attrs);

        WebGraphQlRequest request = mock(WebGraphQlRequest.class);
        WebGraphQlResponse response = mock(WebGraphQlResponse.class);
        WebGraphQlInterceptor.Chain chain = mock(WebGraphQlInterceptor.Chain.class);
        when(chain.next(request)).thenReturn(Mono.just(response));

        // Do not execute the default method; just verify it's invoked
        doNothing().when(request).configureExecutionInput(any());

        WebGraphQlResponse result = interceptor.intercept(request, chain).block();

        assertNotNull(result);
        verify(chain, times(1)).next(request);
    verify(request, times(1)).configureExecutionInput(any());
    }
}
