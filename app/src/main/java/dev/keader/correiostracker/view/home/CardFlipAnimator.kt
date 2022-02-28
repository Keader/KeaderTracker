package dev.keader.correiostracker.view.home

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.view.View
import dev.keader.correiostracker.R

object CardFlipAnimator {
    private const val CAMERA_DISTANCE = 8000

    fun animate(context: Context, front: View, back: View) {
        val scale = context.resources.displayMetrics.density
        front.cameraDistance = CAMERA_DISTANCE * scale
        back.cameraDistance = CAMERA_DISTANCE * scale

        val frontAnim = AnimatorInflater.loadAnimator(context, R.animator.front_card_animator) as AnimatorSet
        val backAnim = AnimatorInflater.loadAnimator(context, R.animator.back_card_animator) as AnimatorSet

        frontAnim.setTarget(front)
        backAnim.setTarget(back)

        frontAnim.start()
        backAnim.start()
    }
}
