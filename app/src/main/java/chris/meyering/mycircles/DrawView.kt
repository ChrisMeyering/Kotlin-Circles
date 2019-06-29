package chris.meyering.mycircles

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import chris.meyering.mycircles.objects.Circle
import chris.meyering.mycircles.objects.Point
import java.util.*
import kotlin.math.max
import kotlin.math.min

class DrawView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int=0):
    View(context, attrs, defStyle) {

    companion object {
        private const val MIN_RADIUS = 30.0f
        private const val MAX_RADIUS = 300.0f
    }

    private val rand : Random = Random()
    private var circleColor : Int = getRandColor()
    var circles : MutableList<Circle> = mutableListOf()
    private var tempCircle : Circle? = null
    private val circlePaint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var radius: Float = MIN_RADIUS


    private var selectedCircleIdx: Int = -1
    private var offset: Point = Point(0f,0f)

    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            radius *= detector.scaleFactor
            radius = max(MIN_RADIUS, min(radius, MAX_RADIUS))
            tempCircle?.r = radius
            return true
        }
    }

    private val scaleDetector = ScaleGestureDetector(context, scaleListener)

    init {
        circlePaint.style = Paint.Style.FILL
        circlePaint.isAntiAlias = true

    }

    private fun getRandColor(): Int {
        return Color.rgb(getRand255(), getRand255(), getRand255())
    }

    private fun getRand255(): Int {
        return rand.nextInt(256)
    }

    private fun addCircle(circle: Circle) {
        circles.add(circle)
        tempCircle = null
        invalidate()
    }

    private fun addTempCircle(circle: Circle) {
        tempCircle = circle
        invalidate()
    }

    //
    private fun moveCircle(p: Point) {
        tempCircle?.moveTo(p + offset)
        invalidate()
    }
//    fun clearCircles() {
//        circles.clear()
//        invalidate()
//    }
//
//    private fun halfAlpha(@ColorInt c: Int) : Int {
//        return ColorUtils.setAlphaComponent(c, 130)
//    }

    private fun intersects(p: Point): Boolean {
        for (i in circles.count() -1 downTo  0) {
            if (circles[i].intersects(p)) {
                selectedCircleIdx = i
                return true
            }
        }
        return false
    }

    private fun onFingerDown(p: Point) {
        if (intersects(p)) {
            val selCirc = circles[selectedCircleIdx]
            offset = selCirc.p!! - p
            circles[selectedCircleIdx].isVisible = false
            addTempCircle(Circle(p + offset, selCirc.r, selCirc.color))
        } else {
            addTempCircle(Circle(p, radius, circleColor))
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        performClick()
        scaleDetector.onTouchEvent(event)
        val p = Point(event.x, event.y)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN ->
                onFingerDown(p)
            MotionEvent.ACTION_MOVE ->
                moveCircle(p)
            MotionEvent.ACTION_UP -> {
                if (selectedCircleIdx == -1) {
                    addCircle(Circle(p, radius, circleColor))
                    circleColor = getRandColor()
                } else {
                    circles[selectedCircleIdx] = tempCircle!!
                    circles[selectedCircleIdx].moveTo(p + offset)
                    circles[selectedCircleIdx].isVisible = true
                    selectedCircleIdx = -1
                    offset = Point()
                    tempCircle = null
                    invalidate()
                }
            }

        }
        return true

    }


    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            for (circle in circles) {
                if (circle.isVisible) {
                    circlePaint.color = circle.color
                    canvas.drawCircle(circle.p!!.x, circle.p!!.y, circle.r, circlePaint)
                }
            }
            if (tempCircle != null) {
                circlePaint.color = tempCircle!!.color
                circlePaint.alpha = 130
                canvas.drawCircle(tempCircle!!.p!!.x, tempCircle!!.p!!.y, tempCircle!!.r, circlePaint)

            }
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            circles = state.circles
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val  savedState: SavedState = SavedState(super.onSaveInstanceState())
        savedState.circles = circles
        return savedState
    }




    internal class SavedState : BaseSavedState {
        var circles: MutableList<Circle> = mutableListOf()

        constructor(superState: Parcelable?) : super(superState)

        constructor(source: Parcel) : super(source) {
            val obj = source.readParcelableArray(Circle::class.java.classLoader)
        }


        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                out.writeParcelableList(circles, flags)
            }

        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(source: Parcel): SavedState {
                return SavedState(source)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }

        }
    }
}