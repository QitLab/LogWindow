package com.qit.logwindow

import android.app.Activity
import android.content.Context

/**
 * author: Qit .
 * date:   On 2019-12-02
 */
class Util {
    companion object {
        const val SHARE_NAME = "share_name"

        fun setRlogEnable(context: Context, value: Boolean) {
            val sharedPreferences = context
                    .getSharedPreferences(SHARE_NAME, Activity.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("rlog", value)
            editor.apply()
        }

        fun isRlogEnable(context: Context): Boolean {
            val sharedPreferences = context
                    .getSharedPreferences(SHARE_NAME, Activity.MODE_PRIVATE)

            return sharedPreferences.getBoolean("rlog", false)
        }

    }
}