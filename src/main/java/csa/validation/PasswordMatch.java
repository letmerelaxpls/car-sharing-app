package csa.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Constraint(validatedBy = PasswordMatchValidator.class)
@Target(ElementType.TYPE)
public @interface PasswordMatch {
    String message() default "Passwords don`t match";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
