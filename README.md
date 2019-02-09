Melosys-eessi
========================
Applikasjon for Lovvalgs-domenet som er en del av EESSI-2 prosjeket. 

Melosys-eessi er en mikrotjeneste utviklet av Team Melosys, og har som oppgave å håndtere all integrasjon videre mot eux. 
Dette innebærer mapping til SED for å sende videre til eux og journalføring/opprettelse av sak/oppgave for både innkommende
og utgående SED'er.

# Utviklingsoppsett

Klon repositoriet og sett det opp som et standard Maven-prosjekt i foretrukket IDE. 

Eneste konfigurasjon som trengs er å sette miljøvariabler:

```
EUXAPP_URL=
RESTSTS_URL=
SRV_USERNAME=
SRV_PASSWORD=
```
 
Disse kan fås enten på Vault eller ved å spørre på slack-kanal #melosys-utvikling

# Henvendelser

Spørsmål knyttet til koden eller prosjektet kan rettes mot:

* [Team Melosys](https://github.com/orgs/navikt/teams/Melosys)  

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen [#melosys-utvikling](https://nav-it.slack.com/messages/C92481HSP/)         