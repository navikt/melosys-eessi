---
name: eux-rina-sed-mapping
description: |
  Expert knowledge of NAV SED to RINA SED mapping in eux-rina-api (CDM 4.4).
  Use when: (1) Creating or modifying SED mappers in melosys-eessi,
  (2) Mocking eux-rina-api responses for e2e or local testing,
  (3) Understanding the RINA template JSON structure and $nav/$medlemskap prefixes,
  (4) Debugging SED mapping issues between NAV format and EESSI format,
  (5) Understanding which fields map to which RINA paths for specific SED types (A001-A012, X001-X008).
---

# EUX-RINA SED Mapping

## Architecture Overview

```
melosys-api/melosys-eessi (NAV SED)
         │
         ▼ SedMapper.mapTilSed()
   SED { nav: Nav, medlemskap: Medlemskap }
         │
         ▼ JSON serialization
         │
    eux-rina-api (NAV SED → RINA SED)
         │
         ▼ Template-based transformation
   RINA SED (EU format)
         │
         ▼
       RINA
```

## Key Concepts

| Term | Description |
|------|-------------|
| NAV SED | Internal NAV format with `nav` and `medlemskap` objects |
| RINA SED | EU-standardized format used by EESSI |
| CDM | Common Data Model - EESSI schema version (current: 4.4) |
| Templates | JSON in eux-rina-api defining field mappings |

## Template Location

eux-rina-api templates:
```
src/main/resources/sedtemplates/v44/
├── legislationapplicable/   # A001-A012
├── administrative/          # X001-X013, X050, X100
├── horizontal/              # H001-H131
├── family/                  # F001-F027
├── pension/                 # P-series
└── awod/                    # DA-series
```

## Template Syntax

Templates use placeholder prefixes:

| Prefix | Source | Description |
|--------|--------|-------------|
| `$nav.` | `SED.nav` | Person, employer, work location |
| `$medlemskap.` | `SED.medlemskap` | Decision, period, legislation |
| `[x]` | Array index | Maps arrays (e.g., `pin[x]`) |

## Common Mappings

**Person identification**:
```
$nav.bruker.person.fornavn           → forename
$nav.bruker.person.etternavn         → familyName
$nav.bruker.person.foedselsdato      → dateBirth
$nav.bruker.person.kjoenn            → sex.value
$nav.bruker.person.pin[x].identifikator → personalIdentificationNumber
$nav.bruker.person.pin[x].land       → country.value
```

**Employer**:
```
$nav.arbeidsgiver[x].navn            → name
$nav.arbeidsgiver[x].adresse.gate    → Address.street
$nav.arbeidsgiver[x].adresse.by      → Address.town
$nav.arbeidsgiver[x].adresse.land    → Address.country.value
$nav.arbeidsgiver[x].identifikator[x].id → IdentificationNumber.number
```

**Decision (A003)**:
```
$medlemskap.vedtak.land              → memberStateWhichLegislationApplies
$medlemskap.vedtak.gjelderperiode.startdato → FixedPeriod.startDate
$medlemskap.vedtak.gjelderperiode.sluttdato → FixedPeriod.endDate
```

## SED Types for Melosys

| SED | Purpose | Key medlemskap fields |
|-----|---------|----------------------|
| A001 | Exception request (Art. 16) | `unntak`, `soeknadsperiode` |
| A002 | Refusal of exception | Response to A001 |
| A003 | Determination (Art. 13) | `vedtak`, `andreland` |
| A004 | Objection | `avvisningsgrunn` |
| A005 | Notification | `vedtak` |
| A008 | Change notification | `endring` |
| A011 | Accept exception | Response to A001 |
| A012 | Confirmation | Simple confirm |
| X008 | Invalidation | `nav.sak.ugyldiggjoere` |

## Mocking for E2E/Local Testing

Mock RINA SED structure:
```json
{
  "A003": {
    "Person": {
      "PersonIdentification": {
        "forename": "Ola",
        "familyName": "Nordmann",
        "dateBirth": "1980-01-15"
      }
    },
    "DecisionLegislationApplicable": {
      "memberStateWhichLegislationApplies": { "value": ["NO"] },
      "PeriodForWhichDecisionApplies": {
        "FixedPeriod": {
          "startDate": "2024-01-01",
          "endDate": "2024-12-31"
        }
      }
    }
  }
}
```

## melosys-eessi Mapper Pattern

```kotlin
class A003Mapper : LovvalgSedMapper<MedlemskapA003> {
    override fun getSedType() = SedType.A003

    override fun getMedlemskap(sedData: SedDataDto) = MedlemskapA003(
        vedtak = getVedtak(sedData),      // → $medlemskap.vedtak.*
        andreland = getAndreLand(sedData)
    )
}

interface SedMapper {
    fun mapTilSed(sedData: SedDataDto) = SED(
        nav = prefillNav(sedData),        // → $nav.*
        medlemskap = getMedlemskap(),
        sedType = getSedType().name
    )
}
```

## References

- **[nav-sed-structure.md](references/nav-sed-structure.md)**: NAV SED DTO classes
- **[rina-templates.md](references/rina-templates.md)**: RINA template patterns
- **[field-mapping.md](references/field-mapping.md)**: Complete field mapping
