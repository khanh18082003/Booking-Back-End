package com.booking.bookingbackend.data.dto.response;

import java.io.Serializable;
import java.sql.Timestamp;

import com.booking.bookingbackend.constant.Method;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class PermissionResponse implements Serializable {

    int id;
    Method method;
    String url;
    String description;

    @JsonProperty("created_at")
    Timestamp createdAt;

    @JsonProperty("updated_at")
    Timestamp updatedAt;
}
