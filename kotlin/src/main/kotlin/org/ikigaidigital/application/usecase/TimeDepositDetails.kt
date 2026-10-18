package org.ikigaidigital.application.usecase

import org.ikigaidigital.TimeDeposit
import org.ikigaidigital.application.domain.Withdrawal

data class TimeDepositDetails(
    val deposit: TimeDeposit,
    val withdrawals: List<Withdrawal>,
)
