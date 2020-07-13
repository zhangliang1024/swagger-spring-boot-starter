package com.zhliang.pzy.swagger.autoconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

/**
 * @创建人：zhiang
 * @version：V1.0
 */
@Configuration
public class SwaggerAutoConfiguration {

	private static final Logger log = LoggerFactory.getLogger(SwaggerAutoConfiguration.class);

	@Configuration
	@Conditional(ConditionApi.class)
	public static class swagger2Docket extends SwaggerDocketConfiguration {
		{
			log.warn( "启用了swagger文档");
		}
	}

	@RestController
	@Conditional(ConditionNotApi.class)
	public static class PreventSwaggerUi extends PreventSwaggerResourcesController {
		{
			log.warn("禁用了swagger文档html页面‘/swagger-ui.html’和其他资源的访问");
		}
	}
}
