package com.example.volunteer_platform.util;

import com.example.volunteer_platform.service.PincodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import com.example.volunteer_platform.repository.CountryRepository;
import com.example.volunteer_platform.repository.StateRepository;
import com.example.volunteer_platform.repository.DistrictRepository;
import com.example.volunteer_platform.repository.PincodeRepository;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class CsvDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(CsvDataLoader.class);

    @Autowired
    private PincodeService pincodeService;
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private PincodeRepository pincodeRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private StateRepository stateRepository;
    @Autowired
    private DistrictRepository districtRepository;

    @Value("classpath:pincode_data.csv")
    Resource pincodeCsvResource;

    @Bean
    CommandLineRunner loadLocationData() {
        return args -> {
            if (countryRepository.count() == 0) {
                if (pincodeCsvResource.exists()) {
                    try (InputStream is = pincodeCsvResource.getInputStream()) {
                        MultipartFile multipartFile = new MockMultipartFile(
                                "pincode_data.csv",
                                "pincode_data.csv",
                                "text/csv",
                                is
                        );
                        pincodeService.loadPincodesFromCSV(multipartFile);
                        logger.info("Location data loading process initiated from CSV...");
                    } catch (IOException e) {
                        logger.error("Error loading location data from CSV: {}", e.getMessage());
                    }
                } else {
                    logger.warn("Pincode CSV data file not found at classpath:pincode_data.csv. Skipping import.");
                }
            } else {
                logger.info("Location data tables are already populated. Skipping CSV data loading on startup.");
            }
        };
    }
}