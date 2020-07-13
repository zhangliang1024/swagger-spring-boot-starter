package com.zhliang.pzy.swagger.properties;


import springfox.documentation.service.Contact;

/**
 * @创建人：zhiang
 * @version：V1.0
 */
public class ContactI {
	/**
	 * 联系的名字
	 */
	private String name = "";;
	/**
	 * 联系的邮箱
	 */
	private String email = "";;
	/**
	 * 联系的地址
	 */
	private String url = "";;

	public Contact toContact() {
		return new Contact(name, url, email);
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
