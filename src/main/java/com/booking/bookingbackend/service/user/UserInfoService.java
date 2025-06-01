package com.booking.bookingbackend.service.user;

import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.entity.CustomUserDetails;
import com.booking.bookingbackend.data.entity.User;
import com.booking.bookingbackend.data.repository.UserRepository;
import com.booking.bookingbackend.exception.AppException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class UserInfoService implements UserDetailsService {

  UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(username)
        .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_UN_AUTHENTICATION));
    return new CustomUserDetails(user);
  }
}
