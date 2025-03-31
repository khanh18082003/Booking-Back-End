package com.booking.bookingbackend.service.authentication;

import com.booking.bookingbackend.data.dto.request.AuthenticationRequest;
import com.booking.bookingbackend.data.dto.request.RefreshTokenRequest;
import com.booking.bookingbackend.data.dto.response.AuthenticationResponse;

public interface AuthenticationService {

  AuthenticationResponse authenticate(AuthenticationRequest request);

  AuthenticationResponse refreshToken(RefreshTokenRequest request);

  void logout(String accessToken, String refreshToken);

}
