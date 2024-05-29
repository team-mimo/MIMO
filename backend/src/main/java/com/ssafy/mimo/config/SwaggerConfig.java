package com.ssafy.mimo.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;

@OpenAPIDefinition(
	info = @Info(title = "MIMO", description = "MIMO API Document", version = "v1"),
	servers = {@Server(url = "https://k10a204.p.ssafy.io", description = "Default Server URL")}
)
@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {
	@Bean
	public GroupedOpenApi chatOpenApi() {
		String[] paths = {"/api/v1/**", "/api/check" };

		return GroupedOpenApi.builder()
			.group("MIMO API v1")
			.pathsToMatch(paths)
			.build();
	}
}
