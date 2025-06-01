package com.example.volunteer_platform.service;

import com.example.volunteer_platform.config.JwtTokenProvider;
import com.example.volunteer_platform.config.UserDetailsServiceImpl;
import com.example.volunteer_platform.dto.ProfileUpdateRequestDTO;
import com.example.volunteer_platform.dto.UserDTO;
import com.example.volunteer_platform.entity.Role;
import com.example.volunteer_platform.entity.User;
import com.example.volunteer_platform.enums.RoleType;
import com.example.volunteer_platform.enums.VolunteerLevel;
import com.example.volunteer_platform.exception.ResourceNotFoundException;
import com.example.volunteer_platform.repository.RoleRepository;
import com.example.volunteer_platform.repository.UserRepository;
//import com.example.volunteer_platform.repository.UserRoleRepository; // No longer needed
import jakarta.persistence.criteria.Predicate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private RoleRepository roleRepository;
    //@Autowired // No longer needed
    //private UserRoleRepository userRoleRepository;

    private static final int SILVER_LEVEL_POINTS = 100;
    private static final int GOLD_LEVEL_POINTS = 300;
    private static final int PLATINUM_LEVEL_POINTS = 700;

    @Transactional(readOnly = true)
    public UserDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        UserDTO userDTO = convertToUserDTOWithPhoto(user);
        return userDTO;
    }

    private UserDTO convertToUserDTOWithPhoto(User user) {
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        userDTO.setPhotoBase64(encodePhotoToBase64(user.getPhoto()));
        return userDTO;
    }

    private String encodePhotoToBase64(byte[] photoBytes) {
        if (photoBytes != null && photoBytes.length > 0) {
            return Base64.getEncoder().encodeToString(photoBytes);
        }
        return null;
    }

    @Transactional
    public String switchRole(String userEmail, RoleType roleType) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Set the new current role.
        user.setCurrentRole(roleType);

        // Get the Role entity.  Important: get the *managed* entity from the repository.
        Role newRole = roleRepository.findByRoleName(roleType);
        if (newRole == null) {
            throw new ResourceNotFoundException("Role not found: " + roleType);
        }

        // Update the roles set.  Clear and add.
        user.getRoles().clear();
        user.getRoles().add(newRole);

        // Save the user.  The @ManyToMany mapping handles the user_roles table.
        userRepository.save(user);

        // Generate a new token.
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(userEmail);
        return jwtTokenProvider.generateToken(userDetails);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> searchUsers(String pincode, String query) {
        Specification<User> spec = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (pincode != null && !pincode.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("pincode"), pincode));
            }
            if (query != null && !query.isEmpty()) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + query.toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + query.toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + query.toLowerCase() + "%")
                ));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        List<User> users = userRepository.findAll(spec);

        return users.stream()
                .map(this::convertToUserDTOWithPhoto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserDTO> searchVolunteersForOrganizer(String skillsFilter, String pincode) {
        Specification<User> spec = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("currentRole"), RoleType.VOLUNTEER));

            if (pincode != null && !pincode.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("pincode"), pincode));
            }
            if (skillsFilter != null && !skillsFilter.isEmpty()) {
                String[] volunteerSkills = skillsFilter.toLowerCase().split(",");
                predicates.add(buildSkillPredicate(root, criteriaBuilder, volunteerSkills));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        List<User> users = userRepository.findAll(spec);

        return users.stream()
                .map(this::convertToUserDTOWithPhoto)
                .collect(Collectors.toList());
    }

    private Predicate buildSkillPredicate(jakarta.persistence.criteria.Root<User> root, jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder, String[] volunteerSkills) {
        if (volunteerSkills == null || volunteerSkills.length == 0) {
            return criteriaBuilder.conjunction();
        }
        List<Predicate> skillPredicates = new ArrayList<>();
        for (String volunteerSkill : volunteerSkills) {
            skillPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("skills")), "%" + volunteerSkill.trim().toLowerCase() + "%"));
        }
        return criteriaBuilder.or(skillPredicates.toArray(new Predicate[0]));
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Transactional
    public UserDTO updateUserProfile(String userEmail, ProfileUpdateRequestDTO updateRequestDTO) throws IOException {

        User existingUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        modelMapper.map(updateRequestDTO, existingUser);

        if (updateRequestDTO.getSkills() != null) {
            existingUser.setSkills(updateRequestDTO.getSkills());
        }
        if (updateRequestDTO.getAvailabilityCalendar() != null) {
            existingUser.setAvailabilityCalendar(updateRequestDTO.getAvailabilityCalendar());
        }

        MultipartFile photoFile = updateRequestDTO.getPhotoFile();
        if (photoFile != null && !photoFile.isEmpty()) {
            existingUser.setPhoto(photoFile.getBytes());
        }

        LocalDateTime startTime = LocalDateTime.now();
        userRepository.save(existingUser);
        LocalDateTime endTime = LocalDateTime.now();
        long durationMs = java.time.Duration.between(startTime, endTime).toMillis();
        log.info("updateUserProfile execution time: {}ms", durationMs);

        return convertToUserDTOWithPhoto(existingUser);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return convertToUserDTOWithPhoto(user);
    }

    @Transactional
    public UserDTO awardPointsToVolunteer(Long userId, int points) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer not found with id: " + userId));

        int currentPoints = user.getVolunteerPoints();
        user.setVolunteerPoints(currentPoints + points);

        updateVolunteerLevel(user);

        user = userRepository.save(user);
        return convertToUserDTOWithPhoto(user);
    }

    private void updateVolunteerLevel(User user) {
        int points = user.getVolunteerPoints();
        VolunteerLevel currentLevel = user.getVolunteerLevel();
        VolunteerLevel newLevel = currentLevel;

        if (points >= PLATINUM_LEVEL_POINTS) {
            newLevel = VolunteerLevel.PLATINUM;
        } else if (points >= GOLD_LEVEL_POINTS) {
            newLevel = VolunteerLevel.GOLD;
        } else if (points >= SILVER_LEVEL_POINTS) {
            newLevel = VolunteerLevel.SILVER;
        }

        if (newLevel != currentLevel) {
            user.setVolunteerLevel(newLevel);
            log.info("Volunteer level updated for user {} to {}", user.getUserId(), newLevel);
        }
    }
}