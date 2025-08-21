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
    
    // 중복 신청 방지를 위한 활성 상담 조회 (PENDING, RESCHEDULE_REQUEST, CONFIRMED)
    @Query("SELECT c FROM Consultation c WHERE c.user.userId = :userId " +
           "AND c.brand.brandId = :brandId " +
           "AND c.status.statusCode IN (1, 2, 3) " +
           "AND c.isActive = true")
    Optional<Consultation> findActiveConsultation(@Param("userId") Long userId, 
                                                 @Param("brandId") Long brandId);
    
    // 사용자의 모든 상담 목록 조회 (취소된 것도 포함)
    @Query("SELECT c FROM Consultation c WHERE c.user.userId = :userId " +
           "ORDER BY c.createdAt DESC")
    Page<Consultation> findAllConsultationsByUserId(@Param("userId") Long userId, Pageable pageable);
    
    // 매니저의 모든 상담 목록 조회 (취소된 것도 포함)
    @Query("SELECT c FROM Consultation c WHERE c.brand.manager.managerId = :managerId " +
           "ORDER BY c.createdAt DESC")
    Page<Consultation> findAllConsultationsByManagerId(@Param("managerId") Long managerId, Pageable pageable);
    
    // 기존 메서드들 (호환성 유지)
    Page<Consultation> findByUserUserId(Long userId, Pageable pageable);
    
    @Query("SELECT c FROM Consultation c WHERE c.brand.manager.managerId = :managerId " +
           "AND c.isActive = true")
    Page<Consultation> findByManagerId(@Param("managerId") Long managerId, Pageable pageable);
    
    // 사용자의 활성 상담 목록 조회
    @Query("SELECT c FROM Consultation c WHERE c.user.userId = :userId " +
           "AND c.isActive = true")
    Page<Consultation> findActiveConsultationsByUserId(@Param("userId") Long userId, Pageable pageable);
    
    // 일정 조정 요청된 상담 목록 조회 (사용자용)
    @Query("SELECT c FROM Consultation c WHERE c.user.userId = :userId " +
           "AND c.status.statusCode = 2 " +
           "AND c.isActive = true")
    List<Consultation> findRescheduleRequestsByUserId(@Param("userId") Long userId);
    
    // 연관 엔티티를 함께 조회하는 메소드
    @Query("SELECT c FROM Consultation c " +
           "JOIN FETCH c.user " +
           "JOIN FETCH c.brand b " +
           "JOIN FETCH b.manager " +
           "JOIN FETCH c.status " +
           "WHERE c.consultationId = :consultationId")
    Optional<Consultation> findByIdWithRelations(@Param("consultationId") Long consultationId);
}