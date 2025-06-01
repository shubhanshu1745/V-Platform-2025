package com.example.volunteer_platform;

import java.time.format.DateTimeFormatter;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.example.volunteer_platform.dto.ProfileUpdateRequestDTO;
import com.example.volunteer_platform.entity.User;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@SpringBootApplication
public class VolunteerPlatformApplication {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static void main(String[] args) {
        SpringApplication.run(VolunteerPlatformApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        Converter<ProfileUpdateRequestDTO, User> availabilityConverter = context -> {
            User destination = context.getDestination();
            ProfileUpdateRequestDTO source = context.getSource();
            if (source != null && source.getAvailabilityCalendar() != null) {
                destination.setAvailabilityCalendar(source.getAvailabilityCalendar());
            }
            return destination;
        };

        modelMapper.addConverter(availabilityConverter);

        return modelMapper;
    }

    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        LocalDateTimeSerializer serializer = new LocalDateTimeSerializer(dateFormatter);
        LocalDateTimeDeserializer deserializer = new LocalDateTimeDeserializer(dateFormatter);

        builder.serializers(serializer);
        builder.deserializers(deserializer);

        return builder;
    }
}