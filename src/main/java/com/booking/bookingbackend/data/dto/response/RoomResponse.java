package com.booking.bookingbackend.data.dto.response;

import com.booking.bookingbackend.data.entity.BedType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomResponse implements Serializable{
    Integer id;
    String roomName;
    String roomType;
    List<BedTypeResponse> bedTypeList;
}
