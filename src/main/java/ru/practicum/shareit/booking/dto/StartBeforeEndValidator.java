package ru.practicum.shareit.booking.dto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, BookingDto> {


    @Override
    public void initialize(StartBeforeEnd startBeforeEnd) {

    }

    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext constraintValidatorContext) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(
                    "Время начала раньше времени окончания бронирования").addPropertyNode("Start").addConstraintViolation();
            return false;
        }
        return true;
    }
}
