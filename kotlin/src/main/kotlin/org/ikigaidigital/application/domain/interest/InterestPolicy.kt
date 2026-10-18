package org.ikigaidigital.application.domain.interest

import org.ikigaidigital.TimeDeposit

private const val NO_INTEREST = 0.0

data class InterestPolicy(
    val annualRate: Double,
    val eligibleDays: IntRange,
    val calculation: InterestCalculation = SimpleMonthlyInterestCalculation,
) {
    fun calculateInterest(deposit: TimeDeposit): Double =
        if (deposit.days in eligibleDays) {
            calculation.calculate(deposit, annualRate)
        } else {
            NO_INTEREST
        }
}
