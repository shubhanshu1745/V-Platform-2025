package com.example.volunteer_platform.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DistrictDTO {
    private Long districtId;
    private String districtName;
    private Long stateId;
    private List<PincodeDTO> pincodes;
}