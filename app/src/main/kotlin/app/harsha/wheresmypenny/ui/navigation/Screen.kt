package app.harsha.wheresmypenny.ui.navigation

sealed class Screen(val route: String) {
    data object Overview       : Screen("overview")
    data object Transactions   : Screen("transactions")
    data object AddTransaction : Screen("add_transaction")
    data object Reports        : Screen("reports")
    data object Settings       : Screen("settings")
}
