package com.example.config;

/**
 * [swagger配置类，生成API文档]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/10/7 9:56]
 */

import io.swagger.annotations.Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
public class Swagger2Configuration {
    @Bean
    public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.any())
                .build();
    }
    //基本信息的配置，信息会在api文档上显示
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("blog测试的接口文档")
                .description("MyBlog相关接口的文档")
                .termsOfServiceUrl("http://localhost:8081/")
                .version("1.0")
                .build();
    }
}
