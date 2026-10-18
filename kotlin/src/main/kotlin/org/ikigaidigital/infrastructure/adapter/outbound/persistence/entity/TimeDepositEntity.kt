package org.ikigaidigital.infrastructure.adapter.outbound.persistence.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal

@Table("timeDeposits")
data class TimeDepositEntity(
    @Id
    val id: Int? = null,
    @Column("planType")
    val planType: String,
    val balance: BigDecimal,
    val days: Int,
)
