package com.kt.backendapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "brand_category")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;
    
    @Column(name = "category_name", length = 50, nullable = false)
    private String categoryName;
}
