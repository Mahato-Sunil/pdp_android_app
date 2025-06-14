package com.officialsunil.pdpapplication.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object SplashScreenUtils {

    // function to setup splash screen
    fun initializeSplashScreen(
        activity: ComponentActivity, durationMillis: Long = 1500L, onComplete: () -> Unit
    ) {
        val splashScreen = activity.installSplashScreen()
        var keepSplashVisible = true
        splashScreen.setKeepOnScreenCondition { keepSplashVisible }

        splashScreen.setOnExitAnimationListener { screen ->
            val animX = ObjectAnimator.ofFloat(screen.iconView, View.SCALE_X, 1f, 0f)
            val animY = ObjectAnimator.ofFloat(screen.iconView, View.SCALE_Y, 1f, 0f)
            AnimatorSet().apply {
                playTogether(animX, animY)
                interpolator = OvershootInterpolator()
                duration = 500
                doOnEnd { screen.remove() }
                start()
            }
        }

        activity.lifecycleScope.launch {
            delay(durationMillis)
            keepSplashVisible = false
            onComplete()
        }
    }
}
