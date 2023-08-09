package no.nav.melosys.eessi.models.kafkadlq;

/**
 * Dette er en enum for å representere forskjellige typer av Kafka-meldinger.
 * Vær oppmerksom på at hver enum-verdi har en tilsvarende strengrepresentasjon som brukes i `@DiscriminatorValue`-annoteringen.
 * Hvis du endrer navnet på en enum-verdi, må du også endre strengen i `@DiscriminatorValue`-annoteringen.
 * Se eksempel:
 *
 * @Entity
 * @DiscriminatorValue("SED_MOTTATT_HENDELSE")
 * public class SedMottattHendelseDLQ extends KafkaDLQ {
 * ...
 * }
 *
 * I dette tilfellet, hvis du endrer SED_MOTTATT_HENDELSE til noe annet, må du også endre "SED_MOTTATT_HENDELSE" i `@DiscriminatorValue`-annoteringen.
 */
public enum QueueType {
    SED_MOTTATT_HENDELSE,
    SED_SENDT_HENDELSE,
    OPPGAVE_ENDRET_HENDELSE_AIVEN
}
