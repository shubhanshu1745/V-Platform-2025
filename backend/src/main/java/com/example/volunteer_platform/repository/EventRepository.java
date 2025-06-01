package com.example.volunteer_platform.repository;

import com.example.volunteer_platform.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    List<Event> findByPincodeAndStartDateBetween(String pincode, LocalDateTime start, LocalDateTime end);
    @Query(value = "SELECT * FROM events e WHERE MATCH(title, description, location, required_skills) AGAINST (:query IN BOOLEAN MODE)", nativeQuery = true)
    List<Event> searchEvents(@Param("query") String query);
}