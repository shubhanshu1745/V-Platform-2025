package com.example.volunteer_platform.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PincodeDTO {
    private Long pincodeId;
    private String pincode;
    private String officeName;
    private String officeType;
    private String delivery;
    private Long districtId;
    private Double latitude;
    private Double longitude;
}