package no.nav.melosys.eessi.models.bucinfo

data class BucInfo(
    var applicationRoleId: String? = null,
    var id: String? = null,
    var processDefinitionId: String? = null,
    var status: String? = null
) {
    fun norgeErCaseOwner(): Boolean = PROCESS_OWNER.equals(applicationRoleId, ignoreCase = true)

    fun bucErÅpen(): Boolean = "open".equals(status, ignoreCase = true)

    companion object {
        private const val PROCESS_OWNER = "PO"
    }
}
