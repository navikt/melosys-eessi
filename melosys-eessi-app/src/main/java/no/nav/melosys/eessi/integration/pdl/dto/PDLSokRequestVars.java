// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.integration.pdl.dto;

import java.util.Collection;

public final class PDLSokRequestVars {
    private final PDLPaging paging;
    private final Collection<PDLSokCriterion> criteria;

    @java.lang.SuppressWarnings("all")
    public PDLSokRequestVars(final PDLPaging paging, final Collection<PDLSokCriterion> criteria) {
        this.paging = paging;
        this.criteria = criteria;
    }

    @java.lang.SuppressWarnings("all")
    public PDLPaging getPaging() {
        return this.paging;
    }

    @java.lang.SuppressWarnings("all")
    public Collection<PDLSokCriterion> getCriteria() {
        return this.criteria;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof PDLSokRequestVars)) return false;
        final PDLSokRequestVars other = (PDLSokRequestVars) o;
        final java.lang.Object this$paging = this.getPaging();
        final java.lang.Object other$paging = other.getPaging();
        if (this$paging == null ? other$paging != null : !this$paging.equals(other$paging)) return false;
        final java.lang.Object this$criteria = this.getCriteria();
        final java.lang.Object other$criteria = other.getCriteria();
        if (this$criteria == null ? other$criteria != null : !this$criteria.equals(other$criteria)) return false;
        return true;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $paging = this.getPaging();
        result = result * PRIME + ($paging == null ? 43 : $paging.hashCode());
        final java.lang.Object $criteria = this.getCriteria();
        result = result * PRIME + ($criteria == null ? 43 : $criteria.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "PDLSokRequestVars(paging=" + this.getPaging() + ", criteria=" + this.getCriteria() + ")";
    }
}
