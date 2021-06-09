package no.nav.melosys.eessi.models.bucinfo;

import lombok.Data;

@Data
public class BucInfo {

    private static final String PROCESS_OWNER = "PO";

    private String applicationRoleId;
    private String id;
    private String processDefinitionId;
    private String status;

    public boolean norgeErCaseOwner() {
        return PROCESS_OWNER.equalsIgnoreCase(applicationRoleId);
    }

    public boolean bucErÅpen() {
        return "open".equalsIgnoreCase(status);
    }
}
