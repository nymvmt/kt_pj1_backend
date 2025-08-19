package com.kt.backendapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "brand_manager")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandManager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manager_id")
    private Long managerId;
    
    @Column(name = "name", length = 100, nullable = false)
    private String name;
    
    @Column(name = "email", length = 255, nullable = false)
    private String email;
    
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Column(name = "password", length = 255, nullable = false)
    private String password;
    
}
