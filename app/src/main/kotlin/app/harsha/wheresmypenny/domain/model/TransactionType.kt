package app.harsha.wheresmypenny.domain.model

enum class TransactionType(val dbValue: String) {
    EXPENSE("EXPENSE"),
    INCOME("INCOME"),
    TRANSFER("TRANSFER");

    companion object {
        fun fromDbValue(value: String): TransactionType =
            entries.first { it.dbValue == value }
    }
}
