package org.ikigaidigital

import org.assertj.core.api.Assertions.assertThat
import org.ikigaidigital.application.domain.interest.InterestPolicy
import org.ikigaidigital.application.domain.interest.PlanType
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class TimeDepositCalculatorTest {
    private val calculator = TimeDepositCalculator()

    @ParameterizedTest(name = "{0} at day {1} has balance {2} after updating")
    @CsvSource(
        "basic, 30, 12000.0",
        "basic, 31, 12010.0",
        "student, 30, 12000.0",
        "student, 31, 12030.0",
        "student, 365, 12030.0",
        "student, 366, 12000.0",
        "premium, 45, 12000.0",
        "premium, 46, 12050.0",
    )
    fun `preserves interest rates and eligibility boundaries`(
        planType: String,
        days: Int,
        expectedBalance: Double,
    ) {
        val deposit = TimeDeposit(1, planType, 12000.0, days)

        calculator.updateBalance(listOf(deposit))

        assertThat(deposit.balance).isEqualTo(expectedBalance)
    }

    @ParameterizedTest(name = "basic balance {0} becomes {1} using legacy rounding")
    @CsvSource(
        "0.0, 0.0",
        "6.0, 6.01",
        "18.0, 18.01",
        "30.0, 30.02",
        "12006.0, 12016.01",
        "1234567.0, 1235595.81",
    )
    fun `preserves rounding of interest calculated with Double`(
        balance: Double,
        expectedBalance: Double,
    ) {
        val deposit = TimeDeposit(1, "basic", balance, 31)

        calculator.updateBalance(listOf(deposit))

        assertThat(deposit.balance).isEqualTo(expectedBalance)
    }

    @ParameterizedTest
    @CsvSource(
        "unknown",
        "BASIC",
    )
    fun `leaves unrecognized plan names unchanged`(planType: String) {
        val deposit = TimeDeposit(1, planType, 12000.0, 60)

        calculator.updateBalance(listOf(deposit))

        assertThat(deposit.balance).isEqualTo(12000.0)
    }

    @Test
    fun `updates deposits in place without changing their other fields or sharing interest`() {
        val deposits =
            listOf(
                TimeDeposit(1, "basic", 12000.0, 31),
                TimeDeposit(2, "premium", 12000.0, 45),
                TimeDeposit(3, "student", 12000.0, 365),
            )

        calculator.updateBalance(deposits)

        assertThat(deposits).containsExactly(
            TimeDeposit(1, "basic", 12010.0, 31),
            TimeDeposit(2, "premium", 12000.0, 45),
            TimeDeposit(3, "student", 12030.0, 365),
        )
    }

    @Test
    fun `applies interest to the current balance on every call`() {
        val deposit = TimeDeposit(1, "basic", 12000.0, 31)

        calculator.updateBalance(listOf(deposit))

        assertThat(deposit.balance).isEqualTo(12010.0)

        calculator.updateBalance(listOf(deposit))

        assertThat(deposit.balance).isEqualTo(12020.01)
    }

    @Test
    fun `uses an injected interest policy`() {
        val policy = InterestPolicy(annualRate = 0.12, eligibleDays = 31..365)
        val customCalculator = TimeDepositCalculator(mapOf(PlanType.BASIC to policy))
        val deposit = TimeDeposit(1, "basic", 12000.0, 31)

        customCalculator.updateBalance(listOf(deposit))

        assertThat(deposit.balance).isEqualTo(12120.0)
    }

    @Test
    fun `accepts an empty list`() {
        calculator.updateBalance(emptyList())
    }
}
