package com.prashanth.taskmanagementsystem.service;

import com.prashanth.taskmanagementsystem.exception.CustomJwtException;
import com.prashanth.taskmanagementsystem.exception.InvalidRoleTypeException;
import com.prashanth.taskmanagementsystem.exception.RoleNotFoundException;
import com.prashanth.taskmanagementsystem.exception.UserAlreadyExistsException;
import com.prashanth.taskmanagementsystem.model.dto.UserDTO;
import com.prashanth.taskmanagementsystem.model.entity.Role;
import com.prashanth.taskmanagementsystem.model.entity.User;
import com.prashanth.taskmanagementsystem.model.enums.RoleType;
import com.prashanth.taskmanagementsystem.model.mapper.UserMapper;
import com.prashanth.taskmanagementsystem.repository.RoleRepository;
import com.prashanth.taskmanagementsystem.repository.UserRepository;
import com.prashanth.taskmanagementsystem.request.SignInAuthRequest;
import com.prashanth.taskmanagementsystem.request.SignUpUserRequest;
import com.prashanth.taskmanagementsystem.response.SignInAuthResponse;
import com.prashanth.taskmanagementsystem.response.SignUpUserResponse;
import com.prashanth.taskmanagementsystem.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing users
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Retrieves all users from the database
     * @return List of all users.
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserMapper::toUserDTO).collect(Collectors.toList());
    }

    /**
     * Authenticates a user and provides a JWT token.
     * @param signInAuthRequest the sign-in request containing email and password
     * @return the response containing the JWT token.
     */
    @Transactional
    public SignInAuthResponse signIn(SignInAuthRequest signInAuthRequest) {

        try {
            String email = signInAuthRequest.getEmail();
            String password = signInAuthRequest.getPassword();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomJwtException("User not found", HttpStatus.BAD_REQUEST));
            String token = jwtTokenProvider.createToken(email, user.getRoles());
            return new SignInAuthResponse(email, token);
        } catch (AuthenticationException e) {
            throw new CustomJwtException("Invalid email/password supplied", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Registers a new user in the system.
     * @param signUpUserRequest the sign-up request containing user details.
     * @return the response containing the registered user details
     */
    @Transactional
    public SignUpUserResponse signUp(SignUpUserRequest signUpUserRequest) {
        User user = UserMapper.toUser(signUpUserRequest);
        if (userRepository.existsByUsername(signUpUserRequest.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        if (userRepository.existsByEmail(signUpUserRequest.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(signUpUserRequest.getPassword()));
        try {
            Role role = roleRepository.findByRoleType(RoleType.valueOf(signUpUserRequest.getRoleType().toUpperCase()))
                    .orElseThrow(() -> new RoleNotFoundException("Role not found"));
            role.setRoleType(RoleType.valueOf(signUpUserRequest.getRoleType().toUpperCase()));
            role.setUser(user);
            user.setRoles(Collections.singletonList(role));
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleTypeException("Invalid role type: " + signUpUserRequest.getRoleType());
        }
        User savedUser = userRepository.save(user);
        return UserMapper.toUserResponse(savedUser);
    }
}
