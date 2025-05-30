package com.dicoding.intermediate_satu.ui

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View


fun playTogether(vararg targets: View) {
    val animatorSet = AnimatorSet()
    val animations = mutableListOf<Animator>()
    for (target in targets){
        val animator = ObjectAnimator.ofFloat(target, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }
        animations.add(animator)
    }
    animatorSet.apply {
        playTogether(animations)
        start()
    }
}
fun playAnimations(vararg views: View){
    val animatorSet = AnimatorSet()
    val animations = mutableListOf<Animator>()
    for (view in views) {
        val animator = ObjectAnimator.ofFloat(view, View.ALPHA, 1f).setDuration(500)
        animations.add(animator)
    }
    animatorSet.apply{
        playSequentially(animations)
        start()
    }
}

fun playTogetherAnimations(vararg views: View){

}