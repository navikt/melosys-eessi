package no.nav.melosys.eessi.integration.oppgave;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class OppgaveOppdateringDtoTest {

    @Test
    void instansier_utenId_feiler() {
        final var builder = OppgaveOppdateringDto.builder().versjon(1);
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(builder::build)
                .withMessageContaining("OppgaveID er påkrevd");
    }

    @Test
    void instansier_utenVersjon_feiler() {
        final var builder = OppgaveOppdateringDto.builder().id(1);
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(builder::build)
                .withMessageContaining("Versjon er påkrevd");
    }
}
