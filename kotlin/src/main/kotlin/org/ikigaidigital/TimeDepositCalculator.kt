package org.ikigaidigital

import org.ikigaidigital.application.domain.interest.InterestPolicies
import org.ikigaidigital.application.domain.interest.InterestPolicy
import org.ikigaidigital.application.domain.interest.PlanType
import java.math.BigDecimal
import java.math.RoundingMode

class TimeDepositCalculator(
    private val policies: Map<PlanType, InterestPolicy>,
) {
    constructor() : this(InterestPolicies.defaults())

    fun updateBalance(xs: List<TimeDeposit>) {
        xs.forEach { deposit ->
            val interest =
                PlanType
                    .from(deposit.planType)
                    ?.let(policies::get)
                    ?.calculateInterest(deposit)
                    ?: 0.0

            val roundedInterest =
                BigDecimal(interest)
                    .setScale(2, RoundingMode.HALF_UP)

            deposit.balance += roundedInterest.toDouble()
        }
    }
}