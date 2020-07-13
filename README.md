# swagger-spring-boot-starter
> 该项目整合Springboot与Swagger,以starter的方式通过配置集成开发文档

### 一、项目介绍
```markdown
1. 集成SpringBoot并完美支持swagger-spring的配置项
2. 支持API通过正则表达式分组配置
3. 支持分环境配置。可以在开发、测试、UAT查看文档，而在生产环境不可查看
```

### 二、使用介绍
> pom.xml
```xml
<dependency>
    <groupId>com.zhliang.pzy</groupId>
    <artifactId>swagger-spring-boot-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```
> application.yaml
```yaml
spring:
  swagger:
    enabled: true                                   # 是否启用swagger
    group:
      user-api:                                     # 用户组api，可以配置多个组
        group-name: user-api                        # api组的名字，会在swagger-ui的api下拉列表中显示；组名前的序号，多个组可以排序；最好不要写中文
        title: 用户相关的操作                        # api组的标题，会在swagger-ui的标题处显示
        description: 用户相关的操作，包括用户登录登出  # api组的描述，会在swagger-ui的描述中显示
        path-regex: /api/user/.*                    # 匹配到本组的api接口，匹配uri，可以用用正则表达式
        path-mapping: /                             # 匹配到的url在swagger中测试请求时加的url前缀
        version: 0.0.0                              # api版本
        license: 该文档仅限公司内部传阅               # 授权协议
        license-url: '#'                            # 授权协议地址
        terms-of-service-url:                       # 服务条款地址
        contact:                                    # 文档联系人
          name: admin                               # 联系人名字
          email: abc@xxx.com                        # 联系人邮箱
          url: http://www.abc.com                   # 联系地址
```