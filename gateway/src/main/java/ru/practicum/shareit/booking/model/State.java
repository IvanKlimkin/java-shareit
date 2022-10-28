package ru.practicum.shareit.booking.model;

import java.util.Objects;
import java.util.Optional;

public enum State {
    ALL,
    APPROVED,
    CURRENT,
    FUTURE,
    PAST,
    REJECTED,
    WAITING;

    public static Optional<State> from(String state) {
        for (State value : State.values()) {
            if (Objects.equals(value.name(), state)) {
                return Optional.of(value);
            }

        }
        return Optional.empty();
    }
}
