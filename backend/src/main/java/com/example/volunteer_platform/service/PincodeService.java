package com.example.volunteer_platform.service;

import com.example.volunteer_platform.entity.Country;
import com.example.volunteer_platform.entity.District;
import com.example.volunteer_platform.entity.Pincode;
import com.example.volunteer_platform.entity.State;
import com.example.volunteer_platform.entity.User;
import com.example.volunteer_platform.enums.RoleType;
import com.example.volunteer_platform.exception.GeneralException;
import com.example.volunteer_platform.repository.CountryRepository;
import com.example.volunteer_platform.repository.DistrictRepository;
import com.example.volunteer_platform.repository.PincodeRepository;
import com.example.volunteer_platform.repository.StateRepository;
import com.example.volunteer_platform.repository.UserRepository;
import com.example.volunteer_platform.util.Haversine;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PincodeService {

    private static final Logger logger = LoggerFactory.getLogger(PincodeService.class);

    @Autowired
    private PincodeRepository pincodeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private StateRepository stateRepository;
    @Autowired
    private DistrictRepository districtRepository;

    @Transactional
    public void loadPincodesFromCSV(MultipartFile file) {
        logger.info("Starting loadPincodesFromCSV (Location Data - Assuming India, Corrected District-State Association)...");
        try (InputStreamReader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim().withIgnoreEmptyLines())) {

            logger.info("Detected CSV Headers: {}", csvParser.getHeaderNames());

            Set<String> seenPincodes = new HashSet<>();
            List<State> statesToSave = new ArrayList<>();
            List<District> districtsToSave = new ArrayList<>();
            List<Pincode> pincodesToSave = new ArrayList<>();

            Country indiaCountry = countryRepository.findByCountryName("India").orElseGet(() -> {
                Country newCountry = new Country();
                newCountry.setCountryName("India");
                newCountry.setCountryCode("IN");
                return countryRepository.save(newCountry);
            });

            for (CSVRecord csvRecord : csvParser) {
                String pincodeValue = null;
                try {
                    pincodeValue = csvRecord.get("pincode").trim();
                    if (!seenPincodes.add(pincodeValue)) {
                        continue;
                    }

                    String stateName = csvRecord.get("statename");
                    String districtName = csvRecord.get("district");

                    if (districtName == null || districtName.trim().isEmpty()) {
                        logger.warn("Skipping pincode record due to missing district name. Pincode: {}", pincodeValue);
                        continue;
                    }

                    State state = stateRepository.findByStateName(stateName).orElseGet(() -> {
                        State newState = new State();
                        newState.setStateName(stateName);
                        newState.setCountry(indiaCountry);
                        return stateRepository.save(newState);
                    });

                    District district = districtRepository.findByDistrictName(districtName).orElseGet(() -> {
                        District newDistrict = new District();
                        newDistrict.setDistrictName(districtName);
                        newDistrict.setState(state);
                        return districtRepository.save(newDistrict);
                    });

                    Pincode pincode = new Pincode();
                    pincode.setPincode(pincodeValue);
                    pincode.setOfficeName(csvRecord.get("officename"));
                    pincode.setOfficeType(csvRecord.get("officetype"));
                    pincode.setDelivery(csvRecord.get("delivery"));
                    pincode.setDistrict(district);
                    try {
                        String latitudeStr = csvRecord.get("latitude").trim();
                        String longitudeStr = csvRecord.get("longitude").trim();
                        if (!"NA".equalsIgnoreCase(latitudeStr) && !"NA".equalsIgnoreCase(longitudeStr)) {
                            pincode.setLatitude(Double.parseDouble(latitudeStr));
                            pincode.setLongitude(Double.parseDouble(longitudeStr));
                        }
                    } catch (NumberFormatException e) {
                        pincode.setLatitude(null);
                        pincode.setLongitude(null);
                    }
                    pincodesToSave.add(pincode);

                } catch (IllegalArgumentException e) {
                    logger.warn("Error processing CSV record: {} Pincode (if available): {}", e.getMessage(), pincodeValue);
                }
            }

            if (!pincodesToSave.isEmpty()) {
                List<Pincode> trulyNewPincodes = new ArrayList<>();
                for (Pincode p : pincodesToSave) {
                    if (!pincodeRepository.findByPincode(p.getPincode()).isPresent()) {
                        trulyNewPincodes.add(p);
                    } else {
                        logger.debug("Skipping pincode already in DB: {}", p.getPincode());
                    }
                }
                if(!trulyNewPincodes.isEmpty()) {
                    pincodeRepository.saveAll(trulyNewPincodes);
                    logger.info("Successfully loaded {} unique pincode records from CSV.", trulyNewPincodes.size());
                } else {
                    logger.info("No new unique pincodes to save.");
                }
            } else {
                logger.info("No new unique pincodes found in CSV.");
            }

        } catch (IOException e) {
            logger.error("Failed to load location data from CSV: {}", e.getMessage());
            throw new GeneralException("Failed to load location data from CSV: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<User> findNearbyVolunteers(String eventPincodeStr, double radiusKm) {
        Pincode eventPincode = pincodeRepository.findByPincode(eventPincodeStr)
                .orElse(null);

        if (eventPincode == null || eventPincode.getLatitude() == null || eventPincode.getLongitude() == null) {
            return new ArrayList<>();
        }

        List<User> allVolunteers = userRepository.findByRoles_RoleName(RoleType.VOLUNTEER);
        List<User> nearbyVolunteers = new ArrayList<>();

        for (User volunteer : allVolunteers) {
            if (volunteer.getPincode() != null) {
                Pincode volunteerPincode = pincodeRepository.findByPincode(volunteer.getPincode()).orElse(null);
                if (volunteerPincode != null && volunteerPincode.getLatitude() != null && volunteerPincode.getLongitude() != null) {
                    double distance = Haversine.distance(
                            eventPincode.getLatitude(), eventPincode.getLongitude(),
                            volunteerPincode.getLatitude(), volunteerPincode.getLongitude()
                    );
                    if (distance <= radiusKm) {
                        nearbyVolunteers.add(volunteer);
                    }
                }
            }
        }
        return nearbyVolunteers;
    }

    @Transactional(readOnly = true)
    public Optional<Pincode> getPincodeByPincodeValue(String pincode) {
        return pincodeRepository.findByPincode(pincode);
    }

    @Transactional(readOnly = true)
    public Optional<State> getStateByStateName(String stateName) {
        return stateRepository.findByStateName(stateName);
    }

    @Transactional(readOnly = true)
    public Optional<District> getDistrictByDistrictName(String districtName) {
        return districtRepository.findByDistrictName(districtName);
    }

    @Transactional(readOnly = true)
    public Optional<Country> getCountryByCountryName(String countryName) {
        return countryRepository.findByCountryName(countryName);
    }

    @Transactional(readOnly = true)
    public List<State> getStatesByCountryId(Long countryId) {
        return stateRepository.findByCountry_CountryId(countryId);
    }

    @Transactional(readOnly = true)
    public List<District> getDistrictsByStateId(Long stateId) {
        return districtRepository.findByState_StateId(stateId);
    }

    @Transactional(readOnly = true)
    public List<Pincode> getPincodesByDistrictId(Long districtId) {
        return pincodeRepository.findByDistrict_DistrictId(districtId);
    }
}