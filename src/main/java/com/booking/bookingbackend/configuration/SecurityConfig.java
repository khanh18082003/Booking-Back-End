package com.booking.bookingbackend.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {


  private static final String[] WHITE_LIST_API = {
      "/permissions/**",
  };

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .authorizeHttpRequests(
            request -> request
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/booking-api/v1/v3/api-docs/**"
                ).permitAll()
//                .requestMatchers(HttpMethod.POST, WHITE_LIST_API).permitAll()
                .requestMatchers("/**").permitAll()
                .anyRequest().authenticated()
        ).sessionManagement(
            manager -> manager
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
//        .authenticationProvider(authenticationProvider())
//        .addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class)
//        .exceptionHandling(
//            ex -> ex
//                .authenticationEntryPoint(new JwtAuthenticationEntryPoint(objectMapper())));

    httpSecurity
        .csrf(AbstractHttpConfigurer::disable);

    return httpSecurity.build();
  }

  // Config resource from swagger
  @Bean
  public WebSecurityCustomizer ignoreResource() {
    return webSecurity -> webSecurity
        .ignoring()
        .requestMatchers("/css/**", "/js/**", "/images/**");
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
  }

}
