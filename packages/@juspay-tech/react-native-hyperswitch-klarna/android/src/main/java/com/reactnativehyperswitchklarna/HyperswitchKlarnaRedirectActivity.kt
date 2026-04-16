package com.reactnativehyperswitchklarna

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager

class HyperswitchKlarnaRedirectActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(0, 0)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        super.onCreate(savedInstanceState)
        forwardToHost(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        forwardToHost(intent)
    }

    private fun forwardToHost(deepLinkIntent: Intent?) {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(
                Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_NEW_TASK
            )
            deepLinkIntent?.data?.let { launchIntent.data = it }
            startActivity(launchIntent)
        }
        finish()
        overridePendingTransition(0, 0)
    }
}
