package org.una.progra3.healthy_life.security.graphql.responses;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Map;

import org.junit.jupiter.api.Test;

class ResponseMenssageTest {

    @Test
    void ok_and_okWithDetails_shouldPopulateFields() {
        var r1 = ResponseMenssage.ok("Hecho");
        assertTrue(r1.isSuccess());
        assertEquals("Hecho", r1.getMessage());
        assertNull(r1.getCode());
        assertNotNull(r1.getTimestamp());
        assertTrue(r1.getDetails().isEmpty());

        var r2 = ResponseMenssage.ok("Actualizado", Map.of("id", 1L));
        assertTrue(r2.isSuccess());
        assertEquals("Actualizado", r2.getMessage());
        assertNull(r2.getCode());
        assertEquals(1L, r2.getDetails().get("id"));
    }

    @Test
    void okWithCode_shouldSetCode() {
        var r = ResponseMenssage.okWithCode("CREATED", "Creado");
        assertTrue(r.isSuccess());
        assertEquals("CREATED", r.getCode());
        assertEquals("Creado", r.getMessage());
    }

    @Test
    void okWithCode_withDetails_shouldSetCodeAndDetails() {
        var details = Map.<String, Object>of("id", 42L, "status", "ok");
        var r = ResponseMenssage.okWithCode("CUSTOM", "Listo", details);
        assertTrue(r.isSuccess());
        assertEquals("CUSTOM", r.getCode());
        assertEquals("Listo", r.getMessage());
        assertEquals(42L, r.getDetails().get("id"));
        assertEquals("ok", r.getDetails().get("status"));
    }

    @Test
    void error_shouldPopulateErrorFields() {
        var r = ResponseMenssage.error("NOT_FOUND", "No existe");
        assertFalse(r.isSuccess());
        assertEquals("NOT_FOUND", r.getCode());
        assertEquals("No existe", r.getMessage());
        assertTrue(r.getDetails().isEmpty());
    }

    @Test
    void error_withDetails_shouldCopyDetailsAndKeepTimestamp() throws InterruptedException {
        var before = Instant.now();
        var r = ResponseMenssage.error("CONFLICT", "Duplicado", Map.of("email", "x@y.com"));
        assertFalse(r.isSuccess());
        assertEquals("CONFLICT", r.getCode());
        assertEquals("x@y.com", r.getDetails().get("email"));
        assertTrue(!r.getTimestamp().isBefore(before));
    var original = Map.<String, Object>of("k", "v");
    ResponseMenssage r2 = ResponseMenssage.ok("msg", original);
        assertThrows(UnsupportedOperationException.class, () -> r2.getDetails().put("x", 1));
    }

    @Test
    void crud_convenience_methods_shouldSetStandardCodes() {
        assertEquals("CREATED", ResponseMenssage.created("creado").getCode());
        assertEquals("UPDATED", ResponseMenssage.updated("actualizado").getCode());
        assertEquals("DELETED", ResponseMenssage.deleted("eliminado").getCode());
    }

    @Test
    void crud_convenience_methods_withDetails_shouldSetCodesAndDetails() {
        var created = ResponseMenssage.created("creado", Map.<String, Object>of("id", 1L));
        assertTrue(created.isSuccess());
        assertEquals("CREATED", created.getCode());
        assertEquals(1L, created.getDetails().get("id"));

        var updated = ResponseMenssage.updated("actualizado", Map.<String, Object>of("id", 2L));
        assertTrue(updated.isSuccess());
        assertEquals("UPDATED", updated.getCode());
        assertEquals(2L, updated.getDetails().get("id"));

        var deleted = ResponseMenssage.deleted("eliminado", Map.<String, Object>of("id", 3L));
        assertTrue(deleted.isSuccess());
        assertEquals("DELETED", deleted.getCode());
        assertEquals(3L, deleted.getDetails().get("id"));
    }
}
