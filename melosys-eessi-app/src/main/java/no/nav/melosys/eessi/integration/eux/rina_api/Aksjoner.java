package no.nav.melosys.eessi.integration.eux.rina_api;

public enum Aksjoner {
    ADD_SUBDOCUMENT("Add_Subdocument"),
    ARCHIVECASE("ArchiveCase"),
    ATTACHMENT_ADDED("Attachment_Added"),
    BACKUPCASE("BackupCase"),
    CLOSE("Close"),
    CREATE("Create"),
    CREATELETTER("CreateLetter"),
    DELETE("Delete"),
    DELETECASE("DeleteCase"),
    IMPORT_SUBDOCUMENT("Import_Subdocument"),
    LOCALCLOSE("LocalClose"),
    LOCALREOPEN("LocalReopen"),
    READ("Read"),
    READPARTICIPANTS("ReadParticipants"),
    REMOVE_SUBDOCUMENT("Remove_Subdocument"),
    REOPEN("Reopen"),
    REQUEST_APPROVAL("Request_Approval"),
    RESTORECASE("RestoreCase"),
    SELECTPARTICIPANTS("SelectParticipants"),
    SEND("Send"),
    SENDPARTICIPANTS("SendParticipants"),
    SUBDOCUMENT("Subdocument"),
    UPDATE("Update"),
    UPDATEPARTICIPANTS("UpdateParticipants"),
    UPDATE_SUBDOCUMENT("Update_Subdocument");

    private final String handling;

    Aksjoner(String handling) {
        this.handling = handling;
    }

    public String hentHandling() {
        return this.handling;
    }


}
