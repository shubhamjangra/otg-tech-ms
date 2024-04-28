package com.otg.tech.constant;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class EnumValueValidator implements ConstraintValidator<EnumValidator, String> {

    private EnumValidator enumValidator;

    @Override
    public void initialize(EnumValidator enumValidator) {
        this.enumValidator = enumValidator;
    }

    @Override
    public boolean isValid(
            String valueForValidation, ConstraintValidatorContext constraintValidatorContext) {

        if (enumValidator.isBlankAllowed() && StringUtils.isBlank(valueForValidation)) {
            return true;
        }

        for (Enum<?> enumValue : this.enumValidator.enumClass().getEnumConstants()) {

            if (isValidEnumValue(valueForValidation, enumValue)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidEnumValue(String valueForValidation, Enum<?> enumValue) {
        return StringUtils.isNotBlank(valueForValidation)
                && valueForValidation.equalsIgnoreCase(enumValue.toString());
    }
}
