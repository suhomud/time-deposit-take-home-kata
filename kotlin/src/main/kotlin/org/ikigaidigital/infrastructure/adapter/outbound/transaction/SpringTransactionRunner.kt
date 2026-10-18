package org.ikigaidigital.infrastructure.adapter.outbound.transaction

import org.ikigaidigital.application.port.TransactionRunner
import org.springframework.transaction.support.TransactionTemplate

class SpringTransactionRunner(
    private val template: TransactionTemplate,
) : TransactionRunner {
    override fun run(block: () -> Unit) {
        template.executeWithoutResult {
            block()
        }
    }
}
