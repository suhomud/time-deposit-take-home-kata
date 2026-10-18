package org.ikigaidigital.application.domain.interest

import org.ikigaidigital.TimeDeposit

fun interface InterestCalculation {
    fun calculate(
        deposit: TimeDeposit,
        annualRate: Double,
    ): Double
}

object SimpleMonthlyInterestCalculation : InterestCalculation {
    private const val MONTHS_PER_YEAR = 12

    override fun calculate(
        deposit: TimeDeposit,
        annualRate: Double,
    ): Double = deposit.balance * annualRate / MONTHS_PER_YEAR
}
