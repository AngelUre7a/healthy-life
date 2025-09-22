package org.una.progra3.healthy_life.service;
import org.una.progra3.healthy_life.entity.User;
import org.una.progra3.healthy_life.repository.UserRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.una.progra3.healthy_life.entity.Role;
import org.una.progra3.healthy_life.repository.RoleRepository;
import java.util.Optional;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {
	@Mock
	UserRepository userRepository;
	@Test
	void testUpdateRoleValid() {
		org.una.progra3.healthy_life.dtos.UpdateRoleInput input = new org.una.progra3.healthy_life.dtos.UpdateRoleInput();
		input.setName(org.una.progra3.healthy_life.entity.enums.RoleType.ADMIN);
		input.setDescription("updated");
		input.setCanRead(false);
		input.setCanWrite(true);
		input.setCanDelete(true);
		Role role = new Role();
		role.setId(1L);
		role.setName(org.una.progra3.healthy_life.entity.enums.RoleType.USER);
		Mockito.when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
		Mockito.when(roleRepository.save(Mockito.any())).thenReturn(role);
		Role result = roleService.updateRole(1L, input);
		assertNotNull(result);
		assertEquals(org.una.progra3.healthy_life.entity.enums.RoleType.ADMIN, result.getName());
		assertEquals("updated", result.getDescription());
		assertFalse(result.isCanRead());
		assertTrue(result.isCanWrite());
		assertTrue(result.isCanDelete());
	}

	@Test
	void testAssignRoleToUserValid() {
		Role role = new Role();
		role.setId(2L);
		role.setName(org.una.progra3.healthy_life.entity.enums.RoleType.SUPERVISOR);
		User user = new User();
		user.setId(1L);
		Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		Mockito.when(roleRepository.findById(2L)).thenReturn(Optional.of(role));
		Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
		User result = roleService.assignRoleToUser(1L, 2L);
		assertNotNull(result);
		assertEquals(role, result.getRole());
	}

	@Test
	void testAssignRoleToUser_UserNotFound() {
		Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());
		RuntimeException ex = assertThrows(RuntimeException.class, () -> roleService.assignRoleToUser(99L, 1L));
		assertTrue(ex.getMessage().toLowerCase().contains("user not found"));
	}

	@Test
	void testAssignRoleToUser_RoleNotFound() {
		User user = new User();
		user.setId(1L);
		Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		// getRoleById() returns null when repository.findById is empty
		Mockito.when(roleRepository.findById(777L)).thenReturn(Optional.empty());
		RuntimeException ex = assertThrows(RuntimeException.class, () -> roleService.assignRoleToUser(1L, 777L));
		assertTrue(ex.getMessage().toLowerCase().contains("role not found"));
	}

	@Test
	void testCreateRoleInvalidName() {
		org.una.progra3.healthy_life.dtos.CreateRoleInput input = new org.una.progra3.healthy_life.dtos.CreateRoleInput();
		input.setName(null);
		assertThrows(IllegalArgumentException.class, () -> roleService.createRole(input));
	}

	@Test
	void testCreateRoleDuplicateName() {
		org.una.progra3.healthy_life.dtos.CreateRoleInput input = new org.una.progra3.healthy_life.dtos.CreateRoleInput();
		input.setName(org.una.progra3.healthy_life.entity.enums.RoleType.USER);
		Mockito.when(roleRepository.existsByName(input.getName())).thenReturn(true);
		assertThrows(RuntimeException.class, () -> roleService.createRole(input));
	}
	@Mock
	RoleRepository roleRepository;
	@InjectMocks
	RoleService roleService;

	@Test
	void testGetRoleById() {
		Role role = new Role();
		role.setId(1L);
		Mockito.when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
		Role result = roleService.getRoleById(1L);
		assertNotNull(result);
		assertEquals(1L, result.getId());
	}

	@Test
	void testGetRoleByIdNotFound() {
		Mockito.when(roleRepository.findById(2L)).thenReturn(Optional.empty());
		Role result = roleService.getRoleById(2L);
		assertNull(result);
	}

	@Test
	void testFindAll() {
		Role role = new Role();
		Mockito.when(roleRepository.findAll()).thenReturn(List.of(role));
		List<Role> result = roleService.getAllRoles();
		assertEquals(1, result.size());
	}

	@Test
	void testCreateRoleValid() {
		org.una.progra3.healthy_life.dtos.CreateRoleInput input = new org.una.progra3.healthy_life.dtos.CreateRoleInput();
		input.setName(org.una.progra3.healthy_life.entity.enums.RoleType.USER);
		input.setDescription("desc");
		input.setCanRead(true);
		input.setCanWrite(true);
		input.setCanDelete(false);
		Role role = new Role();
		role.setName(input.getName());
		role.setDescription(input.getDescription());
		role.setCanRead(input.isCanRead());
		role.setCanWrite(input.isCanWrite());
		role.setCanDelete(input.isCanDelete());
		Mockito.when(roleRepository.existsByName(input.getName())).thenReturn(false);
		Mockito.when(roleRepository.save(Mockito.any())).thenReturn(role);
		Role result = roleService.createRole(input);
		assertNotNull(result);
		assertEquals(org.una.progra3.healthy_life.entity.enums.RoleType.USER, result.getName());
		assertEquals("desc", result.getDescription());
		assertTrue(result.isCanRead());
		assertTrue(result.isCanWrite());
		assertFalse(result.isCanDelete());
	}

	@Test
	void testUpdateRole_NotFound() {
		org.una.progra3.healthy_life.dtos.UpdateRoleInput input = new org.una.progra3.healthy_life.dtos.UpdateRoleInput();
		Mockito.when(roleRepository.findById(123L)).thenReturn(Optional.empty());
		RuntimeException ex = assertThrows(RuntimeException.class, () -> roleService.updateRole(123L, input));
		assertTrue(ex.getMessage().toLowerCase().contains("role not found"));
	}

	@Test
	void testUpdateRole_AllFieldsNull_NoChanges() {
		// existing role with initial values
		Role role = new Role();
		role.setId(5L);
		role.setName(org.una.progra3.healthy_life.entity.enums.RoleType.USER);
		role.setDescription("desc");
		role.setCanRead(true);
		role.setCanWrite(false);
		role.setCanDelete(true);
		Mockito.when(roleRepository.findById(5L)).thenReturn(Optional.of(role));
		Mockito.when(roleRepository.save(Mockito.any())).thenAnswer(inv -> inv.getArgument(0));

		org.una.progra3.healthy_life.dtos.UpdateRoleInput input = new org.una.progra3.healthy_life.dtos.UpdateRoleInput();
		// all fields default to null in DTO
		Role result = roleService.updateRole(5L, input);
		assertNotNull(result);
		assertEquals(org.una.progra3.healthy_life.entity.enums.RoleType.USER, result.getName());
		assertEquals("desc", result.getDescription());
		assertTrue(result.isCanRead());
		assertFalse(result.isCanWrite());
		assertTrue(result.isCanDelete());
		Mockito.verify(roleRepository).save(Mockito.any());
	}

	@Test
	void testDeleteByIdExists() {
		Mockito.when(roleRepository.existsById(1L)).thenReturn(true);
		Mockito.doNothing().when(roleRepository).deleteById(1L);
		boolean result = roleService.deleteRole(1L);
		assertTrue(result);
	}

	@Test
	void testDeleteByIdNotExists() {
		Mockito.when(roleRepository.existsById(2L)).thenReturn(false);
		boolean result = roleService.deleteRole(2L);
		assertFalse(result);
	}
}
