package no.nav.melosys.eessi.integration.pdl.dto;

import java.util.Collection;

import lombok.Value;

@Value
public class PDLSokRequestVars {
    PDLPaging paging;
    Collection<PDLSokCriterion> criteria;
}
