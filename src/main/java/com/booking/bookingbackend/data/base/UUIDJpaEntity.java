package com.booking.bookingbackend.data.base;

import java.io.Serial;
import java.util.UUID;

public abstract class UUIDJpaEntity extends AbstractIdentifiable<UUID> {
  @Serial
  private static final long serialVersionUID = 7161827127843128608L;
}
