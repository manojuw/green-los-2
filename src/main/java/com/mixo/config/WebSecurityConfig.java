package com.mixo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http, ApiKeyFilter apiKeyFilter) throws Exception {
		http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/lending/**", "/bbps/**", "/api/loans/**", "/aeps/**",
				"/esignRequest/**","esignRequestV2/**", "/enachRequest/**", "/esignCall/**", "/nachCallback/**"));

		http.authorizeHttpRequests(authz -> authz
				.requestMatchers("/login", "/bbps/**", "/access-denied", "/registration", "/api/loans/**",
						"/esignCall/**", "/esignRequestV2/**", "/esignRequest/**","v2/esignRequest/**", "/api/lending/**", "/aeps/**",
						"/assets/**", "/sass/**", "/js/**", "/css/**", "/images/**", "/nachCallback/**")
				.permitAll().anyRequest().authenticated())
				.formLogin(authz -> authz.loginPage("/login").permitAll().defaultSuccessUrl("/dashboard", true)
						.failureUrl("/login?error=true"))
				.logout(authz -> authz.deleteCookies("JSESSIONID")
						.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
						.logoutSuccessUrl("/login?logout=true"))
				.exceptionHandling(
						authz -> authz.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
								.accessDeniedPage("/access-denied")
								.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")));

		// Register custom API key filter
		http.addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(customUserDetailsService);
		authenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
		return authenticationProvider;
	}

}