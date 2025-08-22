package com.kt.backendapp.controller;

import com.kt.backendapp.dto.common.ApiResponse;
import com.kt.backendapp.dto.common.PageResponse;
import com.kt.backendapp.dto.request.consultation.ConsultationRescheduleRequest;
import com.kt.backendapp.dto.response.consultation.ConsultationResponse;
import com.kt.backendapp.service.IConsultationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manager/consultations")
@RequiredArgsConstructor
@Slf4j
public class ManagerConsultationController {
    
    private final IConsultationService consultationService;
    
    /**
     * 매니저의 상담 목록 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ConsultationResponse>>> getManagerConsultations(
            @RequestHeader("Manager-Id") Long managerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<ConsultationResponse> consultations = consultationService.getManagerConsultations(managerId, pageable);
        PageResponse<ConsultationResponse> pageResponse = PageResponse.of(consultations);
        
        return ResponseEntity.ok(ApiResponse.success(pageResponse, "상담 목록을 조회했습니다."));
    }
    
    /**
     * 상담 일정 조정 요청
     */
    @PutMapping("/{consultationId}/reschedule")
    public ResponseEntity<ApiResponse<ConsultationResponse>> rescheduleConsultation(
            @PathVariable Long consultationId,
            @Valid @RequestBody ConsultationRescheduleRequest request,
            @RequestHeader("Manager-Id") Long managerId) {
        
        try {
            ConsultationResponse response = consultationService.rescheduleConsultation(consultationId, request, managerId);
            return ResponseEntity.ok(ApiResponse.success(response, "상담 일정 조정 요청을 보냈습니다."));
        } catch (IllegalStateException e) {
            // 상태 관련 비즈니스 로직 오류
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("INVALID_STATUS", e.getMessage()));
        } catch (IllegalArgumentException e) {
            // 권한 없음 또는 잘못된 파라미터 오류
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("INVALID_PARAMETER", e.getMessage()));
        } catch (Exception e) {
            log.error("상담 일정 조정 중 오류 발생 - 상담ID: {}, 매니저ID: {}", consultationId, managerId, e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "상담 일정 조정 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 상담 확정 (PENDING → CONFIRMED 직접 변경)
     */
    @PutMapping("/{consultationId}/confirm")
    public ResponseEntity<ApiResponse<ConsultationResponse>> confirmConsultation(
            @PathVariable Long consultationId,
            @RequestHeader("Manager-Id") Long managerId) {
        
        try {
            ConsultationResponse response = consultationService.confirmConsultation(consultationId, managerId);
            return ResponseEntity.ok(ApiResponse.success(response, "상담이 확정되었습니다."));
        } catch (IllegalStateException e) {
            // 상태 관련 비즈니스 로직 오류
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("INVALID_STATUS", e.getMessage()));
        } catch (IllegalArgumentException e) {
            // 권한 없음 또는 잘못된 파라미터 오류
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("INVALID_PARAMETER", e.getMessage()));
        } catch (Exception e) {
            log.error("상담 확정 중 오류 발생 - 상담ID: {}, 매니저ID: {}", consultationId, managerId, e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "상담 확정 중 오류가 발생했습니다."));
        }
    }
    

    
    /**
     * 상담 취소
     */
    @DeleteMapping("/{consultationId}")
    public ResponseEntity<ApiResponse<Void>> cancelConsultation(
            @PathVariable Long consultationId,
            @RequestHeader("Manager-Id") Long managerId) {
        
        try {
            consultationService.cancelConsultationByManager(consultationId, managerId);
            return ResponseEntity.ok(ApiResponse.success(null, "상담이 취소되었습니다."));
        } catch (IllegalStateException e) {
            // 상태 관련 비즈니스 로직 오류
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("INVALID_STATUS", e.getMessage()));
        } catch (IllegalArgumentException e) {
            // 권한 없음 또는 잘못된 파라미터 오류
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("INVALID_PARAMETER", e.getMessage()));
        } catch (Exception e) {
            log.error("상담 취소 중 오류 발생 - 상담ID: {}, 매니저ID: {}", consultationId, managerId, e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "상담 취소 중 오류가 발생했습니다."));
        }
    }
}
