# RINA SED Templates (CDM 4.4)

## Template Structure

Each template defines how NAV SED fields map to RINA SED structure:

```json
{
  "SED_TYPE": {
    "RINA_Section": {
      "RINA_Field": "$nav.path.to.field"
    }
  }
}
```

## A001 - Exception Request (Art. 16)

```json
{
  "A001": {
    "Person": {
      "PersonIdentification": {
        "forename": "$nav.bruker.person.fornavn",
        "familyName": "$nav.bruker.person.etternavn",
        "dateBirth": "$nav.bruker.person.foedselsdato",
        "sex": { "value": ["$nav.bruker.person.kjoenn"] },
        "PINPersonInEachInstitution": {
          "PersonalIdentificationNumber": [{
            "personalIdentificationNumber": "$nav.bruker.person.pin[x].identifikator",
            "country": { "value": ["$nav.bruker.person.pin[x].land"] }
          }]
        }
      }
    },
    "IdentificationEmployer": {
      "Employer": [{
        "name": "$nav.arbeidsgiver[x].navn",
        "Address": {
          "street": "$nav.arbeidsgiver[x].adresse.gate",
          "town": "$nav.arbeidsgiver[x].adresse.by",
          "postalCode": "$nav.arbeidsgiver[x].adresse.postnummer",
          "country": { "value": ["$nav.arbeidsgiver[x].adresse.land"] }
        }
      }]
    },
    "RequestedPeriod": {
      "startDate": "$medlemskap.soeknadsperiode.startdato",
      "endDate": "$medlemskap.soeknadsperiode.sluttdato"
    },
    "MoreInformation": {
      "justification": "$medlemskap.unntak.begrunnelse",
      "specialCircumstances": { "value": ["$medlemskap.unntak.spesielleomstendigheter.type"] }
    },
    "ApplicableLegislation": {
      "applicableLegislationCurrentSituation": { "value": ["$medlemskap.naavaerendemedlemskap[x].land"] },
      "applicableLegislationRequestedSituation": { "value": ["$medlemskap.forespurtmedlemskap[x].land"] }
    }
  }
}
```

## A003 - Determination (Art. 13)

```json
{
  "A003": {
    "Person": {
      "PersonIdentification": {
        "forename": "$nav.bruker.person.fornavn",
        "familyName": "$nav.bruker.person.etternavn",
        "dateBirth": "$nav.bruker.person.foedselsdato",
        "sex": { "value": ["$nav.bruker.person.kjoenn"] }
      }
    },
    "DecisionLegislationApplicable": {
      "memberStateWhichLegislationApplies": { "value": ["$medlemskap.vedtak.land"] },
      "PeriodForWhichDecisionApplies": {
        "FixedPeriod": {
          "startDate": "$medlemskap.vedtak.gjelderperiode.startdato",
          "endDate": "$medlemskap.vedtak.gjelderperiode.sluttdato"
        },
        "OpenPeriod": {
          "startDate": "$medlemskap.vedtak.gjelderperiode.aapenperiode.startdato",
          "typeOpenPeriod": { "value": ["$medlemskap.vedtak.gjelderperiode.aapenperiode.type"] }
        }
      },
      "Decision": {
        "InitialDecision": { "value": ["$medlemskap.vedtak.eropprinneligvedtak"] },
        "AmendingDecision": {
          "InitialDecision": { "value": ["$medlemskap.vedtak.erendringsvedtak"] },
          "datePreviousDecision": "$medlemskap.vedtak.datoforrigevedtak"
        }
      }
    },
    "EmployerInMemberStateWhichLegislationApplies": {
      "Employer": [{
        "name": "$nav.arbeidsgiver[x].navn",
        "Address": {
          "street": "$nav.arbeidsgiver[x].adresse.gate",
          "town": "$nav.arbeidsgiver[x].adresse.by",
          "country": { "value": ["$nav.arbeidsgiver[x].adresse.land"] }
        }
      }]
    },
    "EmployerInOtherMemberStateConcerned": {
      "Employer": [{
        "name": "$medlemskap.andreland.arbeidsgiver[x].navn",
        "Address": {
          "country": { "value": ["$medlemskap.andreland.arbeidsgiver[x].adresse.land"] }
        }
      }]
    },
    "PlaceWork": {
      "CountriesWork": [{
        "countryWork": { "value": ["$nav.arbeidsland[x].land"] },
        "PlaceWork": {
          "PlaceWork": [{
            "companyNameVesselName": "$nav.arbeidsland[x].arbeidssted[x].navn",
            "Address": {
              "street": "$nav.arbeidsland[x].arbeidssted[x].adresse.gate",
              "town": "$nav.arbeidsland[x].arbeidssted[x].adresse.by"
            }
          }]
        }
      }],
      "aFixedPlaceWorkExistsIndicator": { "value": ["$nav.harfastarbeidssted"] }
    },
    "ArticleRegulationECNo8832004Or9872009": {
      "articleRegulationECNo8832004Or9872009": { "value": ["$medlemskap.relevantartikkelfor8832004eller9872009"] }
    }
  }
}
```

## A008 - Change Notification

```json
{
  "A008": {
    "Person": {
      "PersonIdentification": {
        "forename": "$nav.bruker.person.fornavn",
        "familyName": "$nav.bruker.person.etternavn"
      }
    },
    "PurposeofSED": {
      "NotificationChangesInRelevantData": {
        "Person": {
          "forename": "$medlemskap.endring.fornavn",
          "familyName": "$medlemskap.endring.etternavn"
        },
        "Address": {
          "street": "$medlemskap.endring.adresse.gate",
          "town": "$medlemskap.endring.adresse.by",
          "country": { "value": ["$medlemskap.endring.adresse.land"] }
        },
        "ChangeInPeriod": {
          "actualStartingDate": "$medlemskap.endring.periode.startdato",
          "actualEndDate": "$medlemskap.endring.periode.sluttdato",
          "periodChange": { "value": ["$medlemskap.endring.ihhtparagraf"] }
        },
        "changeEffectiveFrom": "$medlemskap.endring.trerikraftfra",
        "Cancellation": {
          "cancellationWholePeriod": { "value": ["$medlemskap.kansellering.heleperioden.EESSIYesNoType"] }
        }
      },
      "InformationWorkingInTwoOrMoreMemberStates": {
        "PlaceWork": {
          "CountriesWork": [{
            "countryWork": { "value": ["$nav.arbeidsland[x].land"] }
          }]
        }
      }
    }
  }
}
```

## A012 - Confirmation

```json
{
  "A012": {
    "Person": {
      "PersonIdentification": {
        "forename": "$nav.bruker.person.fornavn",
        "familyName": "$nav.bruker.person.etternavn",
        "dateBirth": "$nav.bruker.person.foedselsdato",
        "sex": { "value": ["$nav.bruker.person.kjoenn"] },
        "PINPersonInEachInstitution": {
          "PersonalIdentificationNumber": [{
            "personalIdentificationNumber": "$nav.bruker.person.pin[x].identifikator",
            "country": { "value": ["$nav.bruker.person.pin[x].land"] }
          }]
        }
      }
    },
    "AdditionalInformation": {
      "additionalInformation": "$nav.ytterligereinformasjon"
    },
    "CompetentInstitutionIfDiffersFromSending": {
      "institutionID": "$nav.eessisak.institusjonsid",
      "institutionName": "$nav.eessisak.institusjonsnummer"
    }
  }
}
```

## X008 - Invalidation

```json
{
  "X008": {
    "CaseContext": {
      "PersonContext": {
        "forename": "$nav.sak.kontekst.bruker.person.fornavn",
        "familyName": "$nav.sak.kontekst.bruker.person.etternavn",
        "dateBirth": "$nav.sak.kontekst.bruker.person.foedselsdato",
        "sex": { "value": ["$nav.sak.kontekst.bruker.person.kjoenn"] }
      },
      "EmployerContext": {
        "name": "$nav.sak.kontekst.arbeidsgiver.navn",
        "Address": {
          "street": "$nav.sak.kontekst.arbeidsgiver.adresse.gate"
        }
      }
    },
    "TheFollowingSEDConsideredInvalidWithdrawn": {
      "SEDType": "$nav.sak.ugyldiggjoere.sed.type",
      "dateIssued": "$nav.sak.ugyldiggjoere.sed.utstedelsesdato",
      "reason": { "value": ["$nav.sak.ugyldiggjoere.sed.grunn.type"] },
      "pleaseProvideMoreDetailsIf99OtherSelected": "$nav.sak.ugyldiggjoere.sed.grunn.annet"
    }
  }
}
```

## Common Patterns

### Array Mapping with [x]

When a field contains `[x]`, it indicates array iteration:
```json
"$nav.bruker.person.pin[x].identifikator"
```
Maps to each element in the `pin` array.

### Value Wrapper

Enum/code fields use a `value` array wrapper:
```json
"sex": { "value": ["$nav.bruker.person.kjoenn"] }
"country": { "value": ["$nav.bruker.adresse[x].land"] }
```

### Nested Structure

Deep nesting follows the object hierarchy:
```json
"$nav.arbeidsland[x].arbeidssted[x].adresse.by"
```
Navigates: nav → arbeidsland[i] → arbeidssted[j] → adresse → by

### Date Format

All dates are ISO format: `YYYY-MM-DD`
