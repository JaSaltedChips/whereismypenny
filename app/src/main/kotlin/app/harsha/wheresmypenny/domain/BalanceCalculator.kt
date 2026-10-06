package app.harsha.wheresmypenny.domain

import app.harsha.wheresmypenny.domain.model.AccountType
import app.harsha.wheresmypenny.domain.model.TransactionType

/**
 * Pure accounting logic — no Android dependencies.
 * All amounts are in integer minor currency units (e.g. euro cents).
 *
 * Asset account balance  = opening + income_received + transfers_in - expenses_out - transfers_out
 * Credit card balance    = opening + charges - payments  (positive = amount you owe)
 */
object BalanceCalculator {

    data class TxRecord(
        val type: TransactionType,
        val amountCents: Long,
        val fromAccountId: Long?,
        val toAccountId: Long?,
    )

    /**
     * Compute the balance for a single account given its opening balance and
     * the full list of transactions that touch any account in the ledger.
     * The caller must pass all transactions; this function filters by accountId.
     */
    fun computeBalance(
        accountId: Long,
        accountType: AccountType,
        openingBalanceCents: Long,
        transactions: List<TxRecord>,
    ): Long = when (accountType) {
        AccountType.CREDIT_CARD -> creditCardBalance(accountId, openingBalanceCents, transactions)
        else                    -> assetBalance(accountId, openingBalanceCents, transactions)
    }

    private fun assetBalance(id: Long, opening: Long, txs: List<TxRecord>): Long {
        var balance = opening
        for (tx in txs) {
            when (tx.type) {
                TransactionType.INCOME   -> if (tx.toAccountId == id)   balance += tx.amountCents
                TransactionType.EXPENSE  -> if (tx.fromAccountId == id) balance -= tx.amountCents
                TransactionType.TRANSFER -> {
                    if (tx.toAccountId == id)   balance += tx.amountCents
                    if (tx.fromAccountId == id) balance -= tx.amountCents
                }
            }
        }
        return balance
    }

    // Credit card: positive balance means you OWE that amount.
    // Charges (EXPENSE from card) increase the balance.
    // Payments (TRANSFER to card) reduce the balance.
    private fun creditCardBalance(id: Long, opening: Long, txs: List<TxRecord>): Long {
        var balance = opening
        for (tx in txs) {
            when (tx.type) {
                TransactionType.EXPENSE  -> if (tx.fromAccountId == id) balance += tx.amountCents
                TransactionType.TRANSFER -> if (tx.toAccountId == id)   balance -= tx.amountCents
                TransactionType.INCOME   -> Unit  // income cannot target a credit card
            }
        }
        return balance
    }
}
