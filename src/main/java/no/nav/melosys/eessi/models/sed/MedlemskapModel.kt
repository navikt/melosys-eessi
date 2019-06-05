package no.nav.melosys.eessi.models.sed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

abstract class Medlemskap

@JsonIgnoreProperties(ignoreUnknown = true)
data class MedlemskapA001(
        var unntak: Unntak? = null,
        var vertsland: Vertsland? = null,
        var soeknadsperiode: Fastperiode? = null,
        var tidligereperiode: List<Periode>? = null,
        var naavaerendemedlemskap: List<Land>? = null,// Landkode
        var forespurtmedlemskap: List<Land>? = null,// Landkode
        var anmodning: Anmodning? = null,
        var forrigesoeknad: List<ForrigeSoeknad>? = null
) : Medlemskap()

// A001
data class Unntak(
        var startdatoansattforsikret: String? = null,
        var grunnlag: Grunnlag? = null,
        var spesielleomstendigheter: SpesielleOmstendigheter? = null,
        var startdatokontraktansettelse: String? = null,
        var begrunnelse: String? = null,
        var a1grunnlag: String? = null
)

data class Grunnlag(
        var annet: String? = null,
        var artikkel: String? = null
)

data class SpesielleOmstendigheter(
        var type: String? = null,
        var beskrivelseannensituasjon: String? = null
)

data class Vertsland(
        var arbeidsgiver: List<Arbeidsgiver>? = null
)

data class Fastperiode(
        var sluttdato: String? = null,
        var startdato: String? = null
)

data class AapenPeriode(
        @set:JsonProperty("type")
        var ukjentEllerÅpenSluttdato: String? = null,
        var startdato: String? = null
)

data class Periode(
        var aapenperiode: AapenPeriode? = null,
        var fastperiode: Fastperiode? = null
)

data class Land(
        var landkode: String? = null
)

data class Anmodning(
        var erendring: String? = null
)

data class ForrigeSoeknad(
        var dato: String? = null
)



class MedlemskapA002 : Medlemskap()



data class MedlemskapA003(
        var gjeldendereglerEC883: String? = null,
        var relevantartikkelfor8832004eller9872009: String? = null,
        var andreland: AndrelandA003? = null,
        var vedtak: VedtakA003? = null,
        @field:JsonProperty("isDeterminationProvisional") @get:JsonProperty("isDeterminationProvisional")
        var midlertidigVedtak: String? = null
) : Medlemskap()

//A003
data class VedtakA003(
        var gjeldervarighetyrkesaktivitet: String? = null,
        var erendringsvedtak: String? = null,
        var gjelderperiode: PeriodeA003? = null,
        var land: String? = null
)

data class AndrelandA003(
        var arbeidsgiver: List<Arbeidsgiver>? = null,
        var arbeidsgiveraktivitet: ArbeidsgiverAktivitet? = null
)

data class PeriodeA003 (
        var startdato: String? = null,
        var sluttdato: String? = null,
        var aapenperiode: AapenPeriode? = null
)

data class ArbeidsgiverAktivitet (
        var eraktivitetmarginal: String? = null
)


class MedlemskapA004 : Medlemskap()
class MedlemskapA005 : Medlemskap()
class MedlemskapA006 : Medlemskap()
class MedlemskapA007 : Medlemskap()



@JsonIgnoreProperties(ignoreUnknown = true)
data class MedlemskapA008(
        var endring: EndringA008? = null,
        var bruker: MedlemskapA008Bruker? = null
) : Medlemskap()

data class EndringA008(
        var periode: String? = null,
        var arbeidssted: Adresse? = null,
        var adresse: Adresse? = null,
        var bruker: EndringA008Bruker? = null,
        var trerikraftfra: String? = null,
        var startdato: String? = null,
        var sluttdato: String? = null
)

data class EndringA008Bruker(
        var fornavn: String? = null,
        var etternavn: String? = null
)

data class MedlemskapA008Bruker(
        var arbeidiflereland: ArbeidIFlereLand? = null
)

data class ArbeidIFlereLand(
        var bosted: Bosted? = null,
        var yrkesaktivitet: Yrkesaktivitet? = null
)

data class Bosted(
        var land: String? = null
)

data class Yrkesaktivitet(
        var startdato: String? = null
)



@JsonIgnoreProperties(ignoreUnknown = true)
data class MedlemskapA009 (
        var utsendingsland: Utsendingsland? = null,
        var andreland: Utsendingsland? = null,
        var vedtak: VedtakA009? = null
) : Medlemskap()

data class Utsendingsland (
        var arbeidsgiver: List<Arbeidsgiver>? = null
)

data class VedtakA009 (
        var artikkelforordning: String? = null,
        var datoforrigevedtak: String? = null,
        var erendringsvedtak: String? = null,
        var gjelderperiode: Periode? = null,
        var gjeldervarighetyrkesaktivitet: String? = null,
        var land: String? = null,
        var eropprinneligvedtak: String? = null
)

data class Identifikator (
        var id: String? = null,
        var type: String? = null
)




data class MedlemskapA010  (

        var andreland: Utsendingsland? = null,

        var vedtak: VedtakA010? = null,

        var meldingomlovvalg: MeldingOmLovvalg? = null
) : Medlemskap()

data class VedtakA010 (
        var gjeldervarighetyrkesaktivitet: String? = null,
        var eropprinneligvedtak: String? = null,
        var gjelderperiode: PeriodeA003? = null,
        var land: String? = null
)

data class MeldingOmLovvalg (
        var artikkel: String? = null
)


class MedlemskapA011 : Medlemskap()
class MedlemskapA012 : Medlemskap()

@JsonIgnoreProperties(ignoreUnknown = true)
class NoType : Medlemskap()
