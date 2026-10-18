package org.ikigaidigital.infrastructure.adapter.inbound.rest

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.ikigaidigital.application.usecase.GetTimeDepositsUseCase
import org.ikigaidigital.application.usecase.UpdateBalancesUseCase
import org.ikigaidigital.infrastructure.adapter.inbound.rest.dto.TimeDepositResponse
import org.ikigaidigital.infrastructure.adapter.inbound.rest.dto.toApiResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/time-deposits")
class TimeDepositController(
    private val getTimeDeposits: GetTimeDepositsUseCase,
    private val updateBalances: UpdateBalancesUseCase,
) {
    @Operation(summary = "Retrieve all time deposits")
    @ApiResponse(responseCode = "200", description = "Deposits retrieved")
    @GetMapping
    fun getAll(): List<TimeDepositResponse> = getTimeDeposits.execute().map { it.toApiResponse() }

    @Operation(summary = "Apply monthly interest to all time deposits")
    @ApiResponse(responseCode = "204", description = "Balances updated")
    @PostMapping("/balance-updates")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateAllBalances() {
        updateBalances.execute()
    }
}
