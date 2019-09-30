package no.nav.melosys.eessi.controller.dto;

import lombok.Getter;

@SuppressWarnings("squid:S00115")
public enum Bestemmelse {

    ART_11_1("11_1"),
    ART_11_3_a("11_3_a"),
    ART_11_3_b("11_3_b"),
    ART_11_3_c("11_3_c"),
    ART_11_3_d("11_3_d"),
    ART_11_3_e("11_3_e"),
    ART_11_4("11_4"),
    ART_11_5("11_5"),
    ART_12_1("12_1"),
    ART_12_2("12_2"),
    ART_13_1_a("13_1_a"),
    ART_13_1_b_1("13_1_b_i"),
    ART_13_1_b_2("13_1_b_ii"),
    ART_13_1_b_3("13_1_b_iii"),
    ART_13_1_b_4("13_1_b_iv"),
    ART_13_2_a("13_2_a"),
    ART_13_2_b("13_2_b"),
    ART_13_3("13_3"),
    ART_13_4("13_4"),
    ART_14_11("14_11"),
    ART_15("15"),
    ART_16_1("16_1"),
    ART_16_2("16_2"),
    ART_87_8("87_8"),
    ART_87_a("87_a"),
    ANNET("annet");

    @Getter
    private String value;

    Bestemmelse(String value) {
        this.value = value;
    }
}
