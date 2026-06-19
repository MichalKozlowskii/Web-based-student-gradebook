package com.student_gradebook.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@SpringBootApplication
public class GatewayApplication {
	@Value("${urls.auth}")
	private String authUrl;
	@Value("${urls.groups}")
	private String groupsUrl;
	@Value("${urls.grades}")
	private String gradesUrl;
	@Value("${urls.attendance}")
	private String attendanceUrl;
	@Value("${urls.summary}")
	private String summaryUrl;
	@Value("${urls.exams}")
	private String examsUrl;


	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public RouteLocator routeConfig(RouteLocatorBuilder routeLocatorBuilder) {

		return routeLocatorBuilder.routes()
				.route(p -> p
						.path("/gradebook/auth/**")
						.filters( f -> f.rewritePath("/gradebook/auth/(?<segment>.*)","/${segment}")
								.addResponseHeader("X-Gateway-Time", LocalDateTime.now().toString())
								.circuitBreaker(config -> config.setName("authCircuitBreaker")
										.setFallbackUri("forward:/serviceNotAvailable")))
						.uri(authUrl))
				.route(p -> p
						.path("/gradebook/groups/**")
						.filters( f -> f.rewritePath("/gradebook/groups/(?<segment>.*)","/${segment}")
								.addResponseHeader("X-Gateway-Time", LocalDateTime.now().toString())
								.retry(retryConfig -> retryConfig.setRetries(3)
										.setMethods(HttpMethod.GET)
										.setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true)
								)
								.circuitBreaker(config -> config.setName("groupsCircuitBreaker")
										.setFallbackUri("forward:/serviceNotAvailable")))
						.uri(groupsUrl))
				.route(p -> p
						.path("/gradebook/grades/**")
						.filters( f -> f.rewritePath("/gradebook/grades/(?<segment>.*)","/${segment}")
								.addResponseHeader("X-Gateway-Time", LocalDateTime.now().toString())
								.retry(retryConfig -> retryConfig.setRetries(2)
										.setMethods(HttpMethod.GET)
										.setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true)
								)
								.circuitBreaker(config -> config.setName("gradesCircuitBreaker")
										.setFallbackUri("forward:/serviceNotAvailable")))
						.uri(gradesUrl))
				.route(p -> p
						.path("/gradebook/attendance/**")
						.filters( f -> f.rewritePath("/gradebook/attendance/(?<segment>.*)","/${segment}")
								.addResponseHeader("X-Gateway-Time", LocalDateTime.now().toString())
								.retry(retryConfig -> retryConfig.setRetries(2)
										.setMethods(HttpMethod.GET)
										.setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true)
								)
								.circuitBreaker(config -> config.setName("attendanceCircuitBreaker")
										.setFallbackUri("forward:/serviceNotAvailable")))
						.uri(attendanceUrl))
				.route(p -> p
						.path("/gradebook/summary/**")
						.filters( f -> f.rewritePath("/gradebook/summary/(?<segment>.*)","/${segment}")
								.addResponseHeader("X-Gateway-Time", LocalDateTime.now().toString())
								.retry(retryConfig -> retryConfig.setRetries(2)
										.setMethods(HttpMethod.GET)
										.setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true)
								)
								.circuitBreaker(config -> config.setName("summaryCircuitBreaker")
										.setFallbackUri("forward:/serviceNotAvailable")))
						.uri(summaryUrl))
				.route(p -> p
						.path("/gradebook/exams/**")
						.filters( f -> f.rewritePath("/gradebook/exams/(?<segment>.*)","/${segment}")
								.addResponseHeader("X-Gateway-Time", LocalDateTime.now().toString())
								.retry(retryConfig -> retryConfig.setRetries(2)
										.setMethods(HttpMethod.GET)
										.setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true)
								)
								.circuitBreaker(config -> config.setName("examsCircuitBreaker")
										.setFallbackUri("forward:/serviceNotAvailable")))
						.uri(examsUrl))
				.build();
	}

	@Bean
	public RedisRateLimiter redisRateLimiter() {
		return new RedisRateLimiter(20, 40, 1);
	}

	@Bean
	public KeyResolver ipKeyResolver() {
		return exchange -> Mono.just(
				exchange.getRequest()
						.getRemoteAddress()
						.getAddress()
						.getHostAddress()
		);
	}
}