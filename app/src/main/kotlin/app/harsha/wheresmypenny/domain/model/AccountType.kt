package app.harsha.wheresmypenny.domain.model

enum class AccountType(val dbValue: String) {
    BANK("BANK"),
    CASH("CASH"),
    CREDIT_CARD("CREDIT_CARD");

    val isAsset: Boolean get() = this != CREDIT_CARD

    companion object {
        fun fromDbValue(value: String): AccountType =
            entries.first { it.dbValue == value }
    }
}
