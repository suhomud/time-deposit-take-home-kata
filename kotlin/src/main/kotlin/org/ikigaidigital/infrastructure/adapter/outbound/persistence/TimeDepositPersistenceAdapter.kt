package org.ikigaidigital.infrastructure.adapter.outbound.persistence

import org.ikigaidigital.TimeDeposit
import org.ikigaidigital.application.port.TimeDepositRepository
import org.ikigaidigital.infrastructure.adapter.outbound.persistence.entity.TimeDepositEntity
import org.ikigaidigital.infrastructure.adapter.outbound.persistence.repository.TimeDepositJdbcRepository
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode

@Component
class TimeDepositPersistenceAdapter(
    private val repository: TimeDepositJdbcRepository,
) : TimeDepositRepository {
    override fun findAll(): List<TimeDeposit> = repository.findAll().map { it.toDomain() }

    override fun findAllForUpdate(): List<TimeDeposit> = repository.findAllForUpdate().map { it.toDomain() }

    override fun updateBalances(deposits: List<TimeDeposit>) {
        deposits.forEach { deposit ->
            repository.updateBalance(
                id = deposit.id,
                balance =
                    BigDecimal
                        .valueOf(deposit.balance)
                        .setScale(2, RoundingMode.HALF_UP),
            )
        }
    }

    private fun TimeDepositEntity.toDomain(): TimeDeposit =
        TimeDeposit(
            id = requireNotNull(id),
            planType = planType,
            // Compatibility trade-off: the shared model uses Double,
            balance = balance.toDouble(),
            days = days,
        )
}
