package com.example.volunteer_platform.controller;

import com.example.volunteer_platform.dto.CountryDTO;
import com.example.volunteer_platform.dto.DistrictDTO;
import com.example.volunteer_platform.dto.PincodeDTO;
import com.example.volunteer_platform.dto.StateDTO;
import com.example.volunteer_platform.service.PincodeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/locations")
public class PincodeController {

    @Autowired
    private PincodeService pincodeService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/countries")
    public ResponseEntity<List<CountryDTO>> getAllCountries() {
        List<CountryDTO> countryDTOs = pincodeService.getCountryByCountryName("India").stream()
                .map(country -> modelMapper.map(country, CountryDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(countryDTOs);
    }

    @GetMapping("/countries/{countryId}/states")
    public ResponseEntity<List<StateDTO>> getStatesByCountry(@PathVariable Long countryId) {
        List<StateDTO> stateDTOs = pincodeService.getStatesByCountryId(countryId).stream()
                .map(state -> modelMapper.map(state, StateDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(stateDTOs);
    }

    @GetMapping("/states/{stateId}/districts")
    public ResponseEntity<List<DistrictDTO>> getDistrictsByState(@PathVariable Long stateId) {
        List<DistrictDTO> districtDTOs = pincodeService.getDistrictsByStateId(stateId).stream()
                .map(district -> modelMapper.map(district, DistrictDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(districtDTOs);
    }

    @GetMapping("/districts/{districtId}/pincodes")
    public ResponseEntity<List<PincodeDTO>> getPincodesByDistrict(@PathVariable Long districtId) {
        List<PincodeDTO> pincodeDTOs = pincodeService.getPincodesByDistrictId(districtId).stream()
                .map(pincode -> modelMapper.map(pincode, PincodeDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(pincodeDTOs);
    }

    @GetMapping("/pincodes/{pincodeValue}")
    public ResponseEntity<PincodeDTO> getPincodeDetails(@PathVariable String pincodeValue) {
        return pincodeService.getPincodeByPincodeValue(pincodeValue)
                .map(pincode -> ResponseEntity.ok(modelMapper.map(pincode, PincodeDTO.class)))
                .orElse(ResponseEntity.notFound().build());
    }
}