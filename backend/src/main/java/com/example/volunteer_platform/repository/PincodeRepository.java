package com.example.volunteer_platform.repository;

import com.example.volunteer_platform.entity.Pincode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface PincodeRepository extends JpaRepository<Pincode, Long> {
    Optional<Pincode> findByPincode(String pincode);
    List<Pincode> findByDistrict_DistrictId(Long districtId);
}