package com.online.eShopping.service.impl;

import com.online.eShopping.dto.UserDTO;
import com.online.eShopping.dto.UserRegistrationDTO;
import com.online.eShopping.dto.UserUpdateDTO;
import com.online.eShopping.exception.DuplicateResourceException;
import com.online.eShopping.exception.ResourceNotFoundException;
import com.online.eShopping.mapper.UserMapper;
import com.online.eShopping.model.Role;
import com.online.eShopping.model.User;
import com.online.eShopping.repository.RoleRepository;
import com.online.eShopping.repository.UserRepository;
import com.online.eShopping.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDTO registerUser(UserRegistrationDTO registrationDTO) {
        log.info("Registering new user with email: {}", registrationDTO.getEmail());

        // Kontrolli, kas email on juba kasutuses
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new DuplicateResourceException("Email is already in use: " + registrationDTO.getEmail());
        }

        // Loo uus kasutaja
        User user = new User();
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setFirstName(registrationDTO.getFirstName());
        user.setLastName(registrationDTO.getLastName());
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Lisa kasutajale rollid
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));
        user.setRoles(new HashSet<>(Collections.singletonList(userRole)));

        // Salvesta kasutaja
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());

        return userMapper.toDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        log.info("Fetching user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Kontrolli, kas praegune kasutaja on administraator või sama kasutaja
        User currentUser = getCurrentUserEntity();
        if (!hasAdminRole(currentUser) && !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("You don't have permission to view this user");
        }

        return userMapper.toDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(int page, int size) {
        log.info("Fetching all users page {} with size {}", page, size);

        // Kontrolli, kas praegune kasutaja on administraator
        User currentUser = getCurrentUserEntity();
        if (!hasAdminRole(currentUser)) {
            throw new AccessDeniedException("Only administrators can view all users");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("email").ascending());
        Page<User> userPage = userRepository.findAll(pageable);

        return userPage.map(userMapper::toDTO);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        log.info("Updating user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Kontrolli, kas praegune kasutaja on administraator või sama kasutaja
        User currentUser = getCurrentUserEntity();
        if (!hasAdminRole(currentUser) && !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("You don't have permission to update this user");
        }

        // Uuenda andmeid
        if (userUpdateDTO.getFirstName() != null) {
            user.setFirstName(userUpdateDTO.getFirstName());
        }

        if (userUpdateDTO.getLastName() != null) {
            user.setLastName(userUpdateDTO.getLastName());
        }

        if (userUpdateDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
        }

        user.setUpdatedAt(LocalDateTime.now());

        // Salvesta uuendatud kasutaja
        User updatedUser = userRepository.save(user);
        log.info("User updated with id: {}", updatedUser.getId());

        return userMapper.toDTO(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser() {
        User currentUser = getCurrentUserEntity();
        return userMapper.toDTO(currentUser);
    }

    @Override
    @Transactional
    public UserDTO activateUser(Long id) {
        log.info("Activating user with id: {}", id);

        // Kontrolli, kas praegune kasutaja on administraator
        User currentUser = getCurrentUserEntity();
        if (!hasAdminRole(currentUser)) {
            throw new AccessDeniedException("Only administrators can activate users");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setEnabled(true);
        user.setUpdatedAt(LocalDateTime.now());

        User activatedUser = userRepository.save(user);
        log.info("User activated with id: {}", activatedUser.getId());

        return userMapper.toDTO(activatedUser);
    }

    @Override
    @Transactional
    public UserDTO deactivateUser(Long id) {
        log.info("Deactivating user with id: {}", id);

        // Kontrolli, kas praegune kasutaja on administraator
        User currentUser = getCurrentUserEntity();
        if (!hasAdminRole(currentUser)) {
            throw new AccessDeniedException("Only administrators can deactivate users");
        }

        // Kontrolli, kas kasutaja eksisteerib
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Kontrolli, et ei deaktiveeriks viimast administraatorit
        if (hasAdminRole(user)) {
            long adminCount = userRepository.countByRolesName("ROLE_ADMIN");
            if (adminCount <= 1) {
                throw new AccessDeniedException("Cannot deactivate the last administrator");
            }
        }

        user.setEnabled(false);
        user.setUpdatedAt(LocalDateTime.now());

        User deactivatedUser = userRepository.save(user);
        log.info("User deactivated with id: {}", deactivatedUser.getId());

        return userMapper.toDTO(deactivatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        Set<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                true,
                authorities
        );
    }
    /**
     * Abimeetod praeguse kasutaja leidmiseks.
     */
    private User getCurrentUserEntity() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username));
    }

    /**
     * Abimeetod administraator rolli kontrollimiseks.
     */
    private boolean hasAdminRole(User user) {
        return user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
    }
}
