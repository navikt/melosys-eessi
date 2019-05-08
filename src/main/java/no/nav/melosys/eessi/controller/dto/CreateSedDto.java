package no.nav.melosys.eessi.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateSedDto {
    private String bucId;
    private String sedId;
    private String rinaUrl;
}
