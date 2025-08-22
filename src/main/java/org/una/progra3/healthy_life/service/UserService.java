package org.una.progra3.healthy_life.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.una.progra3.healthy_life.entity.User;
import org.una.progra3.healthy_life.repository.UserRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
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
            System.out.println("[LOGIN] Usuario no encontrado para email: " + email);
            return null;
        }
        if (!user.getPassword().equals(password)) {
            System.out.println("[LOGIN] Contraseña incorrecta para email: " + email);
            return null;
        }
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
        if (password != null) existing.setPassword(password);

        return userRepository.save(existing);
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
