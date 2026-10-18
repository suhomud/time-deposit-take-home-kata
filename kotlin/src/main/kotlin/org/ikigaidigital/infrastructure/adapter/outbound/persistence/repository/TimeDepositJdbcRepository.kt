package org.ikigaidigital.infrastructure.adapter.outbound.persistence.repository

import org.ikigaidigital.infrastructure.adapter.outbound.persistence.entity.TimeDepositEntity
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.math.BigDecimal

interface TimeDepositJdbcRepository : CrudRepository<TimeDepositEntity, Int> {
    @Query(
        """
        SELECT id, "planType", balance, days
        FROM "timeDeposits"
        ORDER BY id
        FOR UPDATE
        """,
    )
    fun findAllForUpdate(): List<TimeDepositEntity>

    @Modifying
    @Query(
        """
        UPDATE "timeDeposits"
        SET balance = :balance
        WHERE id = :id
        """,
    )
    fun updateBalance(
        @Param("id") id: Int,
        @Param("balance") balance: BigDecimal,
    ): Int
}
