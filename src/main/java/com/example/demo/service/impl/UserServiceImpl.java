package com.example.demo.service.impl;

import com.example.demo.entity.User;
import com.example.demo.entity.User.UserStatus;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    
    @Override
    public User createUser(User user) {
        log.info("Creating new user with username: {}", user.getUsername());
        
        // 检查用户名和邮箱是否已存在
        if (existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }
        if (existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }
        
        return userRepository.save(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsernameOrEmail(String identifier) {
        return userRepository.findByUsernameOrEmail(identifier);
    }
    
    @Override
    public User updateUser(User user) {
        log.info("Updating user with id: {}", user.getId());
        
        if (!userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("User not found with id: " + user.getId());
        }
        
        return userRepository.save(user);
    }
    
    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        
        userRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersByStatus(UserStatus status) {
        return userRepository.findByStatus(status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> searchUsersByName(String name) {
        return userRepository.findByFullNameContaining(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public User updateUserStatus(Long id, UserStatus status) {
        log.info("Updating user status for id: {} to {}", id, status);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        user.setStatus(status);
        return userRepository.save(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countActiveUsers() {
        return userRepository.countActiveUsers();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean validatePassword(String username, String password) {
        Optional<User> user = findByUsername(username);
        return user.isPresent() && user.get().getPassword().equals(password);
    }
}