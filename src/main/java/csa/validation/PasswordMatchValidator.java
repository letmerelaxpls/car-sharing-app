package csa.validation;

import csa.dto.user.UserRegRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator
        implements ConstraintValidator<PasswordMatch, UserRegRequestDto> {
    @Override
    public boolean isValid(
            UserRegRequestDto userRegRequestDto,
            ConstraintValidatorContext constraintValidatorContext) {
        if (userRegRequestDto.getPassword() == null
                || userRegRequestDto.getRepeatPassword() == null) {
            return false;
        }
        return userRegRequestDto.getPassword()
                .equals(userRegRequestDto.getRepeatPassword());
    }
}
