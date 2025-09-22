package org.una.progra3.healthy_life.resolver;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.una.progra3.healthy_life.entity.Role;
import org.una.progra3.healthy_life.entity.User;
import org.una.progra3.healthy_life.entity.enums.RoleType;
import org.una.progra3.healthy_life.dtos.CreateRoleInput;
import org.una.progra3.healthy_life.dtos.UpdateRoleInput;
import org.una.progra3.healthy_life.service.RoleService;
import org.una.progra3.healthy_life.service.AuthenticationService;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class RoleResolverTest {
	@Mock RoleService roleService;
	@Mock AuthenticationService authenticationService;
	@InjectMocks RoleResolver roleResolver;

	@Test
	void testAllRoles() {
		Mockito.when(roleService.getAllRoles()).thenReturn(List.of(new Role()));
		List<Role> result = roleResolver.allRoles();
		assertEquals(1, result.size());
	}

	@Test
	void testRoleById() {
		Mockito.when(roleService.getRoleById(1L)).thenReturn(new Role());
		Role result = roleResolver.roleById(1L);
		assertNotNull(result);
	}

	@Test
	void testCreateRole() {
		CreateRoleInput input = new CreateRoleInput();
		input.setName(RoleType.ADMIN);
		input.setDescription("Administrador");
		input.setCanRead(true);
		input.setCanWrite(true);
		input.setCanDelete(true);
		Role role = new Role();
		Mockito.when(roleService.createRole(input)).thenReturn(role);
		Role result = roleResolver.createRole(input);
		assertNotNull(result);
	}

	@Test
	void testUpdateRole() {
		UpdateRoleInput input = new UpdateRoleInput();
		input.setName(RoleType.SUPERVISOR);
		input.setDescription("Edita roles");
		input.setCanRead(true);
		input.setCanWrite(true);
		input.setCanDelete(false);
		Role role = new Role();
		Mockito.when(roleService.updateRole(1L, input)).thenReturn(role);
		Role result = roleResolver.updateRole(1L, input);
		assertNotNull(result);
	}

	@Test
	void testDeleteRole() {
		Mockito.when(roleService.deleteRole(1L)).thenReturn(true);
		Boolean result = roleResolver.deleteRole(1L);
		assertTrue(result);
	}

	@Test
	void testAssignRoleToUser() {
		User user = new User();
		Mockito.when(roleService.assignRoleToUser(2L, 3L)).thenReturn(user);
		User result = roleResolver.assignRoleToUser(2L, 3L);
		assertNotNull(result);
	}
}
