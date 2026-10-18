package org.ikigaidigital.application.usecase

import org.ikigaidigital.TimeDepositCalculator
import org.ikigaidigital.application.port.TimeDepositRepository
import org.ikigaidigital.application.port.TransactionRunner

class UpdateBalancesUseCase(
    private val repository: TimeDepositRepository,
    private val calculator: TimeDepositCalculator,
    private val transactions: TransactionRunner,
) {
    fun execute() {
        transactions.run {
            val deposits = repository.findAllForUpdate()

            calculator.updateBalance(deposits)

            repository.updateBalances(deposits)
        }
    }
}
