package com.booking.bookingbackend.service.authentication;

import com.booking.bookingbackend.data.dto.request.AuthenticationRequest;
import com.booking.bookingbackend.data.dto.response.AuthenticationResponse;

public interface AuthenticationService {
  AuthenticationResponse authenticate(AuthenticationRequest request);
}
