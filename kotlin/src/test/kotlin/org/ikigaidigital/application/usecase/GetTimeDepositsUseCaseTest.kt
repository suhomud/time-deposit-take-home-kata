package org.ikigaidigital.application.usecase

import org.assertj.core.api.Assertions.assertThat
import org.ikigaidigital.TimeDeposit
import org.ikigaidigital.application.domain.Withdrawal
import org.ikigaidigital.application.port.TimeDepositRepository
import org.ikigaidigital.application.port.WithdrawalRepository
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import java.math.BigDecimal
import java.time.LocalDate

class GetTimeDepositsUseCaseTest {
    private val timeDeposits = mock(TimeDepositRepository::class.java)
    private val withdrawals = mock(WithdrawalRepository::class.java)
    private val useCase = GetTimeDepositsUseCase(timeDeposits, withdrawals)

    @Test
    fun `returns deposits in id order with only their own withdrawals`() {
        val basic = TimeDeposit(1, "basic", 12000.0, 31)
        val student = TimeDeposit(2, "student", 5000.0, 365)
        val premium = TimeDeposit(3, "premium", 20000.0, 46)
        val firstStudentWithdrawal = Withdrawal(1, 2, BigDecimal("250.50"), LocalDate.of(2026, 1, 10))
        val basicWithdrawal = Withdrawal(2, 1, BigDecimal("100.25"), LocalDate.of(2026, 1, 11))
        val secondStudentWithdrawal = Withdrawal(3, 2, BigDecimal("50.00"), LocalDate.of(2026, 1, 12))
        `when`(timeDeposits.findAll()).thenReturn(listOf(premium, basic, student))
        `when`(withdrawals.findByTimeDepositIds(listOf(3, 1, 2))).thenReturn(
            listOf(firstStudentWithdrawal, basicWithdrawal, secondStudentWithdrawal),
        )

        val result = useCase.execute()

        assertThat(result).containsExactly(
            TimeDepositDetails(basic, listOf(basicWithdrawal)),
            TimeDepositDetails(student, listOf(firstStudentWithdrawal, secondStudentWithdrawal)),
            TimeDepositDetails(premium, emptyList()),
        )
    }

    @Test
    fun `returns an empty list without fetching withdrawals when there are no deposits`() {
        `when`(timeDeposits.findAll()).thenReturn(emptyList())

        val result = useCase.execute()

        assertThat(result).isEmpty()
        verifyNoInteractions(withdrawals)
    }
}
