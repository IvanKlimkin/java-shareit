package ru.practicum.shareit.booking.model;

import java.util.Objects;
import java.util.Optional;

public enum Status {
    ALL,
    APPROVED,
    CURRENT,
    FUTURE,
    PAST,
    REJECTED,
    WAITING;

    public static Optional<Status> from(String status) {
        for (Status value : Status.values()) {
            if (Objects.equals(value.name(), status)) {
                return Optional.of(value);
            }

        }
        return Optional.empty();
    }
}
