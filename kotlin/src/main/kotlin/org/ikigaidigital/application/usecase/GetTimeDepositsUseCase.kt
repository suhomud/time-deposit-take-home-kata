package org.ikigaidigital.application.usecase

import org.ikigaidigital.application.port.TimeDepositRepository
import org.ikigaidigital.application.port.WithdrawalRepository

class GetTimeDepositsUseCase(
    private val timeDeposits: TimeDepositRepository,
    private val withdrawals: WithdrawalRepository,
) {
    fun execute(): List<TimeDepositDetails> {
        val deposits = timeDeposits.findAll()

        if (deposits.isEmpty()) {
            return emptyList()
        }

        val withdrawalsByDeposit =
            withdrawals
                .findByTimeDepositIds(deposits.map { it.id })
                .groupBy { it.timeDepositId }

        return deposits
            .sortedBy { it.id }
            .map { deposit ->
                TimeDepositDetails(
                    deposit = deposit,
                    withdrawals = withdrawalsByDeposit[deposit.id].orEmpty(),
                )
            }
    }
}
