package com.example.volunteer_platform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pincodes")
public class Pincode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pincodeId;

    @Column(unique = true, nullable = false)
    private String pincode; // Pincode value

    private String officeName;
    private String officeType;
    private String delivery;

    @ManyToOne
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    private Double latitude;
    private Double longitude;
}