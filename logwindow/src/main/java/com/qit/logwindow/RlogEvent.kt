package com.qit.logwindow

import android.content.Context

/**
 * author: Qit .
 * date:   On 2019-12-02
 */


interface RlogEvent {
    //输出事件触发调用
    fun event(group: String, event: String, vararg params: Any)
}

class RlogHandler private constructor() : RlogEvent {

    override fun event(group: String, event: String, vararg params: Any) {}

    companion object {

        @Volatile
        private var mInstatnce: RlogHandler? = null
        private var mRlogController: RlogController? = null

        fun getInstance(context: Context): RlogController {
            if (mInstatnce == null) {
                synchronized(RlogHandler::class) {
                    if (mInstatnce == null) {
                        mInstatnce = RlogHandler()
                        mRlogController = RlogController.getInstance(context, mInstatnce!!)
                    }
                }
            }
            return mRlogController as RlogController
        }
    }
}