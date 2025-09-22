package org.una.progra3.healthy_life.service;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.una.progra3.healthy_life.entity.User;
import org.una.progra3.healthy_life.entity.Habit;
import org.una.progra3.healthy_life.repository.UserRepository;
import java.util.Optional;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	@Test
	void testFindAllPaginated() {
		var pageInput = new org.una.progra3.healthy_life.dtos.PageInputDTO();
		pageInput.setPage(0);
		pageInput.setSize(2);
		pageInput.setSortBy("name");
		pageInput.setSortDirection("ASC");
		org.springframework.data.domain.Page<User> page = org.springframework.data.domain.Page.empty();
		Mockito.when(userRepository.findAll(Mockito.any(org.springframework.data.domain.Pageable.class))).thenReturn(page);
		var result = userService.findAllPaginated(pageInput);
		assertNotNull(result);
	}

	@Test
	void testCreatePageInfo() {
		User user = new User();
		user.setName("Test");
		org.springframework.data.domain.Page<User> page = new org.springframework.data.domain.PageImpl<>(List.of(user));
		var info = userService.createPageInfo(page);
		assertTrue(info.isHasNextPage() == page.hasNext());
		assertTrue(info.isHasPreviousPage() == page.hasPrevious());
		assertEquals(page.getTotalElements(), info.getTotalElements());
		assertEquals(page.getTotalPages(), info.getTotalPages());
		assertEquals(page.getNumber(), info.getCurrentPage());
		assertEquals(page.getSize(), info.getPageSize());
	}
	@Mock
	UserRepository userRepository;
	@Mock
	org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
	@InjectMocks
	UserService userService;

	@Test
	void testFindAll() {
		User user = new User();
		Mockito.when(userRepository.findAll()).thenReturn(List.of(user));
		List<User> result = userService.findAll();
		assertEquals(1, result.size());
	}

	@Test
	void testFindById() {
		User user = new User();
		user.setId(1L);
		Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		User result = userService.findById(1L);
		assertNotNull(result);
		assertEquals(1L, result.getId());
	}

	@Test
	void testFindByIdNotFound() {
		Mockito.when(userRepository.findById(2L)).thenReturn(Optional.empty());
		User result = userService.findById(2L);
		assertNull(result);
	}

	@Test
	void testFindByEmail() {
		User user = new User();
		user.setEmail("test@email.com");
		Mockito.when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));
		User result = userService.findByEmail("test@email.com");
		assertNotNull(result);
		assertEquals("test@email.com", result.getEmail());
	}

	@Test
	void testFindByEmailNotFound() {
		Mockito.when(userRepository.findByEmail("notfound@email.com")).thenReturn(Optional.empty());
		User result = userService.findByEmail("notfound@email.com");
		assertNull(result);
	}

	@Test
	void testLoginValid() {
		User user = new User();
		user.setEmail("test@email.com");
		user.setPassword("encoded");
		Mockito.when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));
		Mockito.when(passwordEncoder.matches("pass", "encoded")).thenReturn(true);
		User result = userService.login("test@email.com", "pass");
		assertNotNull(result);
	}

	@Test
	void testLoginInvalidEmail() {
		Mockito.when(userRepository.findByEmail("bad@email.com")).thenReturn(Optional.empty());
		User result = userService.login("bad@email.com", "pass");
		assertNull(result);
	}

	@Test
	void testLoginInvalidPassword() {
		User user = new User();
		user.setEmail("test@email.com");
		user.setPassword("encoded");
		Mockito.when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));
		Mockito.when(passwordEncoder.matches("badpass", "encoded")).thenReturn(false);
		User result = userService.login("test@email.com", "badpass");
		assertNull(result);
	}

	@Test
	void testSaveValid() {
		User user = new User();
		user.setEmail("test@email.com");
		user.setName("Test");
		user.setPassword("pass");
		Mockito.when(userRepository.existsByEmail("test@email.com")).thenReturn(false);
		Mockito.when(passwordEncoder.encode("pass")).thenReturn("encoded");
		Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
		User result = userService.save(user);
		assertNotNull(result);
	}

	@Test
	void testSaveInvalidEmail() {
		User user = new User();
		user.setEmail("");
		user.setName("Test");
		user.setPassword("pass");
		assertThrows(IllegalArgumentException.class, () -> userService.save(user));
	}

	@Test
	void testSaveInvalidName() {
		User user = new User();
		user.setEmail("test@email.com");
		user.setName("");
		user.setPassword("pass");
		assertThrows(IllegalArgumentException.class, () -> userService.save(user));
	}

	@Test
	void testSaveDuplicateEmail() {
		User user = new User();
		user.setEmail("test@email.com");
		user.setName("Test");
		user.setPassword("pass");
		Mockito.when(userRepository.existsByEmail("test@email.com")).thenReturn(true);
		assertThrows(RuntimeException.class, () -> userService.save(user));
	}

	@Test
	void testUpdateNotFound() {
		Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());
		assertThrows(RuntimeException.class, () -> userService.update(1L, "Nuevo", "nuevo@email.com", "pass"));
	}

	@Test
	void testUpdateValid() {
		User user = new User();
		user.setId(1L);
		user.setEmail("test@email.com");
		user.setName("Test");
		user.setPassword("pass");
		Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		Mockito.when(userRepository.existsByEmail("nuevo@email.com")).thenReturn(false);
		Mockito.when(passwordEncoder.encode("pass")).thenReturn("encoded");
		Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
		User result = userService.update(1L, "Nuevo", "nuevo@email.com", "pass");
		assertNotNull(result);
	}

	@Test
	void testUpdateDuplicateEmail() {
		User user = new User();
		user.setId(1L);
		user.setEmail("test@email.com");
		user.setName("Test");
		user.setPassword("pass");
		Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		Mockito.when(userRepository.existsByEmail("nuevo@email.com")).thenReturn(true);
		assertThrows(RuntimeException.class, () -> userService.update(1L, "Nuevo", "nuevo@email.com", "pass"));
	}

	@Test
	void testUpdateBlankPassword() {
		User user = new User();
		user.setId(1L);
		user.setEmail("test@email.com");
		user.setName("Test");
		user.setPassword("pass");
		Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		assertThrows(IllegalArgumentException.class, () -> userService.update(1L, "Nuevo", "nuevo@email.com", ""));
	}

	@Test
	void testUpdateNullPassword_NoEncoding() {
		User user = new User();
		user.setId(1L);
		user.setEmail("test@email.com");
		user.setName("Test");
		user.setPassword("pass");
		Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
		User result = userService.update(1L, "Nuevo", "test@email.com", null);
		assertNotNull(result);
		assertEquals("Nuevo", result.getName());
		assertEquals("test@email.com", result.getEmail());
	}

	@Test
	void testUpdateSameEmail_NoExistsCheck() {
		User user = new User();
		user.setId(1L);
		user.setEmail("same@email.com");
		user.setName("Test");
		user.setPassword("pass");
		Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
		User result = userService.update(1L, null, "same@email.com", null);
		assertNotNull(result);
		assertEquals("same@email.com", result.getEmail());
	}

	@Test
	void testToggleFavoriteAdd() {
		User user = new User();
		user.setId(1L);
		user.setFavoriteHabits(new java.util.HashSet<>());
		Habit habit = new Habit();
		Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
		User result = userService.toggleFavorite(1L, habit);
		assertTrue(result.getFavoriteHabits().contains(habit));
	}

	@Test
	void testToggleFavoriteRemove() {
		User user = new User();
		user.setId(1L);
		Habit habit = new Habit();
		user.setFavoriteHabits(new java.util.HashSet<>(List.of(habit)));
		Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
		User result = userService.toggleFavorite(1L, habit);
		assertFalse(result.getFavoriteHabits().contains(habit));
	}

	@Test
	void testToggleFavoriteUserNotFound() {
		Habit habit = new Habit();
		Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());
		assertThrows(RuntimeException.class, () -> userService.toggleFavorite(1L, habit));
	}

	@Test
	void testToggleFavoriteHabitNull() {
		User user = new User();
		user.setId(1L);
		user.setFavoriteHabits(new java.util.HashSet<>());
		Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		assertThrows(IllegalArgumentException.class, () -> userService.toggleFavorite(1L, null));
	}

	@Test
	void testDeleteByIdExists() {
		Mockito.when(userRepository.existsById(1L)).thenReturn(true);
		Mockito.doNothing().when(userRepository).deleteById(1L);
		Boolean result = userService.deleteById(1L);
		assertTrue(result);
	}

	@Test
	void testDeleteByIdNotExists() {
		Mockito.when(userRepository.existsById(2L)).thenReturn(false);
		Boolean result = userService.deleteById(2L);
		assertFalse(result);
	}
}
