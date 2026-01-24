# NAV SED Structure

## SED Class

```kotlin
data class SED(
    var medlemskap: Medlemskap? = null,  // SED-specific data
    var nav: Nav? = null,                 // Common NAV data
    var sedType: String? = null,          // "A001", "A003", etc.
    var sedVer: String? = null,           // CDM version
    var sedGVer: String? = null
)
```

## Nav Object (→ $nav.*)

```kotlin
data class Nav(
    var bruker: Bruker? = null,           // → $nav.bruker.*
    var arbeidsgiver: List<Arbeidsgiver>? = null,  // → $nav.arbeidsgiver[x].*
    var selvstendig: Selvstendig? = null, // → $nav.selvstendig.*
    var arbeidsland: List<Arbeidsland>? = null,    // → $nav.arbeidsland[x].*
    var harfastarbeidssted: String? = null,        // "ja"/"nei"
    var ytterligereinformasjon: String? = null,
    var sak: Sak? = null                  // For X-SEDs → $nav.sak.*
)
```

### Bruker (→ $nav.bruker.*)

```kotlin
data class Bruker(
    var person: Person? = null,           // → $nav.bruker.person.*
    var adresse: List<Adresse>? = null,   // → $nav.bruker.adresse[x].*
    var far: Far? = null,                 // → $nav.bruker.far.*
    var mor: Mor? = null                  // → $nav.bruker.mor.*
)

data class Person(
    var fornavn: String? = null,          // → forename
    var etternavn: String? = null,        // → familyName
    var foedselsdato: String? = null,     // → dateBirth (YYYY-MM-DD)
    var kjoenn: Kjønn? = null,            // → sex.value
    var statsborgerskap: List<Statsborgerskap>? = null,
    var pin: List<Pin>? = null,           // → PersonalIdentificationNumber
    var foedested: Foedested? = null,
    var etternavnvedfoedsel: String? = null,
    var fornavnvedfoedsel: String? = null
)

data class Pin(
    var identifikator: String? = null,    // FNR/D-nr
    var land: String? = null,             // ISO-2 country code
    var sektor: String? = null,
    var institusjonsid: String? = null,
    var institusjonsnavn: String? = null
)
```

### Arbeidsgiver (→ $nav.arbeidsgiver[x].*)

```kotlin
data class Arbeidsgiver(
    var navn: String? = null,
    var adresse: Adresse? = null,
    var identifikator: List<Identifikator>? = null
)

data class Identifikator(
    var id: String? = null,               // Org.nr
    var type: String? = null              // "registrering"
)
```

### Arbeidsland (→ $nav.arbeidsland[x].*)

```kotlin
data class Arbeidsland(
    var land: String? = null,             // ISO-2 country code
    var arbeidssted: List<Arbeidssted>? = null
)

data class Arbeidssted(
    var navn: String? = null,
    var adresse: Adresse? = null,
    var hjemmebase: String? = null        // For maritime/aviation
)
```

### Adresse

```kotlin
data class Adresse(
    var gate: String? = null,             // → street
    var bygning: String? = null,          // → buildingName
    var by: String? = null,               // → town
    var postnummer: String? = null,       // → postalCode
    var region: String? = null,           // → region
    var land: String? = null,             // → country.value
    var type: String? = null              // "bosted", "kontakt", "opphold"
)
```

## Medlemskap Classes (→ $medlemskap.*)

Each SED type has its own Medlemskap implementation:

### MedlemskapA003 (Art. 13 Determination)

```kotlin
data class MedlemskapA003(
    var vedtak: VedtakA003? = null,       // → $medlemskap.vedtak.*
    var andreland: Andreland? = null,     // → $medlemskap.andreland.*
    var relevantartikkelfor8832004eller9872009: String? = null,
    var gjeldendereglerEC883: List<String>? = null,
    var isDeterminationProvisional: String? = null
)

data class VedtakA003(
    var land: String? = null,             // Lovvalgsland
    var gjelderperiode: PeriodeA010? = null,
    var gjeldervarighetyrkesaktivitet: String? = null,  // "ja"/"nei"
    var eropprinneligvedtak: String? = null,
    var erendringsvedtak: String? = null,
    var datoforrigevedtak: String? = null
)
```

### MedlemskapA001 (Art. 16 Exception Request)

```kotlin
data class MedlemskapA001(
    var unntak: Unntak? = null,
    var soeknadsperiode: Periode? = null,
    var tidligereperiode: List<Periode>? = null,
    var vertsland: Vertsland? = null,
    var naavaerendemedlemskap: List<Land>? = null,
    var forespurtmedlemskap: List<Land>? = null,
    var forordning8832004: Forordning8832004? = null,
    var anmodning: Anmodning? = null
)
```

### MedlemskapA008 (Change Notification)

```kotlin
data class MedlemskapA008(
    var endring: Endring? = null,         // → $medlemskap.endring.*
    var kansellering: Kansellering? = null,
    var bruker: BrukerA008? = null
)

data class Endring(
    var fornavn: String? = null,
    var etternavn: String? = null,
    var adresse: Adresse? = null,
    var arbeidssted: Arbeidssted? = null,
    var periode: PeriodeEndring? = null,
    var trerikraftfra: String? = null,
    var ihhtparagraf: String? = null
)
```

## SedDataDto (Input to Mappers)

```kotlin
data class SedDataDto(
    var gsakSaksnummer: Long? = null,
    var bruker: BrukerDto,
    var arbeidsgivendeVirksomheter: List<Virksomhet> = emptyList(),
    var selvstendigeVirksomheter: List<Virksomhet> = emptyList(),
    var arbeidsland: List<ArbeidslandDto> = emptyList(),
    var lovvalgsperioder: List<Lovvalgsperiode> = emptyList(),
    var mottakerIder: List<String>? = null,
    var ytterligereInformasjon: String? = null,
    var bostedsadresse: Adresse? = null,
    var kontaktadresse: Adresse? = null,
    var oppholdsadresse: Adresse? = null,
    var utenlandskIdent: Map<String, String> = emptyMap(),
    var harFastArbeidssted: Boolean? = null,
    var vedtakDto: VedtakDto? = null,
    var familieMedlem: List<FamilieMedlem> = emptyList()
)
```

## Enum Values

### Kjønn (Sex)
- `m` → male
- `k` → female
- `u` → unknown

### Adressetype
- `bosted` → residence
- `kontakt` → contact
- `opphold` → temporary

### Article References (Bestemmelse)
- `12_1` → Art. 12(1) Posted workers
- `12_2` → Art. 12(2) Self-employed
- `13_1_a` → Art. 13(1)(a) Work in multiple states, employed
- `13_1_b_i` → Art. 13(1)(b)(i) Residence state
- `13_2_a` → Art. 13(2)(a) Self-employed multiple states
- `16_1` → Art. 16(1) Exception agreement
