package org.ikigaidigital.application.domain.interest

import org.assertj.core.api.Assertions.assertThat
import org.ikigaidigital.TimeDeposit
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class InterestPolicyTest {
    @Test
    fun `uses the supplied calculation with the deposit and configured rate`() {
        val policy =
            InterestPolicy(
                annualRate = 0.1,
                eligibleDays = 46..90,
                calculation = InterestCalculation { deposit, rate -> deposit.balance * rate + 10.0 },
            )
        val deposit = TimeDeposit(1, "premium", 1000.0, 60)

        val interest = policy.calculateInterest(deposit)

        assertThat(interest).isEqualTo(110.0)
        assertThat(deposit.balance).isEqualTo(1000.0)
    }

    @ParameterizedTest
    @ValueSource(ints = [45, 91])
    fun `does not invoke the calculation outside eligible days`(days: Int) {
        val policy =
            InterestPolicy(
                annualRate = 0.1,
                eligibleDays = 46..90,
                calculation = InterestCalculation { _, _ -> error("Ineligible deposits must not earn interest") },
            )

        val interest = policy.calculateInterest(TimeDeposit(1, "premium", 1000.0, days))

        assertThat(interest).isZero()
    }
}
