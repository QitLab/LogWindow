package com.qit.logwindow

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi

/**
 * author: Qit .
 * date:   On 2019-12-02
 */

class LogView : RelativeLayout {
    constructor(context: Context) : super(context) {
        init(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr, 0)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs, defStyleAttr, defStyleRes)
    }

    var desc = "开"
    var tvDesc: TextView? = null
    fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val parent = LayoutInflater.from(context).inflate(R.layout.view_log_window_item, this, true)
        tvDesc = parent.findViewById(R.id.tv_desc)
        desc = getRlogEnable()
        tvDesc?.text = desc
        if (desc == "开") {
            RlogHandler.getInstance(context).close()
        }
        parent.findViewById<View>(R.id.content).setOnClickListener {
            if (RlogHandler.getInstance(context).rlogDialogPermission(context as Activity)) {
                switchRlog()
            }
        }
    }


    private fun switchRlog() {
        Util.setRlogEnable(context, !Util.isRlogEnable(context))
        desc = getRlogEnable()
        tvDesc?.text = desc
        if (desc == "开") {
            RlogHandler.getInstance(context).close()
        }
    }

    private fun getRlogEnable(): String {
        return if (Util.isRlogEnable(context)) "关" else "开"
    }

}
