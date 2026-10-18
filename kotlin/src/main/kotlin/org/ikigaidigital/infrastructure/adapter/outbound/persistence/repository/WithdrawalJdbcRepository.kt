package org.ikigaidigital.infrastructure.adapter.outbound.persistence.repository

import org.ikigaidigital.infrastructure.adapter.outbound.persistence.entity.WithdrawalEntity
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface WithdrawalJdbcRepository : CrudRepository<WithdrawalEntity, Int> {
    @Query(
        """
        SELECT id, "timeDepositId", amount, date
        FROM withdrawals
        WHERE "timeDepositId" IN (:ids)
        ORDER BY id
        """,
    )
    fun findByDepositIds(
        @Param("ids") ids: Collection<Int>,
    ): List<WithdrawalEntity>
}
