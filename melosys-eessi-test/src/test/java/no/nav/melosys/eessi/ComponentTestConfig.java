package no.nav.melosys.eessi;

import io.getunleash.FakeUnleash;
import io.getunleash.Unleash;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;

@TestConfiguration
@ComponentScan(basePackageClasses = MelosysEessiApplication.class)
public class ComponentTestConfig {

    @Bean
    @Primary
    public Unleash fakeUnleash() {
        return new FakeUnleash();
    }
}
