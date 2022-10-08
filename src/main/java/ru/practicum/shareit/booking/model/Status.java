package ru.practicum.shareit.booking.model;

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
            if (value.name().equals(status)) {
                return Optional.of(value);
            }

        }
        return Optional.empty();
    }

}
/*
ALL,
CURRENT,
FUTURE,
PAST,
REJECTED,
WAITING
 */