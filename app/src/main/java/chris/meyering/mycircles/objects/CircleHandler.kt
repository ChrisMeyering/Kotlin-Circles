package chris.meyering.mycircles.objects

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import androidx.loader.app.LoaderManager
import chris.meyering.mycircles.data.provider.CirclesContract

class CircleHandler(val context: Context) {
    private companion object {
        const val TEMP_CIRCLE_ALPHA: Int = 130
    }

    val clearable: Boolean
        get() = circles.size > 0
    var cg = CircleGenerator()
    val chm = CanvasHistoryManager(context)
    var circles : MutableList<Circle> = mutableListOf()
    var tempCircle : Circle? = null
    var selCircleIdx : Int = -1

    val tempCirclePaint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val circlePaint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)


    init {
        tempCirclePaint.style = Paint.Style.FILL
        circlePaint.style = Paint.Style.FILL
    }

    fun intersects(p: Point): Boolean {
        for (i in circles.count() -1 downTo  0) {
            if (circles[i].intersects(p)) {
                selCircleIdx = i
                return true
            }
        }
        return false
    }

    fun move(p: Point) {
        tempCircle?.moveTo(p)
//            circles[selCircleIdx].moveTo(p)
    }



    fun addCircle(c: Circle) {
        circles.add(c)
        Log.i("MyLog.CircleHandler", "there are now ${circles.size} items")
    }

    fun commitTempCircle(p: Point) {
        tempCircle!!.moveTo(p)
        if (selCircleIdx == -1) {
            chm.newEvent(CanvasHistoryManager.CreateEvent(tempCircle!!))
            circles.add(tempCircle!!)
            cg.nextColor()
        } else {
            circles[selCircleIdx].isVisible = true
            chm.newEvent(CanvasHistoryManager.FingerEvent(selCircleIdx, circles[selCircleIdx].copy(), tempCircle!!))
            circles[selCircleIdx] = tempCircle!!.copy()
            selCircleIdx = -1
        }
        tempCircle = null
    }

    fun setTempCircle(p: Point) {
        if (selCircleIdx == -1) {
            tempCircle = cg.getCircle(p)
        } else {
            tempCircle = circles[selCircleIdx].copy()
            circles[selCircleIdx].isVisible = false
            tempCircle!!.moveTo(p)
        }
        tempCirclePaint.color = Color.argb(
            TEMP_CIRCLE_ALPHA,
            Color.red(tempCircle!!.color),
            Color.green(tempCircle!!.color),
            Color.blue(tempCircle!!.color))
    }

    fun undo(): Boolean {
        if (chm.undoable) {
            when (val event = chm.undo()) {
                is CanvasHistoryManager.ClearEvent -> {
                    undoClear(event.deletedCircles)
                }
                is CanvasHistoryManager.CreateEvent -> {
                    circles = circles.dropLast(1).toMutableList()
                }
                is CanvasHistoryManager.FingerEvent -> {
                    replaceCircle(event.circleIdx, event.startCircle)
                }
            }
        }
        return chm.undoable
    }


    fun redo(): Boolean {
        if (chm.redoable) {
            when (val event = chm.redo()) {
                is CanvasHistoryManager.ClearEvent -> {
                    clear()
                }
                is CanvasHistoryManager.CreateEvent -> {
                    circles.add(event.circle)
                }
                is CanvasHistoryManager.FingerEvent -> {
                    replaceCircle(event.circleIdx, event.endCircle)
                }
            }
        }
        return chm.redoable
    }

    fun clear(): Boolean {
        if (circles.size > 0) {
            chm.newEvent(CanvasHistoryManager.ClearEvent(circles.toMutableList()))
            circles.clear()
        }
        return chm.redoable
    }

    fun selectedCircleCenter() : Point {
        if (selCircleIdx == -1) {
            return Point()
        }
        return circles[selCircleIdx].p
    }

    fun scale(f: Float) {
        cg.radius *= f
        tempCircle?.r = cg.radius
    }

    fun undoClear(deletedCircles: MutableList<Circle>) {
        circles = deletedCircles

    }

    fun replaceCircle(i: Int, c: Circle) {
        circles[i] = c
    }


    fun save() {
        saveCircles()
        chm.saveHistory()
    }

    fun saveCircles() {
        val contentResolver: ContentResolver = context.contentResolver
        contentResolver.delete(CirclesContract.VisibleCirclesEntry.CONTENT_URI, null, null)
        for (circle in circles) {
            contentResolver.insert(CirclesContract.VisibleCirclesEntry.CONTENT_URI, circle.toContentValues())
        }
        Log.i("MyLog.CircleHandler", "${circles.size} circles saved")
    }

    fun initCircles(cursor: Cursor) {
        var count = 0
        circles.clear()
        while (cursor.moveToNext()) {
            addCircle(
                Circle(
                    Point(
                        cursor.getFloat(cursor.getColumnIndex(CirclesContract.VisibleCirclesEntry.COLUMN_X)),
                        cursor.getFloat(cursor.getColumnIndex(CirclesContract.VisibleCirclesEntry.COLUMN_Y))
                    ),
                    cursor.getFloat(cursor.getColumnIndex(CirclesContract.VisibleCirclesEntry.COLUMN_R)),
                    cursor.getInt(cursor.getColumnIndex(CirclesContract.VisibleCirclesEntry.COLUMN_COLOR))
                )
            )
            count++
        }
        Log.i("MyLog.CircleHandler", "$count circles were loaded")
    }

    fun setHistoryLoaderManager(loaderManager: LoaderManager) {
        chm.loaderManager = loaderManager
    }
}