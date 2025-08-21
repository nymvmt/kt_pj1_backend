package com.kt.backendapp.service;

import com.kt.backendapp.dto.request.auth.LoginRequest;
import com.kt.backendapp.dto.request.auth.UserCreateRequest;
import com.kt.backendapp.dto.request.brand.BrandManagerCreateRequest;
import com.kt.backendapp.dto.response.auth.LoginResponse;
import com.kt.backendapp.dto.response.auth.UserResponse;
import com.kt.backendapp.dto.response.brand.BrandManagerResponse;
import com.kt.backendapp.entity.*;
import com.kt.backendapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    
    private final UserRepository userRepository;
    private final BrandManagerRepository brandManagerRepository;
    private final BrandRepository brandRepository;
    private final BrandCategoryRepository brandCategoryRepository;
    private final BrandDetailRepository brandDetailRepository;
    
    /**
     * 사용자 회원가입
     */
    @Transactional
    public UserResponse userRegister(UserCreateRequest request) {
        // 1. 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        
        // 2. User 엔티티 생성 및 저장
        User user = User.builder()
            .email(request.getEmail())
            .password(request.getPassword())
            .name(request.getName())
            .phone(request.getPhone())
            .build();
            
        User savedUser = userRepository.save(user);
        
        return UserResponse.from(savedUser);
    }
    
    /**
     * 사용자 로그인
     */
    public LoginResponse userLogin(LoginRequest request) {
        // 1. 이메일로 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        
        // 2. 패스워드 검증
        if (!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        
        // 3. 로그인 응답 생성
        UserResponse userResponse = UserResponse.from(user);
        return LoginResponse.userSuccess(userResponse);
    }
    
    /**
     * 매니저 회원가입 + 브랜드 동시 등록 (상세 정보 포함)
     */
    @Transactional(readOnly = false)
    public BrandManagerResponse managerRegister(BrandManagerCreateRequest request) {
        // 1. 매니저 이메일 중복 체크
        if (brandManagerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        
        // 2. 브랜드명 중복 체크
        if (brandRepository.existsByBrandName(request.getBrandName())) {
            throw new IllegalArgumentException("이미 존재하는 브랜드명입니다.");
        }
        
        // 3. 카테고리 존재 확인 (미리 저장된 카테고리에서 선택)
        BrandCategory category = brandCategoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
        
        // 4. 브랜드 상세 정보 유효성 검증
        if (request.getInitialCost() == null || request.getInitialCost().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("가맹비는 필수이며 0보다 커야 합니다.");
        }
        if (request.getTotalInvestment() == null || request.getTotalInvestment().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("총 창업비용은 필수이며 0보다 커야 합니다.");
        }
        if (request.getAvgMonthlyRevenue() == null || request.getAvgMonthlyRevenue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("평균 월매출은 필수이며 0보다 커야 합니다.");
        }
        if (request.getStoreCount() == null || request.getStoreCount() <= 0) {
            throw new IllegalArgumentException("매장수는 필수이며 0보다 커야 합니다.");
        }
        if (request.getBrandDescription() == null || request.getBrandDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("브랜드 설명은 필수입니다.");
        }
        
        // 5. BrandManager 생성
        BrandManager manager = BrandManager.builder()
            .name(request.getName())
            .email(request.getEmail())
            .password(request.getPassword())
            .phone(request.getPhone())
            .build();
        BrandManager savedManager = brandManagerRepository.save(manager);
        
        // 6. Brand 생성 (매니저 + 카테고리 연결)
        Brand brand = Brand.builder()
            .brandName(request.getBrandName())
            .category(category)
            .manager(savedManager)
            .build();
        Brand savedBrand = brandRepository.save(brand);
        
        // 7. BrandDetail 생성 (브랜드 연결 + 모든 상세 정보 입력 - 필수)
        BrandDetail brandDetail = BrandDetail.builder()
            .brand(savedBrand)
            .initialCost(request.getInitialCost())
            .totalInvestment(request.getTotalInvestment())
            .avgMonthlyRevenue(request.getAvgMonthlyRevenue())
            .storeCount(request.getStoreCount())
            .brandDescription(request.getBrandDescription().trim())
            .viewCount(0L)  // 초기값
            .saveCount(0L)  // 초기값
            .build();
        brandDetailRepository.save(brandDetail);
        
        // 8. Brand 엔티티에 BrandDetail 연결 (양방향 관계 설정)
        savedBrand.setDetails(brandDetail);
        brandRepository.save(savedBrand);
        
        // 8. 응답 생성 (관리 브랜드 포함 - 리스트 방식)
        return BrandManagerResponse.from(savedManager, List.of(savedBrand));
    }
    
    /**
     * 매니저 로그인
     */
    public LoginResponse managerLogin(LoginRequest request) {
        // 1. 이메일로 매니저 조회
        BrandManager manager = brandManagerRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매니저입니다."));
        
        // 2. 패스워드 검증
        if (!manager.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        
        // 3. 관리 브랜드 전체 조회
        List<Brand> managedBrands = brandRepository.findAllByManagerManagerId(manager.getManagerId());
        
        // 4. 로그인 응답 생성 (모든 브랜드 포함)
        BrandManagerResponse managerResponse = BrandManagerResponse.from(manager, managedBrands);
        return LoginResponse.managerSuccess(managerResponse);
    }
    
    /**
     * 로그아웃 (단순 응답)
     */
    public void logout() {
        // 세션 없이 단순 로그아웃
        // 필요시 세션 무효화 로직 추가
    }
    
    /**
     * 내 정보 조회 (향후 세션 기반으로 구현)
     */
    public Object getMyInfo(String userType, Long userId) {
        if ("USER".equals(userType)) {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
            return UserResponse.from(user);
        } else if ("MANAGER".equals(userType)) {
            BrandManager manager = brandManagerRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매니저입니다."));
            
            // 관리 브랜드 전체 조회
            List<Brand> managedBrands = brandRepository.findAllByManagerManagerId(manager.getManagerId());
            return BrandManagerResponse.from(manager, managedBrands);
        }
        throw new IllegalArgumentException("잘못된 사용자 타입입니다.");
    }
}