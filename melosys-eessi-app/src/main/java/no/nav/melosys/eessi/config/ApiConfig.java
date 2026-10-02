package no.nav.melosys.eessi.config;

import no.nav.melosys.eessi.controller.interceptor.AdminTilgangInterceptor;
import no.nav.melosys.eessi.controller.interceptor.CorrelationIdInterceptor;
import no.nav.security.token.support.client.spring.oauth2.EnableOAuth2Client;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableOAuth2Client(cacheEnabled = true)
public class ApiConfig implements WebMvcConfigurer {

    private static final String API_PREFIX = "/api";

    private final AdminTilgangInterceptor adminTilgangInterceptor;

    public ApiConfig(AdminTilgangInterceptor adminTilgangInterceptor) {
        this.adminTilgangInterceptor = adminTilgangInterceptor;
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(API_PREFIX, ApiConfig::erApiTjeneste);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CorrelationIdInterceptor());
        // SedMottakAdminTjeneste ligger utenfor controller-pakken og får ikke /api-prefiks.
        // sed-mottatt-lager har aldri krevd adminnøkkel.
        registry.addInterceptor(adminTilgangInterceptor)
            .addPathPatterns("/admin/**", API_PREFIX + "/admin/**")
            .excludePathPatterns(API_PREFIX + "/admin/sed-mottatt-lager/**");
    }

    private static boolean erApiTjeneste(Class clazz) {
        return clazz.getPackageName().startsWith(Konstanter.CONTROLLER_PAKKE)
            && clazz.isAnnotationPresent(RestController.class);
    }
}
