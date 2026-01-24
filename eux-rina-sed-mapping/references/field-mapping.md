# Complete Field Mapping Reference

## Person Identification

| NAV Path | RINA Path | Notes |
|----------|-----------|-------|
| `$nav.bruker.person.fornavn` | `Person.PersonIdentification.forename` | |
| `$nav.bruker.person.etternavn` | `Person.PersonIdentification.familyName` | |
| `$nav.bruker.person.foedselsdato` | `Person.PersonIdentification.dateBirth` | YYYY-MM-DD |
| `$nav.bruker.person.kjoenn` | `Person.PersonIdentification.sex.value` | m/k/u |
| `$nav.bruker.person.etternavnvedfoedsel` | `Person.PersonIdentification.familyNameAtBirth` | |
| `$nav.bruker.person.fornavnvedfoedsel` | `Person.PersonIdentification.forenameAtBirth` | |

### PIN (Personal Identification Number)

| NAV Path | RINA Path |
|----------|-----------|
| `$nav.bruker.person.pin[x].identifikator` | `PINPersonInEachInstitution.PersonalIdentificationNumber.personalIdentificationNumber` |
| `$nav.bruker.person.pin[x].land` | `PINPersonInEachInstitution.PersonalIdentificationNumber.country.value` |
| `$nav.bruker.person.pin[x].sektor` | `PINPersonInEachInstitution.PersonalIdentificationNumber.sector.value` |
| `$nav.bruker.person.pin[x].institusjonsid` | `PINPersonInEachInstitution.PersonalIdentificationNumber.Institution.institutionID` |
| `$nav.bruker.person.pin[x].institusjonsnavn` | `PINPersonInEachInstitution.PersonalIdentificationNumber.Institution.institutionName` |

### Nationality

| NAV Path | RINA Path |
|----------|-----------|
| `$nav.bruker.person.statsborgerskap[x].land` | `Person.AdditionalInformationPerson.nationality.value` |

### Birth Place

| NAV Path | RINA Path |
|----------|-----------|
| `$nav.bruker.person.foedested.by` | `IfPINNotProvidedForEveryInstitutionPleaseProvide.PlaceBirth.town` |
| `$nav.bruker.person.foedested.land` | `IfPINNotProvidedForEveryInstitutionPleaseProvide.PlaceBirth.country.value` |
| `$nav.bruker.person.foedested.region` | `IfPINNotProvidedForEveryInstitutionPleaseProvide.PlaceBirth.region` |

### Parents

| NAV Path | RINA Path |
|----------|-----------|
| `$nav.bruker.far.person.fornavn` | `IfPINNotProvidedForEveryInstitutionPleaseProvide.forenameFather` |
| `$nav.bruker.far.person.etternavnvedfoedsel` | `IfPINNotProvidedForEveryInstitutionPleaseProvide.fatherFamilyNameAtBirth` |
| `$nav.bruker.mor.person.fornavn` | `IfPINNotProvidedForEveryInstitutionPleaseProvide.forenameMother` |
| `$nav.bruker.mor.person.etternavnvedfoedsel` | `IfPINNotProvidedForEveryInstitutionPleaseProvide.motherFamilyNameAtBirth` |

## Address

| NAV Path | RINA Path |
|----------|-----------|
| `$nav.bruker.adresse[x].gate` | `Address.street` |
| `$nav.bruker.adresse[x].bygning` | `Address.buildingName` |
| `$nav.bruker.adresse[x].by` | `Address.town` |
| `$nav.bruker.adresse[x].postnummer` | `Address.postalCode` |
| `$nav.bruker.adresse[x].region` | `Address.region` |
| `$nav.bruker.adresse[x].land` | `Address.country.value` |
| `$nav.bruker.adresse[x].type` | `Address.type.value` |

## Employer

| NAV Path | RINA Path |
|----------|-----------|
| `$nav.arbeidsgiver[x].navn` | `Employer.name` |
| `$nav.arbeidsgiver[x].adresse.gate` | `Employer.Address.street` |
| `$nav.arbeidsgiver[x].adresse.bygning` | `Employer.Address.buildingName` |
| `$nav.arbeidsgiver[x].adresse.by` | `Employer.Address.town` |
| `$nav.arbeidsgiver[x].adresse.postnummer` | `Employer.Address.postalCode` |
| `$nav.arbeidsgiver[x].adresse.region` | `Employer.Address.region` |
| `$nav.arbeidsgiver[x].adresse.land` | `Employer.Address.country.value` |
| `$nav.arbeidsgiver[x].identifikator[x].id` | `Employer.IdentificationNumbers.IdentificationNumber.number` |
| `$nav.arbeidsgiver[x].identifikator[x].type` | `Employer.IdentificationNumbers.IdentificationNumber.type.value` |

## Self-Employment

| NAV Path | RINA Path |
|----------|-----------|
| `$nav.selvstendig.arbeidsgiver[x].navn` | `SelfEmployment.name` |
| `$nav.selvstendig.arbeidsgiver[x].adresse.*` | `SelfEmployment.Address.*` |
| `$nav.selvstendig.arbeidsgiver[x].identifikator[x].*` | `SelfEmployment.IdentificationNumbers.*` |

## Work Location (Place of Work)

| NAV Path | RINA Path |
|----------|-----------|
| `$nav.arbeidsland[x].land` | `PlaceWork.CountriesWork.countryWork.value` |
| `$nav.arbeidsland[x].arbeidssted[x].navn` | `PlaceWork.CountriesWork.PlaceWork.PlaceWork.companyNameVesselName` |
| `$nav.arbeidsland[x].arbeidssted[x].adresse.*` | `PlaceWork.CountriesWork.PlaceWork.PlaceWork.Address.*` |
| `$nav.arbeidsland[x].arbeidssted[x].hjemmebase` | `PlaceWork.CountriesWork.PlaceWork.PlaceWork.flagStatehomeBase` |
| `$nav.harfastarbeidssted` | `PlaceWork.aFixedPlaceWorkExistsIndicator.value` |

## Decision/Vedtak (A003)

| NAV Path | RINA Path |
|----------|-----------|
| `$medlemskap.vedtak.land` | `DecisionLegislationApplicable.memberStateWhichLegislationApplies.value` |
| `$medlemskap.vedtak.gjelderperiode.startdato` | `DecisionLegislationApplicable.PeriodForWhichDecisionApplies.FixedPeriod.startDate` |
| `$medlemskap.vedtak.gjelderperiode.sluttdato` | `DecisionLegislationApplicable.PeriodForWhichDecisionApplies.FixedPeriod.endDate` |
| `$medlemskap.vedtak.gjelderperiode.aapenperiode.startdato` | `DecisionLegislationApplicable.PeriodForWhichDecisionApplies.OpenPeriod.startDate` |
| `$medlemskap.vedtak.gjelderperiode.aapenperiode.type` | `DecisionLegislationApplicable.PeriodForWhichDecisionApplies.OpenPeriod.typeOpenPeriod.value` |
| `$medlemskap.vedtak.eropprinneligvedtak` | `DecisionLegislationApplicable.Decision.InitialDecision.value` |
| `$medlemskap.vedtak.erendringsvedtak` | `DecisionLegislationApplicable.Decision.AmendingDecision.InitialDecision.value` |
| `$medlemskap.vedtak.datoforrigevedtak` | `DecisionLegislationApplicable.Decision.AmendingDecision.datePreviousDecision` |
| `$medlemskap.vedtak.gjeldervarighetyrkesaktivitet` | `DecisionLegislationApplicable.theDecisionAppliesForDurationActivityIndicator.value` |

## Exception Request (A001)

| NAV Path | RINA Path |
|----------|-----------|
| `$medlemskap.soeknadsperiode.startdato` | `RequestedPeriod.startDate` |
| `$medlemskap.soeknadsperiode.sluttdato` | `RequestedPeriod.endDate` |
| `$medlemskap.unntak.begrunnelse` | `MoreInformation.justification` |
| `$medlemskap.unntak.spesielleomstendigheter.type` | `MoreInformation.specialCircumstances.value` |
| `$medlemskap.unntak.startdatoansattforsikret` | `MoreInformation.dateWhichEmployeeFirstInsuredInSendingCountry` |
| `$medlemskap.unntak.startdatokontraktansettelse` | `MoreInformation.dateWhichContractWithSendingEmployerStarted` |
| `$medlemskap.unntak.a1grunnlag` | `MoreInformation.pDA1IssuedBasisArticle.value` |
| `$medlemskap.naavaerendemedlemskap[x].land` | `ApplicableLegislation.applicableLegislationCurrentSituation.value` |
| `$medlemskap.forespurtmedlemskap[x].land` | `ApplicableLegislation.applicableLegislationRequestedSituation.value` |
| `$medlemskap.anmodning.erendring` | `MoreInformation.TypeRequest.amendmentIndicator.value` |
| `$medlemskap.forordning8832004.unntak.grunnlag.artikkel` | `RequestedForSituationsCoveredByArti8832004.regulationArticlesSituationAgreementExceptionProposed.value` |

## Change Notification (A008)

| NAV Path | RINA Path |
|----------|-----------|
| `$medlemskap.endring.fornavn` | `PurposeofSED.NotificationChangesInRelevantData.Person.forename` |
| `$medlemskap.endring.etternavn` | `PurposeofSED.NotificationChangesInRelevantData.Person.familyName` |
| `$medlemskap.endring.adresse.*` | `PurposeofSED.NotificationChangesInRelevantData.Address.*` |
| `$medlemskap.endring.arbeidssted.*` | `PurposeofSED.NotificationChangesInRelevantData.PlaceWorkInHostCountry.*` |
| `$medlemskap.endring.periode.startdato` | `PurposeofSED.NotificationChangesInRelevantData.ChangeInPeriod.actualStartingDate` |
| `$medlemskap.endring.periode.sluttdato` | `PurposeofSED.NotificationChangesInRelevantData.ChangeInPeriod.actualEndDate` |
| `$medlemskap.endring.ihhtparagraf` | `PurposeofSED.NotificationChangesInRelevantData.ChangeInPeriod.periodChange.value` |
| `$medlemskap.endring.trerikraftfra` | `PurposeofSED.NotificationChangesInRelevantData.changeEffectiveFrom` |
| `$medlemskap.kansellering.heleperioden.EESSIYesNoType` | `PurposeofSED.NotificationChangesInRelevantData.Cancellation.cancellationWholePeriod.value` |

## Invalidation (X008)

| NAV Path | RINA Path |
|----------|-----------|
| `$nav.sak.kontekst.bruker.person.fornavn` | `CaseContext.PersonContext.forename` |
| `$nav.sak.kontekst.bruker.person.etternavn` | `CaseContext.PersonContext.familyName` |
| `$nav.sak.kontekst.bruker.person.foedselsdato` | `CaseContext.PersonContext.dateBirth` |
| `$nav.sak.kontekst.bruker.person.kjoenn` | `CaseContext.PersonContext.sex.value` |
| `$nav.sak.kontekst.arbeidsgiver.navn` | `CaseContext.EmployerContext.name` |
| `$nav.sak.kontekst.arbeidsgiver.adresse.*` | `CaseContext.EmployerContext.Address.*` |
| `$nav.sak.ugyldiggjoere.sed.type` | `TheFollowingSEDConsideredInvalidWithdrawn.SEDType` |
| `$nav.sak.ugyldiggjoere.sed.utstedelsesdato` | `TheFollowingSEDConsideredInvalidWithdrawn.dateIssued` |
| `$nav.sak.ugyldiggjoere.sed.grunn.type` | `TheFollowingSEDConsideredInvalidWithdrawn.reason.value` |
| `$nav.sak.ugyldiggjoere.sed.grunn.annet` | `TheFollowingSEDConsideredInvalidWithdrawn.pleaseProvideMoreDetailsIf99OtherSelected` |

## Other Countries (andreland) in A003

| NAV Path | RINA Path |
|----------|-----------|
| `$medlemskap.andreland.arbeidsgiver[x].navn` | `EmployerInOtherMemberStateConcerned.Employer.name` |
| `$medlemskap.andreland.arbeidsgiver[x].adresse.*` | `EmployerInOtherMemberStateConcerned.Employer.Address.*` |
| `$medlemskap.andreland.arbeidsgiver[x].identifikator[x].*` | `EmployerInOtherMemberStateConcerned.Employer.IdentificationNumbers.*` |
| `$medlemskap.andreland.arbeidsgiveraktivitet.eraktivitetmarginal` | `EmployerInOtherMemberStateConcerned.activityMarginalAccordingArticle145bEC9872009Indicator.value` |

## Additional Information

| NAV Path | RINA Path |
|----------|-----------|
| `$nav.ytterligereinformasjon` | `AdditionalInformation.additionalInformation` |
| `$nav.eessisak.institusjonsid` | `CompetentInstitutionIfDiffersFromSending.institutionID` |
| `$nav.eessisak.institusjonsnummer` | `CompetentInstitutionIfDiffersFromSending.institutionName` |

## Article References

| NAV Value | RINA Value | Description |
|-----------|------------|-------------|
| `12_1` | `12_1` | Posted workers |
| `12_2` | `12_2` | Self-employed posting |
| `13_1_a` | `13_1_a` | Work in multiple states (employed) |
| `13_1_b_i` | `13_1_b_i` | Residence state rule |
| `13_2_a` | `13_2_a` | Self-employed multiple states |
| `16_1` | `16_1` | Exception agreement |

## Value Mappings

### Yes/No (EESSIYesNoType)
- `ja` → `yes`
- `nei` → `no`

### Sex
- `m` → `male`
- `k` → `female`
- `u` → `unknown`

### Address Type
- `bosted` → `residence`
- `kontakt` → `contact`
- `opphold` → `temporary`
