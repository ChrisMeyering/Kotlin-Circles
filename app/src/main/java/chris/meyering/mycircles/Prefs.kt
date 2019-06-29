package chris.meyering.mycircles

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color

class Prefs (context: Context) {
    companion object {
        val PREF_FILENAME = "chris.meyering.mycircles.prefs"
        val BG_COLOR = "pref_background_color"
    }
    val prefs: SharedPreferences = context.getSharedPreferences(PREF_FILENAME,0)

    var bgColor: Int
        get() = prefs.getInt(BG_COLOR, Color.WHITE)
        set(value) = prefs.edit().putInt(BG_COLOR, value).apply()
}