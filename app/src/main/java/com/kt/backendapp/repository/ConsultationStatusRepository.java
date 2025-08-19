package com.kt.backendapp.repository;

import com.kt.backendapp.entity.ConsultationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsultationStatusRepository extends JpaRepository<ConsultationStatus, Long> {
    Optional<ConsultationStatus> findByStatusName(String statusName);
}
