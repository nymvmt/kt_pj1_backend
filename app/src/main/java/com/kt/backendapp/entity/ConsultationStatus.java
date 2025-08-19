package com.kt.backendapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "consultation_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 자동 생성으로 변경
    @Column(name = "status_code")
    private Long statusCode;  // String → Long으로 변경
    
    @Column(name = "status_name", length = 50, nullable = false)
    private String statusName;
}
