package com.qit.logwindow

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

/**
 * author: Qit .
 * date:   On 2019-12-03
 */
class RlogController private constructor(private val mContext: Context, private val mEvent: RlogEvent) : RlogEvent {
    private var mWindow: RlogProxyHandlerWindow? = null
    private val mToastDeque: MutableList<String>?

    init {
        if (mWindow == null) {
            mWindow = RlogProxyHandlerWindow(mContext)
            mWindow!!.setOnDismissListener { pickContentFormQueue() }
        }
        mToastDeque = ArrayList()
    }


    fun close() {
        mToastDeque!!.clear()
        mWindow?.cancel()
    }

    override fun event(group: String, event: String, vararg params: Any) {

        mEvent.event(group, event, *params)
        if (Util.isRlogEnable(mContext)) {
            //开启打点视图
            val s = combinationContent(group, event, *params)
            mToastDeque!!.add(s)
            if (mToastDeque.size > 20) {
                mToastDeque.removeAt(0)
            }
            pickContentFormQueue()
        }
    }

    /**
     * 需要先检车权限
     */
    fun rlogDialogPermission(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(mContext)) {
                //没有权限
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.packageName))
                val pm = activity.packageManager
                val activities = pm.queryIntentActivities(intent, 0)
                if (activities.size <= 0) {
                    //不存在匹配跳转隐式intent的Activity
                } else {
                    //存在匹配跳转隐式intent的Activity
                    activity.startActivityForResult(intent, 1)
                }
                return false
            }
        }
        return true
    }

    private fun combinationContent(group: String, event: String, vararg params: Any): String {
        val builder = StringBuilder()
        builder.append(String.format("%s", group))
                .append("_")
                .append(String.format("%s", event))
                .append(" ")
        val `object`: JSONObject
        if (params.size >= 2) {
            try {
                `object` = JSONObject()
                buildArrayMessage(`object`, params)
                builder.append(String.format("%s", `object`.toString()))
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
        return builder.toString()
    }


    private fun pickContentFormQueue() {
        if (this.mWindow != null) {
            if (mToastDeque != null && mToastDeque.size != 0) {
                mWindow!!.show(mToastDeque)
            } else {
                mWindow!!.removeLogView()
            }
        }
    }

    @Throws(JSONException::class)
    private fun buildArrayMessage(paramsBuilder: JSONObject, params: Array<out Any>) {
        /**
         * 参数额格式校验
         */
        if (params.isNotEmpty()) {
            var i = 0
            do {
                val item = params[i++]

                if (item is Map<*, *>) {
                    for (key in item.keys) {
                        paramsBuilder.put(key.toString(), item[key])
                    }
                } else if (item is String) {
                    if (i < params.size) {
                        val value = params[i++]

                        val clazz = value.javaClass
                        require(!(clazz != Int::class.javaPrimitiveType && clazz != Int::class.java && clazz != Long::class.javaPrimitiveType &&
                                clazz != Long::class.java && clazz != Byte::class.javaPrimitiveType && clazz != Byte::class.java &&
                                clazz != Boolean::class.javaPrimitiveType && clazz != Boolean::class.java &&
                                clazz != Char::class.javaPrimitiveType && clazz != Char::class.java &&
                                clazz != Float::class.javaPrimitiveType && clazz != Float::class.java &&
                                clazz != Double::class.javaPrimitiveType && clazz != Double::class.java &&
                                clazz != Byte::class.javaPrimitiveType && clazz != Byte::class.java &&
                                clazz != String::class.java && clazz != CharSequence::class.java)) { "value's type is not correct" + "it must be string or original type like int or long" }

                        paramsBuilder.put(item, value)
                    } else {
                        throw IllegalArgumentException("key and value must be pair , " + "can't just add a key not follow a value")
                    }
                } else if (item is Array<*>) {
                    buildArrayMessage(paramsBuilder, item as Array<out Any>)
                } else {
                    throw IllegalArgumentException("log params must be like this :" +
                            "key , value,..., Map<String,Object>; and key must be string ," +
                            "map don't need a key。")
                }
            } while (i < params.size)
        }
    }

    companion object {

        private var mInstance: RlogController? = null


        fun getInstance(context: Context, event: RlogEvent): RlogController? {
            if (mInstance == null) {
                synchronized(RlogController::class.java) {
                    if (mInstance == null) {
                        mInstance = RlogController(context, event)
                    }
                }
            }
            return mInstance
        }
    }
}