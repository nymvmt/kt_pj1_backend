package com.kt.backendapp.dto.common;

public enum SortOption {
    NAME_ASC("brandName", "asc"),
    NAME_DESC("brandName", "desc"),
    VIEW_COUNT_ASC("details.viewCount", "asc"),
    VIEW_COUNT_DESC("details.viewCount", "desc"),
    SAVE_COUNT_ASC("details.saveCount", "asc"),
    SAVE_COUNT_DESC("details.saveCount", "desc"),
    CREATED_AT_ASC("createdAt", "asc"),
    CREATED_AT_DESC("createdAt", "desc");
    
    private final String property;
    private final String direction;
    
    SortOption(String property, String direction) {
        this.property = property;
        this.direction = direction;
    }
    
    public String getProperty() {
        return property;
    }
    
    public String getDirection() {
        return direction;
    }
}
