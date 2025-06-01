package com.example.volunteer_platform.service;

import com.example.volunteer_platform.config.JwtTokenProvider;
import com.example.volunteer_platform.config.UserDetailsServiceImpl;
import com.example.volunteer_platform.dto.AuthRequestDTO;
import com.example.volunteer_platform.dto.OtpVerificationRequestDTO;
import com.example.volunteer_platform.dto.UserDTO;
import com.example.volunteer_platform.entity.Otp;
import com.example.volunteer_platform.entity.Role;
import com.example.volunteer_platform.entity.User;
//import com.example.volunteer_platform.entity.UserRole; // No longer needed
import com.example.volunteer_platform.enums.RoleType;
import com.example.volunteer_platform.exception.DuplicateResourceException;
import com.example.volunteer_platform.exception.GeneralException;
import com.example.volunteer_platform.exception.InvalidOtpException;
import com.example.volunteer_platform.exception.ResourceNotFoundException;
import com.example.volunteer_platform.repository.OtpRepository;
import com.example.volunteer_platform.repository.RoleRepository;
import com.example.volunteer_platform.repository.UserRepository;
//import com.example.volunteer_platform.repository.UserRoleRepository; // No longer needed
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private OtpRepository otpRepository;
    //@Autowired // No longer needed
    //private UserRoleRepository userRoleRepository;

    private final Random random = new Random();
    private static final int OTP_EXPIRY_MINUTES = 10;

    @Transactional
    public UserDTO registerUser(AuthRequestDTO authRequestDTO) {
        if (userRepository.existsByEmail(authRequestDTO.getEmail())) {
            throw new DuplicateResourceException("Email already registered.");
        }

        User user = modelMapper.map(authRequestDTO, User.class);
        user.setPassword(passwordEncoder.encode(authRequestDTO.getPassword()));

        // Get or create the VOLUNTEER role.
        Role volunteerRole = roleRepository.findByRoleName(RoleType.VOLUNTEER);
        if (volunteerRole == null) {
            volunteerRole = new Role();
            volunteerRole.setRoleName(RoleType.VOLUNTEER);
            volunteerRole = roleRepository.save(volunteerRole); // Save and get the managed entity
        }

        // Add the VOLUNTEER role to the user's roles set.
        user.getRoles().add(volunteerRole);
        user.setCurrentRole(RoleType.VOLUNTEER); // Set the current role.

        try {
            // Save the user.  The @ManyToMany mapping will *automatically* handle the user_roles table.
            user = userRepository.save(user);
            logger.debug("registerUser: User saved successfully: {}", user.getEmail());

        } catch (DataAccessException e) {
            logger.error("registerUser: DataAccessException during save operations: ", e);
            throw new GeneralException("Error during user registration: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("registerUser: Exception during save operations: ", e);
            throw new GeneralException("Error during user registration: " + e.getMessage(), e);
        }

        // Generate and send OTP (same as before).
        String otpValue = generateOtp();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

        Otp otp = new Otp();
        otp.setUser(user);
        otp.setOtpValue(otpValue);
        otp.setExpiryTime(expiryTime);

        otpRepository.save(otp);
        emailService.sendOtpEmail(user.getEmail(), otpValue);
        return modelMapper.map(user, UserDTO.class);
    }

    private String generateOtp() {
        return String.format("%06d", random.nextInt(1000000));
    }

    @Transactional
    public String verifyOtp(OtpVerificationRequestDTO otpVerificationRequestDTO) {
        User user = userRepository.findByEmail(otpVerificationRequestDTO.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Optional<Otp> otpOptional = otpRepository.findByUser(user);
        if (!otpOptional.isPresent()) {
            throw new InvalidOtpException("OTP not found for this user. Please register or request a new OTP.");
        }

        Otp otp = otpOptional.get();

        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otp);
            throw new InvalidOtpException("OTP expired. Please request a new OTP.");
        }

        if (!otp.getOtpValue().equals(otpVerificationRequestDTO.getOtp())) {
            throw new InvalidOtpException("Invalid OTP.");
        }

        user.setActivated(true);
        userRepository.save(user);
        otpRepository.delete(otp);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        return jwtTokenProvider.generateToken(userDetails);
    }

    @Transactional
    public String loginUser(AuthRequestDTO authRequestDTO) {
        try {
            // 1. Authenticate the user.
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequestDTO.getEmail(),
                            authRequestDTO.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 2. Fetch the user entity.
            User user = userRepository.findByEmail(authRequestDTO.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // 3. Reset the role to VOLUNTEER.
            Role volunteerRole = roleRepository.findByRoleName(RoleType.VOLUNTEER);
            if (volunteerRole == null) {
                // This should ideally never happen if the database is initialized correctly.
                volunteerRole = new Role();
                volunteerRole.setRoleName(RoleType.VOLUNTEER);
                volunteerRole = roleRepository.save(volunteerRole); // Ensure it's managed.
            }
            user.setCurrentRole(RoleType.VOLUNTEER); // Set current role.
            user.getRoles().clear();          // Clear existing roles.
            user.getRoles().add(volunteerRole); // Add the VOLUNTEER role.

            // 4. Save the changes to the user (including the role reset).
            userRepository.save(user);

            // 5. Generate a new JWT token (with the VOLUNTEER role).
            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequestDTO.getEmail());
            String token = jwtTokenProvider.generateToken(userDetails);
            logger.info("Generated token for user '{}' with roles: {}", authRequestDTO.getEmail(), userDetails.getAuthorities());
            return token;

        } catch (org.springframework.security.core.AuthenticationException e) {
            logger.error("Authentication failed for user: {}", authRequestDTO.getEmail(), e);
            return HttpStatus.UNAUTHORIZED.getReasonPhrase(); // Or throw an exception
        } catch (Exception e) {
            logger.error("An error occurred during login for user: {}", authRequestDTO.getEmail(), e);
            return HttpStatus.UNAUTHORIZED.getReasonPhrase(); // Or throw an exception
        }
    }


    @Transactional
    public ResponseEntity<Map<String, String>> switchRole(RoleType roleType) {
        logger.info("AuthService.switchRole - START, requested role: {}", roleType);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        logger.info("AuthService.switchRole - User found: {}, currentRole before switch: {}", user.getEmail(), user.getCurrentRole());

        // Set the new current role.
        user.setCurrentRole(roleType);

        // Clear existing roles and add the new role.
        Role newRole = roleRepository.findByRoleName(roleType);
        if (newRole == null) {
            throw new ResourceNotFoundException("Role not found: " + roleType);
        }
        user.getRoles().clear(); // Clear existing roles.
        user.getRoles().add(newRole); // Add the new role.

        // Save the user.  The @ManyToMany mapping will handle the user_roles table.
        userRepository.save(user);

        logger.info("AuthService.switchRole - Roles updated to: {}", user.getRoles());

        // Reload UserDetails and generate a new token.
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
        String newToken = jwtTokenProvider.generateToken(userDetails);
        logger.info("AuthService.switchRole - New JWT Token generated with role '{}': {}", roleType, newToken);

        Map<String, String> response = new HashMap<>();
        response.put("token", newToken);
        return ResponseEntity.ok(response);
    }
}