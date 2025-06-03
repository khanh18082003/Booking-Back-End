package com.booking.bookingbackend.data.entity;

import com.booking.bookingbackend.util.SecurityUtils;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

// UserDetails is target interface for Spring Security
// Adapter
public record CustomUserDetails(User user) implements UserDetails {

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {

    return SecurityUtils.getAuthorities(user);
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public String getUsername() {
    return user.getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return UserDetails.super.isAccountNonExpired();
  }

  @Override
  public boolean isAccountNonLocked() {
    return UserDetails.super.isAccountNonLocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return UserDetails.super.isCredentialsNonExpired();
  }

  @Override
  public boolean isEnabled() {
    return user.isActive();
  }
}
