package fingertips.backend.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Spring MVC의 설정을 활성화
@EnableWebMvc
@ComponentScan(basePackages = {
        "fingertips.backend.controller",
        "fingertips.backend.exception",
        "fingertips.backend.challenge.controller",
        "fingertips.backend.member.controller",
        "fingertips.backend.consumption.controller",
        "fingertips.backend.asset.controller",
        "fingertips.backend.admin.controller",
}) //컨트롤러만 찾는다. 반드시 이 패키지에 컨트롤러 적어줘야한다.
public class ServletConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/")
                .setViewName("forward:/resources/index.html");
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("/resources/assets/");
    }
}

