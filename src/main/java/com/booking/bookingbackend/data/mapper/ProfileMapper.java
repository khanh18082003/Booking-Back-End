package com.booking.bookingbackend.data.mapper;

import com.booking.bookingbackend.data.base.EntityDtoMapper;
import com.booking.bookingbackend.data.dto.request.ProfileUpdateRequest;
import com.booking.bookingbackend.data.dto.response.ProfileResponse;
import com.booking.bookingbackend.data.entity.Profile;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProfileMapper extends EntityDtoMapper<Profile, ProfileResponse> {

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "phone", source = "phoneNumber")
  @Mapping(target = "gender", ignore = true)
  void merge(ProfileUpdateRequest request, @MappingTarget Profile entity);

}
