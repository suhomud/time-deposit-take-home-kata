package org.ikigaidigital.application.usecase

import org.assertj.core.api.Assertions.assertThat
import org.ikigaidigital.TimeDeposit
import org.ikigaidigital.TimeDepositCalculator
import org.ikigaidigital.application.port.TimeDepositRepository
import org.ikigaidigital.application.port.TransactionRunner
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.mock

class UpdateBalancesUseCaseTest {
    @Test
    fun `reads locked deposits and persists calculated balances within one transaction`() {
        val repository = mock(TimeDepositRepository::class.java)
        val deposits =
            listOf(
                TimeDeposit(1, "basic", 12000.0, 31),
                TimeDeposit(2, "premium", 12000.0, 45),
            )
        var transactionActive = false
        val transactions =
            TransactionRunner { block ->
                transactionActive = true
                try {
                    block()
                } finally {
                    transactionActive = false
                }
            }
        doAnswer {
            assertThat(transactionActive).isTrue()
            deposits
        }.`when`(repository).findAllForUpdate()
        doAnswer {
            assertThat(transactionActive).isTrue()
            assertThat(deposits.map { it.balance }).containsExactly(12010.0, 12000.0)
            null
        }.`when`(repository).updateBalances(deposits)
        val useCase = UpdateBalancesUseCase(repository, TimeDepositCalculator(), transactions)

        useCase.execute()

        val order = inOrder(repository)
        order.verify(repository).findAllForUpdate()
        order.verify(repository).updateBalances(deposits)
        order.verifyNoMoreInteractions()
        assertThat(transactionActive).isFalse()
    }
}
