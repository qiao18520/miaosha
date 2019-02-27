package com.miaoshaproject.validator;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;


@Component //类扫描的时候可以扫描到
public class ValidatorImpl implements InitializingBean{

	private Validator validator;//真正的工具
	
	//实现校验方法并返回校验结果
	public ValidationResult validate(Object bean) {
		ValidationResult result = new ValidationResult();
		//如果bean有错误constrainViolationSet就会参数
		Set<ConstraintViolation<Object>> constrainViolationSet = validator.validate(bean);
		if(constrainViolationSet.size()>0) {
			//有错误
			result.setHasErrors(true);
			constrainViolationSet.forEach(constraintViolation->{
				String errMsg = constraintViolation.getMessage();
				String propertyName = constraintViolation.getPropertyPath().toString();
				result.getErrorMsgMap().put(propertyName, errMsg);
			});
		}
		return result;
	}
	
	//当Spring Bean初始化完成之后会回调对应ValidatorImpl的afterPropertiesSet
	@Override
	public void afterPropertiesSet() throws Exception {
		//将hibernate validator通过工厂的初始化方式使其实例化
		this.validator = Validation.buildDefaultValidatorFactory().getValidator();//校验器
	}

}
