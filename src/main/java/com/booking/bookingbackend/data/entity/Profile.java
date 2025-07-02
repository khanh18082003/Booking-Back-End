package com.booking.bookingbackend.data.entity;

import java.io.Serial;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.UuidGenerator.Style;

import com.booking.bookingbackend.constant.Gender;
import com.booking.bookingbackend.data.base.UUIDJpaEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "tbl_profile")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile extends UUIDJpaEntity {

    @Serial
    private static final long serialVersionUID = -4881599000575895723L;

    @Id
    @UuidGenerator(style = Style.TIME)
    UUID id;

    @Column(name = "first_name")
    String firstName;

    @Column(name = "last_name")
    String lastName;

    @Column(name = "avatar")
    String avatar;

    @Column(name = "country_code")
    String countryCode;

    @Column(name = "phone_number")
    String phone;

    @Column(name = "dob")
    LocalDate dob;

    @Column(name = "address")
    String address;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    Gender gender;

    @Column(name = "nationality")
    String nationality;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;
}
