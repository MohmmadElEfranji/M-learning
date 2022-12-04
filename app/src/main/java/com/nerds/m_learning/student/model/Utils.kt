package com.nerds.m_learning.student.model

import android.app.Activity
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.nerds.m_learning.R


object Utils {

    // this will toggle or action bar color
    fun toggleStatusBarColor(activity: Activity, color: Int) {
        val window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(activity, R.color.purple_200)
    }
}