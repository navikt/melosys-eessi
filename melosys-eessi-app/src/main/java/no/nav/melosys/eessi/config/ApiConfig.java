package no.nav.melosys.eessi.config;

import no.nav.melosys.eessi.security.HeaderValidatorRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Profile("nais")
@Configuration
public class ApiConfig implements WebMvcConfigurer {

    private final Environment environment;

    public ApiConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public HeaderValidatorRequestInterceptor requestInterceptor() {
        return new HeaderValidatorRequestInterceptor(
                environment.getProperty("melosys.api.security.header-name"),
                environment.getProperty("melosys.api.security.header-value"),
                environment.getProperty("spring.profiles.active")
        );
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestInterceptor())
                .addPathPatterns(
                        "/**"
                )
                .excludePathPatterns(
                        "/actuator/**"
                );
    }
}
