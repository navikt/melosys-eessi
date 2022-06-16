# SED Mottak
```mermaid
flowchart
    sedMottatt --> sedAlleredeBehandlet{Er sed allerede behandlet?}
    sedAlleredeBehandlet --ja--> allederedeBehandlet(X)
    sedAlleredeBehandlet --nei--> lagreHendelse

    lagreHendelse --> hentSED
    hentSED --> identifiserPerson{Er person identifisert?}

    identifiserPerson -- identifisert --> erRinasakIdentifisert{Er rinasak identifisert?}
    erRinasakIdentifisert --allerede identifisert--> rinasakIdentifisert(X)
    erRinasakIdentifisert --ikke identifisert--> publiserEventBucIdentifisert

    identifiserPerson --ikke identifisert--> finnesOppgaveAllerede{Finnes ID oppgave?}
    finnesOppgaveAllerede --finnes allerede--> oppgaveFinnesAllerede(X)
    finnesOppgaveAllerede --finnes ikke--> opprettJP
    opprettJP --> opprettOppgaveIdFordeling
```

