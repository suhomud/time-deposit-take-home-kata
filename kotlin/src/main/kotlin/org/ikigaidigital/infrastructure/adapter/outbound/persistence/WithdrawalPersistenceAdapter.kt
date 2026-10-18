package org.ikigaidigital.infrastructure.adapter.outbound.persistence

import org.ikigaidigital.application.domain.Withdrawal
import org.ikigaidigital.application.port.WithdrawalRepository
import org.ikigaidigital.infrastructure.adapter.outbound.persistence.entity.WithdrawalEntity
import org.ikigaidigital.infrastructure.adapter.outbound.persistence.repository.WithdrawalJdbcRepository
import org.springframework.stereotype.Component

@Component
class WithdrawalPersistenceAdapter(
    private val repository: WithdrawalJdbcRepository,
) : WithdrawalRepository {
    override fun findByTimeDepositIds(ids: Collection<Int>): List<Withdrawal> {
        if (ids.isEmpty()) {
            return emptyList()
        }

        return repository.findByDepositIds(ids).map { it.toDomain() }
    }

    private fun WithdrawalEntity.toDomain(): Withdrawal =
        Withdrawal(
            id = requireNotNull(id),
            timeDepositId = timeDepositId,
            amount = amount,
            date = date,
        )
}
