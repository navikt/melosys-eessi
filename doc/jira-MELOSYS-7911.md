# MELOSYS-7911 - Støtte for rammeavtale om fjernarbeid (TWFA) i CDM 4.4

## Bakgrunn

I CDM 4.4 er det lagt til et felt som muliggjør markering av avtalen om fjernarbeid. Formålet med dette er å forenkle muligheten for automatisk behandling av saker under avtalen om fjernarbeid (Telework Framework Agreement - TWFA)

## Løsningsbeskrivelse

### 1) Case Owner (CO) - Anmodning om unntak

Sending av A001 skal fungere som normalt i CDM 4.4-versjonen.

#### 1.1) CO - Sending av A001 (Telework Framework Agreement)

Når NO er CO (Case Owner) bør det legges til støtte i Melosys slik at saksbehandler kan markere at dette gjelder fjernarbeid som faller innunder TWFA. Markering av fjernarbeid skal bare kunne settes i SEDen når artikkel 13(1)a velges først i kombinasjon på vedtakssteget (under "Artikkelen det søkes unntak fra").

På den måten settes det **"(1) Ja"** pkt. 8.3 i SED A001 som sendes fra RINA. Dersom dette implementeres må det også testes at A001 i PDF-utkast og journalført PDF-versjon viser endringen korrekt.

### 2) Counterparty (CP)

Når NO er CP bør det legges til støtte i Melosys slik at Melosys håndterer mottak av SED A001 som normalt i CDM 4.4-versjon.

#### 2.1) CP

Dersom mottak og sending av A001 i CDM 4.4-versjon fungerer godt er det også ønskelig å automatisk behandle forespørsler om unntak der rammeavtalen om fjernarbeid (TWFA) er markert.

## Akseptansekriterier

### Sakseier / CO

**1.** **Når** jeg som saksbehandler skal sende en anmodning om unntak der NO er sakseier
**så** skal obligatoriske felter fylles ut automatisk fra behandlingen
**og** det skal være mulig å se forhåndsvisning av SEDen
før den sendes via RINA og journalføres på saken.

**2.** **Når** jeg som saksbehandler skal sende en anmodning om unntak der art. 13(1)(a) velges i kombinasjon med fjernarbeid etter TWFA
**så** skal pkt 8.3.1 i SEDen "Rammeavtale for fjernarbeid": Markeres med **"[1] Ja"**
og det skal være mulig å se forhåndsvisning av SEDen
før den sendes via RINA og journalføres på saken.

### Mottakerpart / CP

**1. Når** Melosys mottar en A001 i CDM 4.4-versjon
**så** skal manuell behandling om anmodning om unntak opprettes som normalt
**og** SED A011 skal sendes automatisk ved saksbehandlers godkjenning,
samt unntaksperioden med hjemmel skal lagres i medl.

**2. Når** Melosys mottar en A001 i CDM 4.4-versjon
der saksbehandler avslår anmodningen **så** skal Melosys sende SED A002
og seden skal journalføres automatisk på saken,
samt unntaksperioden med hjemmel skal lagres i medl.

**3. Når** Melosys mottar en A001 i CDM 4.4-versjon
der saksbehandler delvis innvilger anmodning så skal Melosys sende SED A002 med den forkortede perioden
og SEDen skal journalføres automatisk på saken,
samt unntaksperioden med hjemmel skal lagres i medl.

Forutsatt at automatisering av SEDer etter rammeavtalen om fjernarbeid (TWFA) er på plass (kan skilles ut i egen oppgave):

**4. Når** Melosys mottar en A001 i CDM 4.4-versjon fra et land som har signert TWFA
**der** rammeavtale for fjernarbeid pkt. 8.3.1 er markert med **"[1] Ja"**
i kombinasjon med art. 13(1)(a) i pkt. 8.1,
**og** perioden det gjelder ikke er lengre enn 3 år, samt starter på eller etter ikrafttredelsesdato for landet,
**så** skal Melosys automatisk sende SED A002, dersom det er mulig og perioden skal automatisk registreres i medl.
Dersom art. 13(1)(a) ikke er satt i pkt. 8.1, eller "[1] Ja" ikke er markert, skal SEDen generere manuell behandling.

**5. Når** Melosys mottar en A001 i CDM 4.4-versjon fra et land som har signert TWFA
**der** rammeavtale for fjernarbeid pkt. 8.3.1 er markert med **"[0] Nei"**
i kombinasjon med art. 13(1)(a) i pkt. 8.1,
**så** skal Melosys generere en manuell behandling som normalt.

## EU/EØS-land inkludert Sveits som har signert avtalen

| Land | Ikrafttredelsesdato |
|------|---------------------|
| Østerrike | 01.07.2023 |
| Belgia | 01.07.2023 |
| Kroatia | 01.07.2023 |
| Tsjekkia | 01.07.2023 |
| Estland | 01.02.2026 |
| Finland | 01.07.2023 |
| Frankrike | 01.07.2023 |
| Tyskland | 01.07.2023 |
| Irland | 01.06.2024 |
| Italia | 01.01.2024 |
| Liechtenstein | 01.07.2023 |
| Litauen | 01.05.2024 |
| Luxembourg | 01.07.2023 |
| Malta | 01.07.2023 |
| Nederland | 01.07.2023 |
| Norge | 01.07.2023 |
| Polen | 01.07.2023 |
| Portugal | 01.07.2023 |
| Slovakia | 01.07.2023 |
| Slovenia | 01.09.2023 |
| Spania | 01.07.2023 |
| Sverige | 01.07.2023 |
| Sveits | 01.07.2023 |
