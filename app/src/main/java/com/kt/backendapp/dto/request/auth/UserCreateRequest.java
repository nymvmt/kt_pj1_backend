package com.kt.backendapp.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateRequest {
    
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(max = 255, message = "이메일은 255자를 초과할 수 없습니다.")
    private String email;
    
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 6, max = 255, message = "비밀번호는 6자 이상 255자 이하여야 합니다.")
    private String password;
    
    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 100, message = "이름은 100자를 초과할 수 없습니다.")
    private String name;
    
    @Size(max = 20, message = "전화번호는 20자를 초과할 수 없습니다.")
    private String phone;
}