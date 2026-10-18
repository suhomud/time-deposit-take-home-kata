package org.ikigaidigital.application.port

import org.ikigaidigital.application.domain.Withdrawal

interface WithdrawalRepository {
    fun findByTimeDepositIds(ids: Collection<Int>): List<Withdrawal>
}
