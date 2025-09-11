package no.nav.melosys.eessi.controller.dto

import io.swagger.v3.oas.annotations.media.Schema

data class ResendSedListeDto(
    @Schema(description = "Liste over sedId (også kalt setIdentifier) som skal sendes på nytt", example = "[\"0123456_0a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d_1\", \"6543210_d5c4b3a2f1e0d9c8b7a6f5e4d3c2b1a0_2\"]")
    val sedIds: List<String>
)
