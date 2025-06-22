package com.booking.bookingbackend.controller;

import com.booking.bookingbackend.data.dto.request.UserCreationRequest;
import com.booking.bookingbackend.data.dto.response.UserResponse;
import com.booking.bookingbackend.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@TestPropertySource("/application-test.yml")
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }

    private UserCreationRequest request;

    private UserResponse response;
    private UUID id;

    @BeforeEach
    void initData() {
        request = UserCreationRequest.builder()
                .email("nguyentrungk461@gmail.com")
                .password("123456789")
                .confirmPassword("123456789")
                .build();
        id = UUID.randomUUID();
        response = UserResponse.builder()
                .id(id)
                .email("nguyentrungk461@gmail.com")
                .active(false)
                .createdAt(Timestamp.valueOf(LocalDate.now().atStartOfDay()))
                .updatedAt(Timestamp.valueOf(LocalDate.now().atStartOfDay()))
                .roles(Set.of())
                .build();
    }

    @Test
    void createUserTest() throws Exception {
        // Implement test logic here
        log.info("Running create user test");

        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(request);

        Mockito.when(userService.save(ArgumentMatchers.any())).thenReturn(response);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/users/register")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(content)
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value("M000"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.id").value(id))
        ;
    }
}
