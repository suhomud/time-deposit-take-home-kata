package org.ikigaidigital.application.port

fun interface TransactionRunner {
    fun run(block: () -> Unit)
}
