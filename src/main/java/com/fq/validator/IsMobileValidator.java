package com.fq.validator;

import com.alibaba.druid.util.StringUtils;
import com.fq.util.ValidateUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @Auther: 冯庆
 * @Date: 2018/8/8 10:47
 * @Description:
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    private boolean required = false;
    @Override
    public void initialize(IsMobile constraintAnnotation) {

    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (required){
            return ValidateUtil.isMobile(s);
        }else if (StringUtils.isEmpty(s)){
            return true;
        }else {
            return ValidateUtil.isMobile(s);
        }
    }
}
