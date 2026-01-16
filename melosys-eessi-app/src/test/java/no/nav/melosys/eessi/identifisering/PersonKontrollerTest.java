package no.nav.melosys.eessi.identifisering;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersonKontrollerTest {

    @Test
    void fjernSpesialtegn_fjernerIkkeAsciiTegn() {
        assertThat(PersonKontroller.fjernSpesialtegn("Łukasz")).isEqualTo("ukasz");
        assertThat(PersonKontroller.fjernSpesialtegn("Jän")).isEqualTo("Jn");
        assertThat(PersonKontroller.fjernSpesialtegn("Müller")).isEqualTo("Mller");
        assertThat(PersonKontroller.fjernSpesialtegn("Øyvind")).isEqualTo("yvind");
        assertThat(PersonKontroller.fjernSpesialtegn("Åse")).isEqualTo("se");
        assertThat(PersonKontroller.fjernSpesialtegn("Ærlig")).isEqualTo("rlig");
    }

    @Test
    void fjernSpesialtegn_beholderVanligeBokstaver() {
        assertThat(PersonKontroller.fjernSpesialtegn("Jan")).isEqualTo("Jan");
        assertThat(PersonKontroller.fjernSpesialtegn("Lukasz")).isEqualTo("Lukasz");
        assertThat(PersonKontroller.fjernSpesialtegn("Ole Hansen")).isEqualTo("OleHansen");
    }

    @Test
    void fjernSpesialtegn_haandtererNull() {
        assertThat(PersonKontroller.fjernSpesialtegn(null)).isEqualTo("");
    }

    @Test
    void fjernSpesialtegn_haandtererTomStreng() {
        assertThat(PersonKontroller.fjernSpesialtegn("")).isEqualTo("");
    }

    @Test
    void erSubsequence_finnerbokstaverIRekkefoelge() {
        assertThat(PersonKontroller.erSubsequence("Jn", "Jan")).isTrue();
        assertThat(PersonKontroller.erSubsequence("abc", "aXbYcZ")).isTrue();
        assertThat(PersonKontroller.erSubsequence("ukasz", "Lukasz")).isTrue();
    }

    @Test
    void erSubsequence_feilRekkefoelgeGirFalse() {
        assertThat(PersonKontroller.erSubsequence("nJ", "Jan")).isFalse();
        assertThat(PersonKontroller.erSubsequence("cba", "aXbYcZ")).isFalse();
    }

    @Test
    void erSubsequence_erCaseInsensitive() {
        assertThat(PersonKontroller.erSubsequence("JN", "jan")).isTrue();
        assertThat(PersonKontroller.erSubsequence("jn", "JAN")).isTrue();
    }

    @Test
    void navnMatcherMedSubsequence_spesialtegnISed() {
        // SED har spesialtegn, PDL har ikke
        assertThat(PersonKontroller.navnMatcherMedSubsequence("Jän", "Jan")).isTrue();
        assertThat(PersonKontroller.navnMatcherMedSubsequence("Łukasz", "Lukasz")).isTrue();
        assertThat(PersonKontroller.navnMatcherMedSubsequence("Müller", "Muller")).isTrue();
        assertThat(PersonKontroller.navnMatcherMedSubsequence("Øyvind", "Oyvind")).isTrue();
    }

    @Test
    void navnMatcherMedSubsequence_spesialtegnIPdl() {
        // PDL har spesialtegn, SED har ikke
        assertThat(PersonKontroller.navnMatcherMedSubsequence("Jan", "Jän")).isTrue();
        assertThat(PersonKontroller.navnMatcherMedSubsequence("Lukasz", "Łukasz")).isTrue();
        assertThat(PersonKontroller.navnMatcherMedSubsequence("Muller", "Müller")).isTrue();
        assertThat(PersonKontroller.navnMatcherMedSubsequence("Oyvind", "Øyvind")).isTrue();
    }

    @Test
    void navnMatcherMedSubsequence_beggeHarSpesialtegn() {
        assertThat(PersonKontroller.navnMatcherMedSubsequence("Jän", "Jãn")).isTrue();
        assertThat(PersonKontroller.navnMatcherMedSubsequence("Müller", "Müller")).isTrue();
    }

    @Test
    void navnMatcherMedSubsequence_ulikeNavnGirFalse() {
        assertThat(PersonKontroller.navnMatcherMedSubsequence("Anna", "Jan")).isFalse();
        assertThat(PersonKontroller.navnMatcherMedSubsequence("Per", "Pål")).isFalse();
        assertThat(PersonKontroller.navnMatcherMedSubsequence("Hansen", "Johansen")).isFalse();
    }
}
