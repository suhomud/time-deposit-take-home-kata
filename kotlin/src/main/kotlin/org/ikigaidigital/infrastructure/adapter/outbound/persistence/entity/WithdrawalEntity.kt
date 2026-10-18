package org.ikigaidigital.infrastructure.adapter.outbound.persistence.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDate

@Table("withdrawals")
data class WithdrawalEntity(
    @Id
    val id: Int? = null,
    @Column("timeDepositId")
    val timeDepositId: Int,
    val amount: BigDecimal,
    val date: LocalDate,
)
