package no.danielzeller.metaballslib.spinner

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.PathInterpolator


class BlobDrawable(val metaBall: Drawable, val tinColors: IntArray) : Drawable() {

    private val motion: ArrayList<BrownianMotion> = ArrayList()
    private var ballSize = 0
    private var sizeAnim: ValueAnimator? = null

    fun startAnimations() {
        ballSize = (bounds.width() * 0.25f).toInt()
        animateBallSize(0, ballSize, 300, null)
        val boundsSizeX = bounds.width().toFloat()
        val boundsSizeY = bounds.height().toFloat()
        motion.clear()
        for (i in 0 until 5) {
            motion.add(BrownianMotion(Vector3(boundsSizeX*0.8f, boundsSizeY*0.8f, 1000f)))
        }
    }

    fun stopAllAnimations() {
        sizeAnim?.cancel()
    }

    fun stopAndHide(spinner: View) {
        animateBallSize(ballSize, 0, 700, spinner)
    }

    fun animateBallSize(from: Int, to: Int, duration: Long, spinner: View?) {
        sizeAnim?.cancel()
        sizeAnim = ValueAnimator.ofInt(from, to).setDuration(duration)
        sizeAnim?.interpolator = PathInterpolator(.88f, 0f, .15f, 1f)
        sizeAnim?.addUpdateListener { animation ->
            ballSize = animation.animatedValue as Int
            metaBall.setBounds(-ballSize, -ballSize, ballSize, ballSize)
        }
        if (spinner != null)
            sizeAnim?.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationRepeat(animation: Animator?) {
                    super.onAnimationRepeat(animation)
                    spinner.visibility = View.GONE
                    stopAllAnimations()
                }
            })
        sizeAnim?.start()
    }


    override fun draw(canvas: Canvas) {
        canvas.translate((bounds.width() / 2).toFloat(), (bounds.height() / 2).toFloat())
        for (i in 0 until 5) {
            val count = canvas.save()
            val brownianMotion = motion[i]
            brownianMotion.update()
            canvas.translate(brownianMotion.position.x, brownianMotion.position.y)
            metaBall.setTint(tinColors[i])
            metaBall.draw(canvas)
            canvas.restoreToCount(count)
        }
        invalidateSelf()
    }


    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        startAnimations()
    }

    override fun setAlpha(alpha: Int) {

    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }
}