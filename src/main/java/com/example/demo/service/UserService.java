package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.entity.User.UserStatus;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 创建新用户
     */
    User createUser(User user);
    
    /**
     * 根据ID查找用户
     */
    Optional<User> findById(Long id);
    
    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 根据用户名或邮箱查找用户
     */
    Optional<User> findByUsernameOrEmail(String identifier);
    
    /**
     * 更新用户信息
     */
    User updateUser(User user);
    
    /**
     * 删除用户
     */
    void deleteUser(Long id);
    
    /**
     * 获取所有用户
     */
    List<User> getAllUsers();
    
    /**
     * 根据状态获取用户列表
     */
    List<User> getUsersByStatus(UserStatus status);
    
    /**
     * 根据全名模糊查询用户
     */
    List<User> searchUsersByName(String name);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 更新用户状态
     */
    User updateUserStatus(Long id, UserStatus status);
    
    /**
     * 统计活跃用户数量
     */
    long countActiveUsers();
    
    /**
     * 验证用户密码
     */
    boolean validatePassword(String username, String password);
}