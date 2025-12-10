package com.covoiturage.service;

import com.covoiturage.dto.UserDTO;
import com.covoiturage.model.User;
import com.covoiturage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * 注册新用户
     */
    @Transactional
    public UserDTO register(String email, String password, String firstName, 
                           String lastName, String phone) {
        
        // 1. 验证：检查email是否已存在
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        // 2. 验证：检查必填字段
        if (email == null || email.isEmpty()) {
            throw new RuntimeException("Email is required");
        }
        if (password == null || password.length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters");
        }
        if (firstName == null || firstName.isEmpty()) {
            throw new RuntimeException("First name is required");
        }
        if (lastName == null || lastName.isEmpty()) {
            throw new RuntimeException("Last name is required");
        }
        
        // 3. 创建User entity
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));  // BCrypt加密
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        user.setIsVerified(false);
        
        // 4. 保存到数据库
        User savedUser = userRepository.save(user);
        
        // 5. 转换成DTO返回
        return new UserDTO(savedUser);
    }
    
    /**
     * 用户登录
     */
    public UserDTO login(String email, String password) {
        // 1. 验证：检查必填字段
        if (email == null || email.isEmpty()) {
            throw new RuntimeException("Email is required");
        }
        if (password == null || password.isEmpty()) {
            throw new RuntimeException("Password is required");
        }
        
        // 2. 查找用户
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Email ou mot de passe incorrect"));
        
        // 3. 验证密码（使用BCrypt）
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }
        
        // 4. 返回用户信息
        return new UserDTO(user);
    }
    
    /**
     * 根据ID获取用户
     */
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserDTO(user);
    }
    
    /**
     * 根据Email获取用户
     */
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserDTO(user);
    }
    
    /**
     * 更新用户资料
     */
    @Transactional
    public UserDTO updateProfile(Long userId, String firstName, String lastName, String phone, String bio) {
        
        // 1. 查找用户
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // 2. 验证必填字段
        if (firstName == null || firstName.isEmpty()) {
            throw new RuntimeException("First name is required");
        }
        if (lastName == null || lastName.isEmpty()) {
            throw new RuntimeException("Last name is required");
        }
        
        // 3. 更新字段
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        user.setBio(bio);
        
        // 4. 保存
        User updatedUser = userRepository.save(user);
        
        // 5. 更新Session中的用户信息
        return new UserDTO(updatedUser);
    }
    
    /**
     * 更新用户信息 - 从DTO更新
     */
    @Transactional
    public UserDTO updateUser(UserDTO userDTO) {
        
        // 1. 查找用户
        User user = userRepository.findById(userDTO.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // 2. 验证必填字段
        if (userDTO.getFirstName() == null || userDTO.getFirstName().isEmpty()) {
            throw new RuntimeException("First name is required");
        }
        if (userDTO.getLastName() == null || userDTO.getLastName().isEmpty()) {
            throw new RuntimeException("Last name is required");
        }
        if (userDTO.getEmail() == null || userDTO.getEmail().isEmpty()) {
            throw new RuntimeException("Email is required");
        }
        
        // 3. 检查邮箱是否已被其他用户使用
        if (!user.getEmail().equals(userDTO.getEmail()) && userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // 4. 更新字段
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setBio(userDTO.getBio());
        
        // 5. 保存
        User updatedUser = userRepository.save(user);
        
        return new UserDTO(updatedUser);
    }
}
