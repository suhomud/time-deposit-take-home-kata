package org.ikigaidigital.application.domain.interest

object InterestPolicies {
    fun defaults(): Map<PlanType, InterestPolicy> =
        mapOf(
            PlanType.BASIC to
                InterestPolicy(
                    annualRate = 0.01,
                    eligibleDays = 31..Int.MAX_VALUE,
                ),
            PlanType.STUDENT to
                InterestPolicy(
                    annualRate = 0.03,
                    eligibleDays = 31..365,
                ),
            PlanType.PREMIUM to
                InterestPolicy(
                    annualRate = 0.05,
                    eligibleDays = 46..Int.MAX_VALUE,
                ),
        )
}
