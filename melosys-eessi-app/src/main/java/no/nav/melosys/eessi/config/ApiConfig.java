package no.nav.melosys.eessi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiConfig implements WebMvcConfigurer {

    private static final String API_PREFIX = "/api";

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(API_PREFIX, ApiConfig::erApiTjeneste);
    }

    private static boolean erApiTjeneste(Class clazz) {
        return clazz.getPackageName().startsWith(Konstanter.CONTROLLER_PAKKE)
                && clazz.isAnnotationPresent(RestController.class);
    }
}
