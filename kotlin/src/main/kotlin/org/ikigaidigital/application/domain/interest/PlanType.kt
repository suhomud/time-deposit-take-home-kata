package org.ikigaidigital.application.domain.interest

enum class PlanType(
    val value: String,
) {
    BASIC("basic"),
    STUDENT("student"),
    PREMIUM("premium"),
    ;

    companion object {
        fun from(value: String): PlanType? = entries.firstOrNull { it.value == value }
    }
}
