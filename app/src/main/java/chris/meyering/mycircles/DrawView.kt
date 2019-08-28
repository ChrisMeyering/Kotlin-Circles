package chris.meyering.mycircles

import android.content.Context
import android.database.Cursor
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.loader.app.LoaderManager
import chris.meyering.mycircles.objects.CircleHandler
import chris.meyering.mycircles.objects.Point

class DrawView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int=0):
    View(context, attrs, defStyle) {


    val ch = CircleHandler(context)

    private var offset: Point = Point(0f,0f)

    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            ch.scale(detector.scaleFactor)
            return true
        }
    }

    private val scaleDetector = ScaleGestureDetector(context, scaleListener)

    /*******************************************
     *               FAB ACTIONS
     *******************************************/

    fun undo(): Boolean {

        val b = ch.undo()
        invalidate()
        return b
    }

    fun redo(): Boolean {
        val b = ch.redo()
        invalidate()
        return b
    }

    fun clear(): Boolean {
        val b = ch.clear()
        invalidate()
        return b
    }

    fun move(p: Point) {
        ch.move(p)
        invalidate()
    }

    fun setHistoryLoaderManager(loaderManager: LoaderManager) {
        ch.setHistoryLoaderManager(loaderManager)
    }
    fun setHistoryCursor(cursor: Cursor) {
        ch.chm.historyEventCursor = cursor
    }

    /*******************************************
     *                 ACTIONS                 *
     *******************************************/

    private fun onFingerDown(p: Point) {
        if (ch.intersects(p)) {
            offset = ch.selectedCircleCenter() - p
            ch.setTempCircle(p + offset)
        } else {
            ch.setTempCircle(p)
        }
        invalidate()
    }

    private fun onFingerUp(p: Point) {
        ch.commitTempCircle(p)
        offset = Point()
        invalidate()
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
                move(p + offset)
            MotionEvent.ACTION_UP ->
                onFingerUp(p + offset)
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
            var paint = ch.circlePaint
            for (circle in ch.circles) {
                if (circle.isVisible) {
                    paint.color = circle.color
                    canvas.drawCircle(circle.p.x, circle.p.y, circle.r, paint)
                }
            }
            if (ch.tempCircle != null) {
                paint = ch.tempCirclePaint
                canvas.drawCircle(ch.tempCircle!!.p.x, ch.tempCircle!!.p.y, ch.tempCircle!!.r, paint)

            }

        }
    }


    /***********************************
     * Data persistence                *
     ***********************************/

//    override fun onRestoreInstanceState(state: Parcelable?) {
//        if (state is SavedState) {
//            super.onRestoreInstanceState(state.superState)
//            ch.circles = state.circles
//        } else {
//            super.onRestoreInstanceState(state)
//        }
//    }
//
//    override fun onSaveInstanceState(): Parcelable? {
//        val  savedState = SavedState(super.onSaveInstanceState())
//        savedState.circles = ch.circles
//        return savedState
//    }

    fun initCircles(cursor: Cursor) {
        ch.initCircles(cursor)
    }

    fun save() {
        ch.save()

    }

//    fun load() {
//        ch.loadCircles()
//    }
/*
    internal class SavedState : BaseSavedState {
        var circles: MutableList<Circle> = mutableListOf()

        constructor(superState: Parcelable?) : super(superState)

        constructor(source: Parcel) : super(source) {
            circles = source.readParcelableArray(Circle::class.java.classLoader) as MutableList<Circle>
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
    }*/
}