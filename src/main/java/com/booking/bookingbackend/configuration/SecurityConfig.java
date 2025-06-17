package com.booking.bookingbackend.configuration;

import com.booking.bookingbackend.constant.EndpointConstant;
import com.booking.bookingbackend.service.user.UserInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import jakarta.websocket.Endpoint;
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
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomizeRequestFilter requestFilter;
    private final UserInfoService userInfoService;

    private static final String[] WHITE_LIST_API = {
            EndpointConstant.ENDPOINT_USER + "/register",
            EndpointConstant.ENDPOINT_AUTH + "/login",
            EndpointConstant.ENDPOINT_AUTH + "/host/**",
            EndpointConstant.ENDPOINT_AUTH + "/verify-email",
            EndpointConstant.ENDPOINT_AUTH + "/refresh-token",
            EndpointConstant.ENDPOINT_AUTH + "/logout",
            EndpointConstant.ENDPOINT_AUTH + "/check-exist-email",
            EndpointConstant.ENDPOINT_AUTH + "/outbound/authentication",
            EndpointConstant.ENDPOINT_AUTH + "/outbound/authentication-app",
            EndpointConstant.ENDPOINT_MAIL,
            EndpointConstant.ENDPOINT_USER + "/forgot-password",
            EndpointConstant.ENDPOINT_USER + "/reset-password",
            EndpointConstant.ENDPOINT_PAYMENT,
            EndpointConstant.ENDPOINT_BOOKING,
            EndpointConstant.ENDPOINT_USER + "/host/check-email",
            EndpointConstant.ENDPOINT_PROPERTY + "/redis"
    };
    private static final String[] GET_LIST_API = {
            EndpointConstant.ENDPOINT_PROPERTY + "/search",
            EndpointConstant.ENDPOINT_PROPERTY + "/{id}",
            EndpointConstant.ENDPOINT_PROPERTY + "/{id}/accommodations",
            EndpointConstant.ENDPOINT_PROPERTY + "/{id}/reviews",
            EndpointConstant.ENDPOINT_PROPERTY + "/{id}/accommodations/available",
            EndpointConstant.ENDPOINT_PROPERTY + "/{id}/detail",
            EndpointConstant.ENDPOINT_PAYMENT + "/check-payment-status",
            EndpointConstant.ENDPOINT_PAYMENT + "/get-payment",
            EndpointConstant.ENDPOINT_LOCATION,
            EndpointConstant.ENDPOINT_AMENITIES + "/properties",
            EndpointConstant.ENDPOINT_IMAGE + "/**"
    };


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

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
                                .requestMatchers(HttpMethod.GET, GET_LIST_API).permitAll()
                                .anyRequest().authenticated()
                ).sessionManagement(
                        manager -> manager
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(
                        ex -> ex
                                .authenticationEntryPoint(new JwtAuthenticationEntryPoint(objectMapper())));

        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource())); // cấu hình CORS
        return httpSecurity.build();
    }

    // Config resource from swagger
    @Bean
    public WebSecurityCustomizer ignoreResource() {
        return webSecurity -> webSecurity
                .ignoring()
                .requestMatchers("/css/**", "/js/**", "/images/**");
    }

    /**
     * Configures and provides a custom {@link AuthenticationProvider} bean to be used in the
     * authentication process. The method sets up a {@link DaoAuthenticationProvider} with a password
     * encoder and a user details service.
     *
     * @return an instance of {@link AuthenticationProvider} configured with password encoding and
     * user details service.
     */
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

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(
                LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME)
        );
        module.addSerializer(
                LocalDate.class,
                new LocalDateSerializer(DateTimeFormatter.ISO_DATE)
        );
        module.addDeserializer(
                LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME)
        );
        module.addDeserializer(
                LocalDate.class,
                new LocalDateDeserializer(DateTimeFormatter.ISO_DATE)
        );
        module.addSerializer(
                LocalTime.class,
                new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm"))
        );
        module.addDeserializer(
                LocalTime.class,
                new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm"))
        );
        objectMapper.registerModule(module);
        return objectMapper;
    }
}
