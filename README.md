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
SRV_USERNAME=
SRV_PASSWORD=
```
 
Disse kan fås enten på Vault eller ved å spørre på slack-kanal #melosys-utvikling.

Du må også kjøre opp en postgers-instans. Det kan gjøres gjennom følgende docker-kommando:
```
docker run -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=su -d --rm postgres
```

For å aksessere postgres-databasen i preprod må man først koble seg mot og logge inn i vault gjennom kommandolinjen.
Dokumentasjon hvordan det gjøres finnes her: https://github.com/navikt/utvikling/blob/master/Vault.md


# Henvendelser

Spørsmål knyttet til koden eller prosjektet kan rettes mot:

* [Team Melosys](https://github.com/orgs/navikt/teams/Melosys)  

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen [#melosys-utvikling](https://nav-it.slack.com/messages/C92481HSP/)         