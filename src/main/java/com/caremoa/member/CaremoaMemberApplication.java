package com.caremoa.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing     // JPA Auditing을 활성화 하기 위한 어노테이션
@EnableFeignClients
public class CaremoaMemberApplication {

	public static void main(String[] args) {
		SpringApplication.run(CaremoaMemberApplication.class, args);
	}
}
