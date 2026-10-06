package app.harsha.wheresmypenny

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import app.harsha.wheresmypenny.ui.navigation.AppNavigation
import app.harsha.wheresmypenny.ui.theme.WheresMyPennyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WheresMyPennyTheme {
                AppNavigation()
            }
        }
    }
}
