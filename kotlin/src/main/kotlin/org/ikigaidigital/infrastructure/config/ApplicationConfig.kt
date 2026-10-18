package org.ikigaidigital.infrastructure.config

import org.ikigaidigital.TimeDepositCalculator
import org.ikigaidigital.application.port.TimeDepositRepository
import org.ikigaidigital.application.port.TransactionRunner
import org.ikigaidigital.application.port.WithdrawalRepository
import org.ikigaidigital.application.usecase.GetTimeDepositsUseCase
import org.ikigaidigital.application.usecase.UpdateBalancesUseCase
import org.ikigaidigital.infrastructure.adapter.outbound.transaction.SpringTransactionRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate

@Configuration(proxyBeanMethods = false)
class ApplicationConfig {
    @Bean
    fun getTimeDepositsUseCase(
        timeDeposits: TimeDepositRepository,
        withdrawals: WithdrawalRepository,
    ): GetTimeDepositsUseCase = GetTimeDepositsUseCase(timeDeposits, withdrawals)

    @Bean
    fun updateBalancesUseCase(
        repository: TimeDepositRepository,
        calculator: TimeDepositCalculator,
        transactions: TransactionRunner,
    ): UpdateBalancesUseCase = UpdateBalancesUseCase(repository, calculator, transactions)

    @Bean
    fun timeDepositCalculator() = TimeDepositCalculator()

    @Bean
    fun transactionRunner(transactionManager: PlatformTransactionManager): TransactionRunner =
        SpringTransactionRunner(
            TransactionTemplate(transactionManager),
        )
}
