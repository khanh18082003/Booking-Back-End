package com.booking.bookingbackend.data.base;

import java.io.Serial;
import java.util.Objects;
import org.hibernate.proxy.HibernateProxy;

public abstract class AbstractIdentifiable<I> implements Identifiable<I> {

  @Serial
  private static final long serialVersionUID = -3107645187362007736L;

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    var id = getId();

    if (id == null) {
      return false;
    }

    return o instanceof AbstractIdentifiable<?> other
        && getEffectiveClass(this) == getEffectiveClass(o)
        && Objects.equals(id, other.getId());
  }

  @Override
  public final int hashCode() {
    var id = getId();

    return id == null ? getEffectiveClass(this).hashCode() : id.hashCode();
  }

  private static Class<?> getEffectiveClass(Object object) {
    return object instanceof HibernateProxy hibernateProxy
        ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass()
        : object.getClass();
  }
}
