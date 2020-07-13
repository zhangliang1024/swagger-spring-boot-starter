package com.zhliang.pzy.swagger.autoconfig;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @创建人：zhiang
 * @version：V1.0
 */
class ConditionNotApi implements Condition {
	ConditionApi conditionApi=new ConditionApi();
	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		 return !conditionApi.matches(context, metadata);
	}
}