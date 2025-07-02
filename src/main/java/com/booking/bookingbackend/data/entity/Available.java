package com.booking.bookingbackend.data.entity;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.booking.bookingbackend.data.base.AbstractIdentifiable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "tbl_available")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Available extends AbstractIdentifiable<Integer> {

    @Serial
    private static final long serialVersionUID = 1666931288627522644L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "date", nullable = false)
    LocalDate date;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    BigDecimal price;

    @Column(name = "total_inventory", nullable = false)
    int totalInventory;

    @Column(name = "total_reserved", nullable = false)
    int totalReserved;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    Accommodation accommodation;
}
