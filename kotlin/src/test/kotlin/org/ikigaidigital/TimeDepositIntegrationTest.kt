package org.ikigaidigital

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.ikigaidigital.application.domain.Withdrawal
import org.ikigaidigital.application.port.TimeDepositRepository
import org.ikigaidigital.application.port.TransactionRunner
import org.ikigaidigital.application.port.WithdrawalRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.json.JsonCompareMode
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer
import java.math.BigDecimal
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class TimeDepositIntegrationTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        private val jdbc: JdbcTemplate,
        private val timeDeposits: TimeDepositRepository,
        private val withdrawals: WithdrawalRepository,
        private val transactions: TransactionRunner,
    ) {
        companion object {
            @Container
            @ServiceConnection
            @JvmField
            val postgres = PostgreSQLContainer("postgres:17-alpine")
        }

        @BeforeEach
        fun clearDatabase() {
            jdbc.execute("""TRUNCATE withdrawals, "timeDeposits" RESTART IDENTITY""")
        }

        @Test
        fun `retrieves all deposits with the required fields and their withdrawal history`() {
            val basicId = insertDeposit("basic", "12000.00", 31)
            val studentId = insertDeposit("student", "5000.25", 365)
            val premiumId = insertDeposit("premium", "20000.00", 46)
            val studentWithdrawalId = insertWithdrawal(studentId, "250.50", LocalDate.of(2026, 1, 10))
            val firstBasicWithdrawalId = insertWithdrawal(basicId, "100.25", LocalDate.of(2026, 1, 11))
            val secondBasicWithdrawalId = insertWithdrawal(basicId, "50.00", LocalDate.of(2026, 1, 12))

            mockMvc
                .perform(get("/api/time-deposits"))
                .andExpect(status().isOk)
                .andExpect(
                    content().json(
                        """
                        [
                          {
                            "id": $basicId,
                            "planType": "basic",
                            "balance": 12000.00,
                            "days": 31,
                            "withdrawals": [
                              {"id": $firstBasicWithdrawalId, "amount": 100.25, "date": "2026-01-11"},
                              {"id": $secondBasicWithdrawalId, "amount": 50.00, "date": "2026-01-12"}
                            ]
                          },
                          {
                            "id": $studentId,
                            "planType": "student",
                            "balance": 5000.25,
                            "days": 365,
                            "withdrawals": [
                              {"id": $studentWithdrawalId, "amount": 250.50, "date": "2026-01-10"}
                            ]
                          },
                          {
                            "id": $premiumId,
                            "planType": "premium",
                            "balance": 20000.00,
                            "days": 46,
                            "withdrawals": []
                          }
                        ]
                        """.trimIndent(),
                        JsonCompareMode.STRICT,
                    ),
                )
        }

        @Test
        fun `updates all balances according to the original rules and preserves other data`() {
            jdbc.execute(
                """
                INSERT INTO "timeDeposits" ("planType", balance, days) VALUES
                    ('basic', 12000.00, 30),
                    ('basic', 12000.00, 31),
                    ('student', 12000.00, 30),
                    ('student', 12000.00, 31),
                    ('student', 12000.00, 365),
                    ('student', 12000.00, 366),
                    ('premium', 12000.00, 45),
                    ('premium', 12000.00, 46)
                """.trimIndent(),
            )
            insertWithdrawal(2, "250.50", LocalDate.of(2026, 1, 10))
            val originalMetadata = jdbc.queryForList("""SELECT id, "planType", days FROM "timeDeposits" ORDER BY id""")
            val originalWithdrawals = jdbc.queryForList("SELECT * FROM withdrawals ORDER BY id")

            mockMvc
                .perform(post("/api/time-deposits/balance-updates"))
                .andExpect(status().isNoContent)
                .andExpect(content().string(""))

            assertThat(storedBalances()).containsExactly(
                BigDecimal("12000.00"),
                BigDecimal("12010.00"),
                BigDecimal("12000.00"),
                BigDecimal("12030.00"),
                BigDecimal("12030.00"),
                BigDecimal("12000.00"),
                BigDecimal("12000.00"),
                BigDecimal("12050.00"),
            )
            assertThat(jdbc.queryForList("""SELECT id, "planType", days FROM "timeDeposits" ORDER BY id"""))
                .isEqualTo(originalMetadata)
            assertThat(jdbc.queryForList("SELECT * FROM withdrawals ORDER BY id"))
                .isEqualTo(originalWithdrawals)
            mockMvc
                .perform(get("/api/time-deposits"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.length()").value(8))
                .andExpect(jsonPath("$[1].balance").value(12010.0))
        }

        @Test
        fun `both endpoints support an empty database`() {
            mockMvc
                .perform(get("/api/time-deposits"))
                .andExpect(status().isOk)
                .andExpect(content().json("[]", JsonCompareMode.STRICT))

            mockMvc
                .perform(post("/api/time-deposits/balance-updates"))
                .andExpect(status().isNoContent)
                .andExpect(content().string(""))

            assertThat(storedBalances()).isEmpty()
        }

        @Test
        fun `fetches withdrawals only for the requested deposits`() {
            val basicId = insertDeposit("basic", "12000.00", 31)
            val studentId = insertDeposit("student", "5000.00", 365)
            val withdrawalId = insertWithdrawal(basicId, "250.50", LocalDate.of(2026, 1, 10))
            insertWithdrawal(studentId, "100.00", LocalDate.of(2026, 1, 11))

            val result = withdrawals.findByTimeDepositIds(listOf(basicId))

            assertThat(result).containsExactly(
                Withdrawal(withdrawalId, basicId, BigDecimal("250.50"), LocalDate.of(2026, 1, 10)),
            )
        }

        @Test
        fun `rolls back persisted balance changes if the transaction fails`() {
            insertDeposit("basic", "12000.00", 31)
            insertDeposit("student", "5000.00", 365)

            assertThatThrownBy {
                transactions.run {
                    val deposits = timeDeposits.findAllForUpdate()
                    TimeDepositCalculator().updateBalance(deposits)
                    timeDeposits.updateBalances(deposits)
                    assertThat(storedBalances()).containsExactly(BigDecimal("12010.00"), BigDecimal("5012.50"))
                    throw IllegalStateException("Abort balance update")
                }
            }.isInstanceOf(IllegalStateException::class.java)
                .hasMessage("Abort balance update")

            assertThat(storedBalances()).containsExactly(BigDecimal("12000.00"), BigDecimal("5000.00"))
        }

        @Test
        fun `OpenAPI documents exactly the two required operations`() {
            mockMvc
                .perform(get("/v3/api-docs"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.paths.length()").value(2))
                .andExpect(jsonPath("$.paths['/api/time-deposits'].length()").value(1))
                .andExpect(jsonPath("$.paths['/api/time-deposits'].get.responses['200']").exists())
                .andExpect(jsonPath("$.paths['/api/time-deposits/balance-updates'].length()").value(1))
                .andExpect(jsonPath("$.paths['/api/time-deposits/balance-updates'].post.responses['204']").exists())
        }

        private fun insertDeposit(
            planType: String,
            balance: String,
            days: Int,
        ): Int =
            requireNotNull(
                jdbc.queryForObject(
                    """INSERT INTO "timeDeposits" ("planType", balance, days) VALUES (?, ?, ?) RETURNING id""",
                    Int::class.java,
                    planType,
                    BigDecimal(balance),
                    days,
                ),
            )

        private fun insertWithdrawal(
            timeDepositId: Int,
            amount: String,
            date: LocalDate,
        ): Int =
            requireNotNull(
                jdbc.queryForObject(
                    """INSERT INTO withdrawals ("timeDepositId", amount, date) VALUES (?, ?, ?) RETURNING id""",
                    Int::class.java,
                    timeDepositId,
                    BigDecimal(amount),
                    date,
                ),
            )

        private fun storedBalances(): List<BigDecimal> =
            jdbc
                .queryForList("""SELECT balance FROM "timeDeposits" ORDER BY id""", BigDecimal::class.java)
                .map { requireNotNull(it) }
    }
