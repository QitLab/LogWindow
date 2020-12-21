package com.qit.logwindow;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangdiansheng on 2018/8/14.
 */

public class RlogProxyHandlerWindow {

    /**
     * Listener that is called when this popup window is dismissed.
     */
    public interface OnDismissListener {
        /**
         * Called when this popup window is dismissed.
         */
        public void onDismiss();
    }

    private View mView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private ListView mList;
    private List<String> mData = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private OnDismissListener mOnDismissListener;
    private boolean mIsShow;
    private Context mContext;

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }


    public void removeLogView() {
        if (mIsShow) {
            if (mWindowManager != null) {
                mWindowManager.removeView(mView);
            }
            mIsShow = false;
        }
    }


    public RlogProxyHandlerWindow(Context context) {
        mParams = new WindowManager.LayoutParams();
        mContext = context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        if (Build.VERSION.SDK_INT >= 26) {
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        }

        //设置效果为背景透明.
        mParams.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

        //设置窗口初始停靠位置.
        mParams.gravity = Gravity.CENTER;
        mParams.x = 0;
        mParams.y = 0;

        //获取浮动窗口视图所在布局.
        LayoutInflater inflater = LayoutInflater.from(context);
        mView = inflater.inflate(R.layout.lib_log_window_view_toast_content, null);
        mList = mView.findViewById(R.id.list);
        adapter = new ArrayAdapter<>(mContext, R.layout.list_item, android.R.id.text1, mData);//新建并配置ArrayAapeter
        mList.setAdapter(adapter);
    }


    public void show(List<String> s) {
        if (!mIsShow) {
            mIsShow = true;
            mWindowManager.addView(mView, mParams);
        }
        mData.clear();
        mData.addAll(s);
        adapter.notifyDataSetChanged();
    }

    public void cancel() {
        removeLogView();
    }
}
