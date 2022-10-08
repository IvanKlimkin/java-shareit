package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.exception.ValidationException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, BookingDto> {


    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext constraintValidatorContext) {

        if (bookingDto.getStart().isBefore(bookingDto.getEnd())) {
            return true;
        } else {
            throw new ValidationException("Время начала раньше времени окончания бронирования");
        }

    }
}
