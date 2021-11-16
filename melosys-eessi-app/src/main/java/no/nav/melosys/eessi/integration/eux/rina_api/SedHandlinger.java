package no.nav.melosys.eessi.integration.eux.rina_api;

public enum SedHandlinger {
    Add_Subdocument("Add_Subdocument"),
    ArchiveCase("ArchiveCase"),
    Attachment_Added("Attachment_Added"),
    BackupCase("BackupCase"),
    Close("Close"),
    Create("Create"),
    CreateLetter("CreateLetter"),
    Delete("Delete"),
    DeleteCase("DeleteCase"),
    Import_Subdocument("Import_Subdocument"),
    LocalClose("LocalClose"),
    LocalReopen("LocalReopen"),
    Read("Read"),
    ReadParticipants("ReadParticipants"),
    Remove_Subdocument("Remove_Subdocument"),
    Reopen("Reopen"),
    Request_Approval("Request_Approval"),
    RestoreCase("RestoreCase"),
    SelectParticipants("SelectParticipants"),
    Send("Send"),
    SendParticipants("SendParticipants"),
    Subdocument("Subdocument"),
    Update("Update"),
    UpdateParticipants("UpdateParticipants"),
    Update_Subdocument("Update_Subdocument");

    private final String handling;

    SedHandlinger(String handling) {
        this.handling = handling;
    }

    public String hentHandling() {
        return this.handling;
    }


}
