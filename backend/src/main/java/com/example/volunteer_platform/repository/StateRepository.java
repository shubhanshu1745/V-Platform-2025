package com.example.volunteer_platform.repository;

import com.example.volunteer_platform.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {
    Optional<State> findByStateName(String stateName);
    Optional<State> findByStateCode(String stateCode);
    List<State> findByCountry_CountryId(Long countryId);
}