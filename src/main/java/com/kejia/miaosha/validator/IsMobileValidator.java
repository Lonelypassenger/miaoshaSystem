package com.kejia.miaosha.validator;

import com.alibaba.druid.util.StringUtils;
import com.kejia.miaosha.utils.ValidatorUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @AUTHOR :yuankejia
 * @DESCRIPTION:
 * @DATE:CRETED: IN 18:19 2019/9/24
 * @MODIFY:
 */
public class IsMobileValidator implements ConstraintValidator<isMobile, String> {
    private boolean required = false;

    public void initialize(isMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(required) {
            return ValidatorUtil.isMobile(value);
        }else {
            if(StringUtils.isEmpty(value)) {
                return true;
            }else {
                return ValidatorUtil.isMobile(value);
            }
        }
    }
}
