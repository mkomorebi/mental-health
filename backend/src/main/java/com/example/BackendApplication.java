package com.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * BackendApplication 启动类，用于启动 Spring Boot 应用程序。
 * 配置 MyBatis 的 Mapper 扫描路径。
 */
@SpringBootApplication
@MapperScan("com.example.mapper")
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
