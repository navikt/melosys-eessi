Melosys-eessi
========================
Applikasjon for Lovvalgs-domenet som er en del av EESSI-2 prosjeket.

Melosys-eessi er en mikrotjeneste utviklet av Team Melosys, og har som oppgave å håndtere all integrasjon videre mot eux.
Dette innebærer mapping til SED for å sende videre til eux og journalføring/opprettelse av sak/oppgave for både innkommende
og utgående SED'er.

# Utviklingsoppsett

Klon repositoriet og sett det opp som et standard Maven-prosjekt i foretrukket IDE.
Deretter kjør opp en postgres-instans i terminalen med kommando:
```
docker run -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=su -d --rm postgres
```
<br>


For å aksessere postgres-databasen i dev må man først koble seg mot og logge inn i vault gjennom kommandolinjen. <br>
Dokumentasjon hvordan det gjøres finnes her: https://github.com/navikt/utvikling/blob/main/docs/teknisk/Vault.md

# Kjøre applikasjonen lokalt
melosys-eessi kan kjøres opp som en ren Spring-applikasjon, appen heter "MelosysEessiApplication". <br>
For å kjøre applikasjonen lokalt er det viktig at du setter profilen til `local-mock`.

## Test av mottak av SED-er på topic fra EUX
For å teste mottak av SED-er på topic fra EUX kan du benytte deg av `lag-sed-controller` som kan kjøres via
http://localhost:8083/swagger-ui/#/lag-sed-controller/lagSakUsingPOST. <br>
JSON for `requestDto` finner du i [melosys-docker-compose](https://github.com/navikt/melosys-docker-compose) under
mock/src/main/resources/eux:
- [sedHendelse_A001.json](../melosys-docker-compose/mock/src/main/resources/eux/sedHendelse_A001.json)
- [sedHendelse_A003.json](../melosys-docker-compose/mock/src/main/resources/eux/sedHendelse_A003.json)
- [sedHendelse_A009.json](../melosys-docker-compose/mock/src/main/resources/eux/sedHendelse_A009.json)
- [sedHendelse_A009_over2aar1dag.json](../melosys-docker-compose/mock/src/main/resources/eux/sedHendelse_A009_over2aar1dag.json)


## Spørsmål knyttet til koden eller prosjektet kan rettes mot:

* [Team Melosys](https://github.com/orgs/navikt/teams/melosys)

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen [#team-melosys](https://nav-it.slack.com/messages/C92481HSP/)
