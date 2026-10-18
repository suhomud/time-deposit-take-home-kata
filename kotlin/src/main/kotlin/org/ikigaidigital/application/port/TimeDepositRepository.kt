package org.ikigaidigital.application.port

import org.ikigaidigital.TimeDeposit

interface TimeDepositRepository {
    fun findAll(): List<TimeDeposit>

    fun findAllForUpdate(): List<TimeDeposit>

    fun updateBalances(deposits: List<TimeDeposit>)
}
