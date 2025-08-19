package com.kt.backendapp.repository;

import com.kt.backendapp.entity.Consultation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    
    @Query("SELECT c FROM Consultation c WHERE c.user.userId = :userId " +
           "AND c.brand.brandId = :brandId " +
           "AND c.status.statusCode IN (1, 2, 3)")  // PENDING=1, RESCHEDULE_REQUEST=2, CONFIRMED=3
    Optional<Consultation> findActiveConsultation(@Param("userId") Long userId, 
                                                 @Param("brandId") Long brandId);
    
    Page<Consultation> findByUserUserId(Long userId, Pageable pageable);
    
    @Query("SELECT c FROM Consultation c WHERE c.brand.manager.managerId = :managerId")
    Page<Consultation> findByManagerId(@Param("managerId") Long managerId, Pageable pageable);
}
