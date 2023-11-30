Melosys-eessi
========================
Applikasjon for Lovvalgs-domenet som er en del av EESSI-2 prosjeket.

Melosys-eessi er en mikrotjeneste utviklet av Team Melosys, og har som oppgave å håndtere all integrasjon videre mot eux.
Dette innebærer mapping til SED for å sende videre til eux og journalføring/opprettelse av sak/oppgave for både innkommende
og utgående SED'er.

# Utviklingsoppsett

Klon repositoriet og sett det opp som et standard Maven-prosjekt i foretrukket IDE.


Sett først miljøvariabler:
```
SRV_USERNAME
SRV_PASSWORD
KAFKA_BOOTSTRAP_SERVERS
```

Disse kan fås enten på Vault eller ved å spørre på slack-kanal #melosys-utvikling.

Du må også kjøre opp en postgers-instans. Det kan gjøres gjennom følgende docker-kommando:
```
docker run -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=su -d --rm postgres
```

For å aksessere postgres-databasen i preprod må man først koble seg mot og logge inn i vault gjennom kommandolinjen.
Dokumentasjon hvordan det gjøres finnes her: https://github.com/navikt/utvikling/blob/main/docs/teknisk/Vault.md

## Kjøre applikasjonen lokalt
### Skaff deg en fungerende versjon av NAVs SSL-truststore
Dette er en fil som heter noe á la `nav_truststore_nonproduction_ny2.jts`, og ett tilhørende passord
som JVM-en trenger for å lese innholdet. Det fins flere versjoner av den i omløp på Fasit, og de endres av og til.
Vanligvis er det enkleste å kopiere fila og passordet fra ett annet NAV-prosjekt.

### Kjøring i IDE
For å kjøre i din IDE, opprett en kjørekonfigurasjon for SpringBoot-main-klassen, `MelosysEessiApplication` med følgende systemegenskaper (`Run Configurations` | `Arguments`| `VM Arguments` i Eclipse):
```
-Dspring.profiles.active=local
-DSRV_USERNAME=${SRV_USER_NAME}
-DSRV_PASSWORD=${SRV_PASSWORD}
-DKAFKA_BOOTSTRAP_SERVERS=${KAFKA_BOOTSTRAP_SERVERS}
-DPG_HOST=e34apvl00253.devillo.no
-Djavax.net.ssl.trustStore=${TRUSTSTORE}
-Djavax.net.ssl.trustStorePassword=${TRUSTSTORE_PASSWORD}
```
Her er alle uttrykkene som ser ut som dereferering av miljøvariable/systemeegenskaper, e.g. `${SRV_USER_NAME}`,
ment som placeholders som må erstattes med faktisk verdi (evt. propageres fra prosessmiljøet rundt din IDE).
Du trenger ikke å sette `PG_HOST` dersom du kan kjøre Docker på din egen maskin (localhost).

Det er også mulig å sette alle innstillingene globalt for JVM-en, for å dele konfigurasjon mellom kjørekonfigurasjoner. I Eclipse
er dette på `Window | Preferences | Java | Installed JREs | <JRE> | Edit | Default VM arguments`.

# Henvendelser

Spørsmål knyttet til koden eller prosjektet kan rettes mot:

* [Team Melosys](https://github.com/orgs/navikt/teams/melosys)

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen [#team-melosys](https://nav-it.slack.com/messages/C92481HSP/)
