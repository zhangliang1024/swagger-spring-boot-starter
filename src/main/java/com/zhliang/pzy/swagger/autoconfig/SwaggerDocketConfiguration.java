package com.zhliang.pzy.swagger.autoconfig;

import com.google.common.base.Predicate;
import com.zhliang.pzy.swagger.properties.SwaggerGroupProperties;
import com.zhliang.pzy.swagger.properties.SwaggerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import java.util.*;
import java.util.Map.Entry;

/**
 * @创建人：zhiang
 * @version：V1.0
 */
@EnableSwagger2
public class SwaggerDocketConfiguration implements BeanFactoryPostProcessor, EnvironmentAware {

	private static final Logger log = LoggerFactory.getLogger(SwaggerDocketConfiguration.class);

	private Environment environment;

	@Override
	public void setEnvironment(Environment environment) {
		this.environment =  environment;
	}


	private SwaggerProperties getSwaggerProperties() {
		SwaggerProperties existingValue = Binder.get(environment).bind(SwaggerProperties.prefix, SwaggerProperties.class).get();
		return existingValue;
	}

	private String[] splitBasePackages(String basePackage) {
		if (StringUtils.isEmpty(basePackage) || (basePackage = basePackage.trim()).isEmpty()) {
			return null;
		} else {
			return basePackage.split(",");
		}
	}

	private Docket getSwagger2Docket(final SwaggerGroupProperties groupProperties, final List<String> pathUrls) {
		final String[] basePackages = this.splitBasePackages(groupProperties.getBasePackage());
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(new ApiInfoBuilder().title(groupProperties.getTitle())
						.description(groupProperties.getDescription()).version(groupProperties.getVersion())
						.license(groupProperties.getLicense()).licenseUrl(groupProperties.getLicenseUrl())
						.termsOfServiceUrl(groupProperties.getTermsOfServiceUrl())
						.contact(groupProperties.getContact().toContact()).build())
				.securityContexts(Collections.singletonList(securityContext()))
				.securitySchemes(Collections.singletonList(securitySchemes()))
				.groupName(groupProperties.getGroupName()).pathMapping(groupProperties.getPathMapping())// 最终调用接口后会和paths拼接在一起
				.select()
				.apis(new Predicate<RequestHandler>() {
					@Override
					public boolean apply(RequestHandler input) {
						if (basePackages == null)
							return true;
						String packageName = input.declaringClass().getName();
						for (String basePackage : basePackages) {
							if (packageName.startsWith(basePackage) || packageName.matches(basePackage+".*")) {
								return true;
							}
						}
						return false;
					}
				})
				.paths(new Predicate<String>() {
					@Override
					public boolean apply(String input) {
						String pathRegex = groupProperties.getPathRegex();
						if (StringUtils.isEmpty(pathRegex) || input.matches(pathRegex)) {
							pathUrls.add(input);
							return true;
						} else {
							return false;
						}
					}
				})
				.build();
	}

	private Docket getOtherSwagger2Docket(final List<String> pathUrls) {
		SwaggerGroupProperties otherSwagger = otherSwagger();
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(new ApiInfoBuilder().title(otherSwagger.getTitle()).description(otherSwagger.getDescription())
						.termsOfServiceUrl(otherSwagger.getTermsOfServiceUrl()).version(otherSwagger.getVersion())
						.contact(otherSwagger.getContact().toContact()).license(otherSwagger.getLicense())
						.licenseUrl(otherSwagger.getLicense()).build())
				.securityContexts(Collections.singletonList(securityContext()))
				.securitySchemes(Collections.singletonList(securitySchemes()))
				.groupName(otherSwagger.getGroupName()).pathMapping(otherSwagger.getPathMapping())// 最终调用接口后会和paths拼接在一起
				.select().apis(new Predicate<RequestHandler>() {
					@Override
					public boolean apply(RequestHandler input) {
						return input.isAnnotatedWith(GetMapping.class) || input.isAnnotatedWith(PostMapping.class)
								|| input.isAnnotatedWith(DeleteMapping.class) || input.isAnnotatedWith(PutMapping.class)
								|| input.isAnnotatedWith(RequestMapping.class);
					}
				}).paths(new Predicate<String>() {
					@Override
					public boolean apply(String input) {
						return !pathUrls.contains(input);
					}
				}).build();
	}

	private SwaggerGroupProperties otherSwagger() {
		SwaggerGroupProperties otherSwagger = new SwaggerGroupProperties();
		otherSwagger.setGroupName("other-api");
		otherSwagger.setDescription("以上api中未被包含进来得接口");
		otherSwagger.setPathMapping("/");
		otherSwagger.setVersion("");
		otherSwagger.setPathRegex(null);
		otherSwagger.setTitle("其它api");
		return otherSwagger;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		SwaggerProperties swaggerProperties = getSwaggerProperties();
		List<String> pathUrls = new ArrayList<String>();
		if (swaggerProperties.getGroup() != null && !swaggerProperties.getGroup().isEmpty()) {
			Iterator<Entry<String, SwaggerGroupProperties>> it = swaggerProperties.getGroup().entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, SwaggerGroupProperties> entry = (Map.Entry<String, SwaggerGroupProperties>) it.next();
				Docket swagger2Docket = this.getSwagger2Docket(entry.getValue(), pathUrls);
				beanFactory.registerSingleton(entry.getKey(), swagger2Docket);
			}
		}
		beanFactory.registerSingleton("other-api", this.getOtherSwagger2Docket(pathUrls));
	}

	/**
	 * 配置基于 ApiKey 的鉴权对象
	 */
	private ApiKey apiKey() {
		return new ApiKey(getSwaggerProperties().getAuthorization().getName(),
				getSwaggerProperties().getAuthorization().getKeyName(),
				ApiKeyVehicle.HEADER.getValue());
	}

	/**
	 * 配置基于 BasicAuth 的鉴权对象
	 */
	private BasicAuth basicAuth() {
		return new BasicAuth(getSwaggerProperties().getAuthorization().getName());
	}

	/**
	 * 配置默认的全局鉴权策略的开关，以及通过正则表达式进行匹配；默认 ^.*$ 匹配所有URL
	 * 其中 securityReferences 为配置启用的鉴权策略
	 */
	private SecurityContext securityContext() {
		return SecurityContext.builder()
				.securityReferences(defaultAuth())
				.forPaths(PathSelectors.regex(getSwaggerProperties().getAuthorization().getAuthRegex()))
				.build();
	}

	/**
	 * 配置默认的全局鉴权策略；其中返回的 SecurityReference 中，reference 即为ApiKey对象里面的name，保持一致才能开启全局鉴权
	 */
	private List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Collections.singletonList(SecurityReference.builder()
				.reference(getSwaggerProperties().getAuthorization().getName())
				.scopes(authorizationScopes).build());
	}

	public SecurityScheme securitySchemes(){
		if ("BasicAuth".equalsIgnoreCase(getSwaggerProperties().getAuthorization().getType())) {
			return basicAuth();
		} else if (!"None".equalsIgnoreCase(getSwaggerProperties().getAuthorization().getType())) {
			return apiKey();
		}
		return apiKey();
	}

}
