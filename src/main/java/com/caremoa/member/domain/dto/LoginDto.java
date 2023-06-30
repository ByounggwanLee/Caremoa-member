package com.caremoa.member.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDto {
	private Long id; // -- ID
	private String userId;
	private String password;
    private String name;
    private String role;
}
