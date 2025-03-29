package com.booking.bookingbackend.configuration;

import com.booking.bookingbackend.service.user.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final CustomizeRequestFilter requestFilter;
  private final UserInfoService userInfoService;

  private static final String[] WHITE_LIST_API = {
      "/users/register",
      "/auth/login"
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
                .requestMatchers(HttpMethod.POST, WHITE_LIST_API).permitAll()
                .anyRequest().authenticated()
        ).sessionManagement(
            manager -> manager
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class);
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
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
    daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
    daoAuthenticationProvider.setUserDetailsService(userInfoService);
    return daoAuthenticationProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
  }

}
