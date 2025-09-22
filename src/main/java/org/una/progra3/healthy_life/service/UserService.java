package org.una.progra3.healthy_life.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.una.progra3.healthy_life.entity.User;
import org.una.progra3.healthy_life.entity.Habit;
import org.una.progra3.healthy_life.repository.UserRepository;
import org.una.progra3.healthy_life.dtos.PageInputDTO;
import org.una.progra3.healthy_life.dtos.PageInfoDTO;
import org.una.progra3.healthy_life.dtos.UserPagedResponseDTO;
import org.una.progra3.healthy_life.dtos.UserDTO;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Page<User> findAllPaginated(PageInputDTO pageInput) {
        Sort sort = pageInput.getSortDirection().equalsIgnoreCase("DESC") 
            ? Sort.by(pageInput.getSortBy()).descending()
            : Sort.by(pageInput.getSortBy()).ascending();
        
        Pageable pageable = PageRequest.of(pageInput.getPage(), pageInput.getSize(), sort);
        return userRepository.findAll(pageable);
    }

    public PageInfoDTO createPageInfo(Page<?> page) {
        return new PageInfoDTO(
            page.hasNext(),
            page.hasPrevious(),
            page.hasContent() ? String.valueOf(page.getContent().get(0).hashCode()) : null,
            page.hasContent() ? String.valueOf(page.getContent().get(page.getContent().size() - 1).hashCode()) : null,
            (int) page.getTotalElements(),
            page.getTotalPages(),
            page.getNumber(),
            page.getSize()
        );
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            System.out.println("Invalid credentials");
            return null;
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            System.out.println("Invalid credentials");
            return null;
        }
        System.out.println("Token: " + user.getAuthTokens());
        return user;
    }

    @Transactional
    public User save(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
    if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
    }

    @Transactional
    public User update(Long id, String name, String email, String password) {
        User existing = findById(id);
        if (existing == null) {
            throw new RuntimeException("User not found");
        }

        if (name != null) existing.setName(name);
        if (email != null && !email.equals(existing.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new RuntimeException("Email already exists");
            }
            existing.setEmail(email);
        }
        if (password != null) {
            if (password.isBlank()) {
                throw new IllegalArgumentException("Password cannot be blank");
            }
            existing.setPassword(passwordEncoder.encode(password));
        }

        return userRepository.save(existing);
    }

    @Transactional
    public User toggleFavorite(Long userId, Habit habit) {
        User user = findById(userId);
        if (user == null) throw new RuntimeException("User not found");
        if (habit == null) throw new IllegalArgumentException("Habit is required");
        if (user.getFavoriteHabits().contains(habit)) {
            user.getFavoriteHabits().remove(habit);
        } else {
            user.getFavoriteHabits().add(habit);
        }
        return userRepository.save(user);
    }

    @Transactional
    public Boolean deleteById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
