package com.dev.pushkar.consochrome

import android.content.SharedPreferences
import android.graphics.Typeface
import android.widget.ImageView

import java.util.Calendar

/**
 * Class contains all the global data used for the application
 * @author Maharshi
 */
object Data {
    val LEVELS_PER_BUFFER = 2
    val MAX_BUFFER = 3
    val STEPS_PER_LEVEL = 30
    val TOTOL_LEVELS = LEVELS_PER_BUFFER * MAX_BUFFER
    val TIMEOUT_INCREMENT = 500
    val TIMEOUT_MIN = 3000
    val TIMEOUT_MAX = 10000

    val LEADERBOARD_LENGTH = 10

    val COLOR_RED = "RED"
    val COLOR_YELLOW = "YELLOW"
    val COLOR_BLUE = "BLUE"
    val COLOR_GREEN = "GREEN"
    val COLOR_BROWN = "BROWN"
    val COLOR_MAGENTA = "MAGENTA"
    val COLOR_INDIGO = "INDIGO"
    val COLOR_GRAY = "GRAY"

    val COLORS = arrayOf(COLOR_RED, COLOR_BLUE, COLOR_GREEN, COLOR_YELLOW, COLOR_MAGENTA, COLOR_INDIGO, COLOR_BROWN, COLOR_GRAY)

    val N_COLORS = 8
    var consoLogos = arrayOfNulls<ImageView>(N_COLORS)
    var colorImages = IntArray(N_COLORS)//to be initialized

    var mainActivity: MainActivity? = null
    var tapTileActivity: TapTilesActivity? = null
    var tapTileOverActivity: TapTileGameOverActivity? = null

    var calender: Calendar? = null
    var localData: SharedPreferences? = null
    val applicationPreference = "appPref"

    val KEY_NEW_INSTALL = "new_install"
    val KEY_TT_BEST_SCORE = "bs1"
    val KEY_TT_DAILY_BEST_SCORE = "dbs1"
    val KEY_TODAY = "day"
    val KEY_CONSO_TILE_BEST_SCORE = "bs2"
    val KEY_USERNAME = "username"
    val KEY_UPDATES = "updates"
    val KEY_UPDATES_READ = "update_read"
    val KEY_LEADER_D = "daily_l"
    val KEY_LEADER_A = "all_time_l"

    var KEY_DAILY_SIZE = "daily_size"
    var KEY_ALL_TIME_SIZE = "all_time_size"
    var KEY_USERS_ALL_TIME = arrayOfNulls<String>(LEADERBOARD_LENGTH)
    var KEY_USERS_DAILY = arrayOfNulls<String>(LEADERBOARD_LENGTH)
    var KEY_SCORES_ALL_TIME = arrayOfNulls<String>(LEADERBOARD_LENGTH)
    var KEY_SCORES_DAILY = arrayOfNulls<String>(LEADERBOARD_LENGTH)

    val DEFAULT_USERNAME = "guest"

    val UPDATE_READ = 1
    val UPDATE_UNREAD = 0

    val MAC_ID_NOT_FOUND = 0
    val SCORE_UPDATED_SUCCESSFUL = 1
    val UNEXPECTED_ERROR = 2

    val USERNAME_ALREADY_EXISTS = 0
    val DEVICE_REGISTRATION_SUCCESSFUL = 1

    val url_submit_score = "http://consortium123.site50.net/consotiles/verify_user_v2.php"
    val url_device_register = "http://consortium123.site50.net/consotiles/submit_user_v2.php"
    val url_leaderboard_daily = "http://consortium123.site50.net/consotiles/leaderboard_daily_v2.php"
    val url_leaderboard_all_time = "http://consortium123.site50.net/consotiles/leaderboard_all_time_v2.php"
    val url_updates = "http://consortium123.site50.net/consotiles/view_updates_v2.php"

    var appTypeface: Typeface? = null

    init {

        for (i in 0 until LEADERBOARD_LENGTH) {
            KEY_USERS_ALL_TIME[i] = "all_time_users_" + (i + 1)
            KEY_SCORES_ALL_TIME[i] = "all_time_scores_" + (i + 1)
            KEY_USERS_DAILY[i] = "daily_users_" + (i + 1)
            KEY_SCORES_DAILY[i] = "daily_scores_" + (i + 1)
        }
    }
}
