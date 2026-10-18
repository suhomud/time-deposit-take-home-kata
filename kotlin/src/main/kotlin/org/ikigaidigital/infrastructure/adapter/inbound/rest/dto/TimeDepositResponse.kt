package org.ikigaidigital.infrastructure.adapter.inbound.rest.dto

import org.ikigaidigital.application.usecase.TimeDepositDetails
import java.math.BigDecimal

data class TimeDepositResponse(
    val id: Int,
    val planType: String,
    val balance: BigDecimal,
    val days: Int,
    val withdrawals: List<WithdrawalResponse>,
)

fun TimeDepositDetails.toApiResponse(): TimeDepositResponse =
    TimeDepositResponse(
        id = deposit.id,
        planType = deposit.planType,
        balance = BigDecimal.valueOf(deposit.balance),
        days = deposit.days,
        withdrawals = withdrawals.map { it.toApiResponse() },
    )
