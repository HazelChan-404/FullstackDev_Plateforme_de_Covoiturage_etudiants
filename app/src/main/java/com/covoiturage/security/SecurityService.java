package com.covoiturage.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.covoiturage.dto.UserDTO;
import com.covoiturage.service.UserService;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinSession;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class SecurityService {
    
    @Autowired
    private UserService userService;
    
    /**
     * Get the currently authenticated user
     */
    public UserDTO getAuthenticatedUser() {
        return VaadinSession.getCurrent().getAttribute(UserDTO.class);
    }
    
    /**
     * Get the currently authenticated user ID
     */
    public Long getAuthenticatedUserId() {
        UserDTO user = getAuthenticatedUser();
        return user != null ? user.getId() : null;
    }
    
    /**
     * Set the authenticated user in session
     */
    public void setAuthenticatedUser(UserDTO user) {
        VaadinSession.getCurrent().setAttribute(UserDTO.class, user);
    }
    
    /**
     * Clear the authenticated user from session (logout)
     */
    public void logout() {
        VaadinSession.getCurrent().setAttribute(UserDTO.class, null);
        VaadinSession.getCurrent().close();
    }
    
    /**
     * Check if user is authenticated
     */
    public boolean isAuthenticated() {
        return getAuthenticatedUser() != null;
    }
    
    /**
     * Authenticate user with email and password
     */
    public UserDTO authenticate(String email, String password) {
        try {
            UserDTO user = userService.login(email, password);
            setAuthenticatedUser(user);
            return user;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Check if the authenticated user is the owner of the resource
     */
    public boolean isOwner(Long resourceUserId) {
        UserDTO currentUser = getAuthenticatedUser();
        return currentUser != null && currentUser.getId().equals(resourceUserId);
    }
    
    /**
     * Check if the authenticated user has admin privileges
     * Note: This is a basic implementation. You may want to add roles to User entity
     */
    public boolean isAdmin() {
        UserDTO currentUser = getAuthenticatedUser();
        return currentUser != null && Boolean.TRUE.equals(currentUser.getIsVerified());
        // This is a placeholder - you might want to add an isAdmin field to User entity
    }
    
    /**
     * Get client IP address
     */
    public String getClientIpAddress() {
        try {
            HttpServletRequest request = VaadinServletRequest.getCurrent().getHttpServletRequest();
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0].trim();
            }
            String xRealIp = request.getHeader("X-Real-IP");
            if (xRealIp != null && !xRealIp.isEmpty()) {
                return xRealIp;
            }
            return request.getRemoteAddr();
        } catch (Exception e) {
            return "unknown";
        }
    }
    
    /**
     * Check if user has permission to access a resource
     */
    public boolean hasPermission(Long resourceUserId) {
        return isOwner(resourceUserId) || isAdmin();
    }
}
