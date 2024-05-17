### 运行所需环境

JDK 17

Maven 3.9.5

idea使用版本2022以上，低版本Idea不支持jdk17，同时低版本jdk不支持**Spring-AI**

**注意事项**

**1.**  由于**Spring-AI**是最近才推出的发行版本，国内的阿里云仓库还没有相关依赖，在导入项目时，需要修改maven仓库的配置文件settings.xml，在里面需要
**注释掉**

```xml

<mirror>
    <id>alimaven</id>
    <name>aliyun maven</name>
    <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
    <mirrorOf>central</mirrorOf>
</mirror>
```

直接使用**maven的默认仓库**(需要挂梯子)

**2.**  在yaml配置文件中，需要导入自己的api key和提供服务的base-url

```yaml
spring:
  ai:
    openai:
      api-key: your-api-key
      base-url: your-base-url
      chat:
        api-key: your-api-key
        base-url: your-base-url
        options:
          model: gpt-3.5-turbo
          temperature: 0.7
```

