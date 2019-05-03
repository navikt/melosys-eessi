package no.nav.melosys.eessi.models.bucinfo;

import lombok.Data;

@Data
public class BucInfo {
    private String applicationRoleId;
    private String id;
    private String processDefinitionId;
    private String status;
}
