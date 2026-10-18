package org.ikigaidigital.infrastructure.adapter.inbound.rest.dto

import org.ikigaidigital.application.domain.Withdrawal
import java.math.BigDecimal
import java.time.LocalDate

data class WithdrawalResponse(
    val id: Int,
    val amount: BigDecimal,
    val date: LocalDate,
)

fun Withdrawal.toApiResponse(): WithdrawalResponse =
    WithdrawalResponse(
        id = id,
        amount = amount,
        date = date,
    )
