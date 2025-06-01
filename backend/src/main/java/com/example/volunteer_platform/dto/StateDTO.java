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
public class StateDTO {
    private Long stateId;
    private String stateName;
    private String stateCode;
    private Long countryId;
    private List<DistrictDTO> districts;
}