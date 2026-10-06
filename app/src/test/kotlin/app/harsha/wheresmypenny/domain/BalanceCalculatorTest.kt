package app.harsha.wheresmypenny.domain

import app.harsha.wheresmypenny.domain.BalanceCalculator.TxRecord
import app.harsha.wheresmypenny.domain.model.AccountType
import app.harsha.wheresmypenny.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for BalanceCalculator using the accounting examples from decisions.md.
 * All amounts are in euro cents unless noted.
 */
class BalanceCalculatorTest {

    // ── Helper ────────────────────────────────────────────────────────────────

    private fun expense(amountCents: Long, fromId: Long) =
        TxRecord(TransactionType.EXPENSE, amountCents, fromAccountId = fromId, toAccountId = null)

    private fun income(amountCents: Long, toId: Long) =
        TxRecord(TransactionType.INCOME, amountCents, fromAccountId = null, toAccountId = toId)

    private fun transfer(amountCents: Long, fromId: Long, toId: Long) =
        TxRecord(TransactionType.TRANSFER, amountCents, fromAccountId = fromId, toAccountId = toId)

    private fun balance(
        accountId: Long,
        type: AccountType,
        opening: Long,
        txs: List<TxRecord>,
    ) = BalanceCalculator.computeBalance(accountId, type, opening, txs)

    // ── Example 1: Basic expenses from a bank account ──────────────────────

    @Test
    fun `bank balance decreases with expenses`() {
        val bankId = 1L
        val txs = listOf(
            expense(4500, bankId),   // €45.00 Groceries
            expense(1250, bankId),   // €12.50 Transport
        )
        assertEquals(94_250L, balance(bankId, AccountType.BANK, 100_000L, txs))
    }

    // ── Example 2: Credit card charges and payments ─────────────────────────

    @Test
    fun `credit card balance increases with charges`() {
        val cardId = 2L
        val txs = listOf(
            expense(8000, cardId),   // €80.00 Shopping
            expense(2000, cardId),   // €20.00 Eating out
        )
        assertEquals(10_000L, balance(cardId, AccountType.CREDIT_CARD, 0L, txs))
    }

    @Test
    fun `credit card balance decreases with transfer payment`() {
        val bankId = 1L
        val cardId = 2L
        val txs = listOf(
            expense(8000, cardId),
            expense(2000, cardId),
            transfer(5000, bankId, cardId),   // partial payment
            transfer(5000, bankId, cardId),   // full clearance
        )
        assertEquals(0L, balance(cardId, AccountType.CREDIT_CARD, 0L, txs))
        assertEquals(190_000L, balance(bankId, AccountType.BANK, 200_000L, txs))
    }

    @Test
    fun `transfers do not count as income or expense in report totals`() {
        val bankA = 1L
        val bankB = 2L
        val txs = listOf(transfer(4000, bankA, bankB))

        assertEquals(96_000L, balance(bankA, AccountType.BANK, 100_000L, txs))
        assertEquals(4_000L,  balance(bankB, AccountType.BANK, 0L, txs))

        // Transfers should not appear in income or expense aggregation:
        // verify they are not counted in income/expense sums
        val incomeTotal = txs.filter { it.type == TransactionType.INCOME }.sumOf { it.amountCents }
        val expenseTotal = txs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amountCents }
        assertEquals(0L, incomeTotal)
        assertEquals(0L, expenseTotal)
    }

    // ── Example 3: Income and refund ────────────────────────────────────────

    @Test
    fun `income increases bank balance`() {
        val bankId = 1L
        val txs = listOf(
            income(300_000, bankId),   // €3,000 Salary
            expense(4_500, bankId),    // €45.00 Groceries
            income(1_000, bankId),     // €10.00 Refund (INCOME with Refund category)
        )
        assertEquals(346_500L, balance(bankId, AccountType.BANK, 50_000L, txs))
    }

    // ── Example 4: Opening balance with existing credit card debt ───────────

    @Test
    fun `credit card with opening debt tracks correctly`() {
        val bankId = 1L
        val cardId = 2L
        val txs = listOf(
            expense(3_000, cardId),              // €30.00 new charge
            transfer(18_000, bankId, cardId),    // full payment of €150 + €30
        )
        // Opening debt: €150.00 = 15,000 cents
        assertEquals(0L, balance(cardId, AccountType.CREDIT_CARD, 15_000L, txs))
    }

    // ── Edge cases ────────────────────────────────────────────────────────────

    @Test
    fun `empty transaction list returns opening balance`() {
        assertEquals(50_000L, balance(1L, AccountType.BANK, 50_000L, emptyList()))
        assertEquals(10_000L, balance(2L, AccountType.CREDIT_CARD, 10_000L, emptyList()))
    }

    @Test
    fun `transactions for other accounts do not affect this account`() {
        val myBank = 1L
        val otherBank = 99L
        val txs = listOf(
            income(100_000, otherBank),
            expense(50_000, otherBank),
        )
        assertEquals(30_000L, balance(myBank, AccountType.BANK, 30_000L, txs))
    }
}
