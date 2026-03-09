package com.clark.roper.Dispatch.service;

import com.clark.roper.Dispatch.entity.User;
import com.clark.roper.Dispatch.enums.UserRolesEnum;
import com.clark.roper.Dispatch.enums.UserStatus;
import com.clark.roper.Dispatch.exception.BadRequestException;
import com.clark.roper.Dispatch.exception.ResourceNotFoundException;
import com.clark.roper.Dispatch.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin-only operations
 */
@Service
@AllArgsConstructor
public class AdminService {

  private static final int MAX_PAGE_SIZE = 100;

  private final UserRepository userRepository;

  //Get all users

  public Page<AdminUserView> getAllUsers(int page, int size) {
    return userRepository.findAll(PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE)))
        .map(this::toAdminView);
  }


     // Change a user's role

  public String changeUserRole(Long userId, UserRolesEnum newRole) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
    user.setRole(newRole);
    userRepository.save(user);
    return "User " + user.getUsername() + " role changed to " + newRole.name();
  }


   //Change a user's status

  public String changeUserStatus(Long userId, UserStatus newStatus) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
    user.setStatus(newStatus);
    userRepository.save(user);
    return "User " + user.getUsername() + " status changed to " + newStatus.name();
  }

  private AdminUserView toAdminView(User user) {
    AdminUserView view = new AdminUserView();
    view.id = user.getId();
    view.username = user.getUsername();
    view.email = user.getEmail();
    view.role = user.getRole();
    view.status = user.getStatus();
    view.created = user.getCreated() != null ? user.getCreated().toString() : null;
    return view;
  }


  public static class AdminUserView {
    public Long id;
    public String username;
    public String email;
    public UserRolesEnum role;
    public UserStatus status;
    public String created;
  }
}
