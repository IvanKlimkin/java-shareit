package ru.practicum.shareit.booking.dto;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Constraint(validatedBy = StartBeforeEndValidator.class)
public @interface StartBeforeEnd {
    Class<?>[] groups() default {};
}
