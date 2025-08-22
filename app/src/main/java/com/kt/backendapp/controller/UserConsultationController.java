package com.kt.backendapp.controller;

import com.kt.backendapp.dto.common.ApiResponse;
import com.kt.backendapp.dto.common.PageResponse;
import com.kt.backendapp.dto.request.consultation.ConsultationCreateRequest;
import com.kt.backendapp.dto.request.consultation.ConsultationUserResponseRequest;
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

import java.util.List;

@RestController
@RequestMapping("/api/user/consultations")
@RequiredArgsConstructor
@Slf4j
public class UserConsultationController {
    
    private final IConsultationService consultationService;
    
    /**
     * 상담 신청
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ConsultationResponse>> createConsultation(
            @Valid @RequestBody ConsultationCreateRequest request,
            @RequestHeader("User-Id") Long userId) {
        
        try {
            // 요청에 사용자 ID 설정
            request.setUserId(userId);
            
            ConsultationResponse response = consultationService.createConsultation(request);
            
            return ResponseEntity.ok(ApiResponse.success(response, "상담 신청이 완료되었습니다."));
        } catch (IllegalStateException e) {
            // 중복 신청 등의 비즈니스 로직 오류
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("CONSULTATION_DUPLICATE", e.getMessage()));
        } catch (IllegalArgumentException e) {
            // 잘못된 파라미터 오류
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("INVALID_PARAMETER", e.getMessage()));
        } catch (Exception e) {
            log.error("상담 신청 중 오류 발생 - 사용자ID: {}, 브랜드ID: {}", userId, request.getBrandId(), e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "상담 신청 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 사용자의 상담 목록 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ConsultationResponse>>> getUserConsultations(
            @RequestHeader("User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<ConsultationResponse> consultations = consultationService.getUserConsultations(userId, pageable);
        PageResponse<ConsultationResponse> pageResponse = PageResponse.of(consultations);
        
        return ResponseEntity.ok(ApiResponse.success(pageResponse, "상담 목록을 조회했습니다."));
    }
    
    /**
     * 일정 조정 요청 목록 조회
     */
    @GetMapping("/reschedule-requests")
    public ResponseEntity<ApiResponse<List<ConsultationResponse>>> getRescheduleRequests(
            @RequestHeader("User-Id") Long userId) {
        
        List<ConsultationResponse> rescheduleRequests = consultationService.getUserRescheduleRequests(userId);
        
        return ResponseEntity.ok(ApiResponse.success(rescheduleRequests, "일정 조정 요청 목록을 조회했습니다."));
    }
    
    /**
     * 일정 조정 요청에 대한 응답 (수락/거절)
     */
    @PutMapping("/{consultationId}/respond")
    public ResponseEntity<ApiResponse<ConsultationResponse>> respondToReschedule(
            @PathVariable Long consultationId,
            @Valid @RequestBody ConsultationUserResponseRequest request,
            @RequestHeader("User-Id") Long userId) {
        
        try {
            ConsultationResponse response = consultationService.respondToReschedule(consultationId, request, userId);
            
            String message = request.getUserResponse().name().equals("ACCEPT") ? 
                    "상담 일정을 수락했습니다." : "상담을 취소했습니다.";
            
            return ResponseEntity.ok(ApiResponse.success(response, message));
        } catch (IllegalStateException e) {
            // 상태 관련 비즈니스 로직 오류
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("INVALID_STATUS", e.getMessage()));
        } catch (IllegalArgumentException e) {
            // 권한 없음 또는 잘못된 파라미터 오류
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("INVALID_PARAMETER", e.getMessage()));
        } catch (Exception e) {
            log.error("일정 조정 응답 중 오류 발생 - 상담ID: {}, 사용자ID: {}", consultationId, userId, e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "일정 조정 응답 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 상담 취소
     */
    @DeleteMapping("/{consultationId}")
    public ResponseEntity<ApiResponse<Void>> cancelConsultation(
            @PathVariable Long consultationId,
            @RequestHeader("User-Id") Long userId) {
        
        try {
            consultationService.cancelConsultation(consultationId, userId);
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
            log.error("상담 취소 중 오류 발생 - 상담ID: {}, 사용자ID: {}", consultationId, userId, e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "상담 취소 중 오류가 발생했습니다."));
        }
    }
}
