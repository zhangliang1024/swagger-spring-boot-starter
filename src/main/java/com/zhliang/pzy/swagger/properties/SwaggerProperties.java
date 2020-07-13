package com.zhliang.pzy.swagger.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.Map;

/**
 * @创建人：zhiang
 * @version：V1.0
 */
@ConfigurationProperties(SwaggerProperties.prefix)
public class SwaggerProperties {
	public static final String prefix = "spring.swagger";

	/**
	 * 启用swagger，如果不配置，spring.profiles中包含api依然会启用，如果配置为false，swagger不会启用，如果配置为true，swagger会启用
	 */
	private Boolean enabled;
	/**
	 * swagger2组配置
	 */
	private Map<String, SwaggerGroupProperties> group;

	/**
	 * 全局统一鉴权配置
	 **/
	private Authorization authorization = new Authorization();

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Map<String, SwaggerGroupProperties> getGroup() {
		return group;
	}

	public void setGroup(Map<String, SwaggerGroupProperties> group) {
		this.group = group;
	}

	public Authorization getAuthorization() {
		return authorization;
	}

	public void setAuthorization(Authorization authorization) {
		this.authorization = authorization;
	}
}
