package com.example.volunteer_platform.entity;

import com.example.volunteer_platform.enums.RoleType;
import com.example.volunteer_platform.enums.VolunteerLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    private RoleType currentRole = RoleType.VOLUNTEER;

    private String pincode;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    private boolean activated = false;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Otp otp;

    @Lob
    private String contactDetails;
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] photo;
    private String gender;

    @Column(columnDefinition = "TEXT")
    private String skills;

    private Integer volunteerPoints = 0;
    @Enumerated(EnumType.STRING)
    private VolunteerLevel volunteerLevel = VolunteerLevel.BRONZE;

    @Column(columnDefinition = "TEXT")
    private String availabilityCalendar;
}
