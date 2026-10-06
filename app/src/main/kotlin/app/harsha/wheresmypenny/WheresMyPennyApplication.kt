package app.harsha.wheresmypenny

import android.app.Application
import app.harsha.wheresmypenny.data.db.AppDatabase

class WheresMyPennyApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
}
