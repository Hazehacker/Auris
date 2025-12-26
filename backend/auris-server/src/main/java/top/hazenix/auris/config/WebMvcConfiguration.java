package top.hazenix.auris.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import top.hazenix.auris.interceptor.JwtTokenUserInterceptor;

/**
 * @description: 配置web层相关组件（swagger）
 * @author: Hazenix
 * @version: 1.0.1
 * @date: 2025/12/11
 * @return
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    private final JwtTokenUserInterceptor jwtTokenUserInterceptor;

    /**
     * 注册自定义拦截器
     *
     * @param registry
     */
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/user/user/logout")
                .addPathPatterns("/user/user/userinfo")
                .addPathPatterns("/user/user/profile")
                .addPathPatterns("/user/user/password");



    }

    /**
     * 通过knife4j生成接口文档的相关配置
     * @return
     */
    @Bean
    public Docket docket() {
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("Auris音乐项目接口文档")
                .version("1.0.0")
                .description("Auris音乐项目接口文档")
                .build();
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .select()
                //扫描的包要写对
                .apis(RequestHandlerSelectors.basePackage("top.hazenix.auris.controller"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }

    /**
     * (knife4j)给doc.html设置静态资源映射
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //如果不设置静态资源映射,访问http://localhost:8080/doc.html，idea会把doc.html看成一个controll层的一个接口
        log.info("开始设置静态资源映射");
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }



}
