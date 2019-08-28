package chris.meyering.mycircles.objects

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import chris.meyering.mycircles.data.provider.CirclesContract
import java.lang.Exception

/**

 */


class CanvasHistoryManager(val context: Context): LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        private const val TAG = "CanvasHistoryManager"
        private const val NAME_CREATE_EVENT = "CREATE_EVENT"
        private const val NAME_FINGER_EVENT = "FINGER_EVENT"
        private const val NAME_CLEAR_EVENT = "CLEAR_EVENT"
        private const val LOADER_CIRCLE_HISTORY_ID = 21

        fun getCircleFromCursor(cursor: Cursor) : Circle =
            Circle(
                Point(
                    cursor.getFloat(cursor.getColumnIndex(CirclesContract.CircleHistoryEntry.COLUMN_X)),
                    cursor.getFloat(cursor.getColumnIndex(CirclesContract.CircleHistoryEntry.COLUMN_Y))
                ),
                cursor.getFloat(cursor.getColumnIndex(CirclesContract.CircleHistoryEntry.COLUMN_R)),
                cursor.getInt(cursor.getColumnIndex(CirclesContract.CircleHistoryEntry.COLUMN_COLOR))

            )

    }
    val undoable: Boolean
        get() = undoList.size > 0
    val redoable: Boolean
        get() = redoList.size > 0
    var redoList: MutableList<Event> = mutableListOf()
    var undoList: MutableList<Event> = mutableListOf()
    var loaderManager: LoaderManager? = null

    var historyEventCursor: Cursor? = null
        set(value) {
            field = value
            getNextEvent()
        }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        if (id == LOADER_CIRCLE_HISTORY_ID) {
            Log.i("CanvasHistoryManager", "Recovering event " +
                    "${historyEventCursor!!.getInt(historyEventCursor!!.getColumnIndex(CirclesContract.EventHistoryEntry.COLUMN_EVENT_ID))}" +
                    " of type ${historyEventCursor!!.getString(historyEventCursor!!.getColumnIndex(CirclesContract.EventHistoryEntry.COLUMN_EVENT_TYPE))}"
            )
            return CursorLoader(
                context,
                CirclesContract.CircleHistoryEntry.getUriWithId(
                    historyEventCursor!!.getInt(historyEventCursor!!.getColumnIndex(CirclesContract.EventHistoryEntry.COLUMN_EVENT_ID))
                ),
                null, null, null, null
            )
        } else {
            throw Exception("Unknown Loader ID $id in CanvasHistoryManager.onCreateLoader")
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        if (!data.isClosed && loader.id == LOADER_CIRCLE_HISTORY_ID) {
            data.use {
                Log.i("CanvasHistoryManager", "onLoadFinished. Cursor size = ${data.count}")
                when (historyEventCursor!!.getString(historyEventCursor!!.getColumnIndex(CirclesContract.EventHistoryEntry.COLUMN_EVENT_TYPE))) {
                    NAME_CREATE_EVENT -> {
                        Log.i("CanvasHistoryManager", "|recovering Create event...")
                        if (data.moveToNext()) {
                            undoList.add(CreateEvent(getCircleFromCursor(data)))
                        } else {
                            throw Exception("Failed to recover CREATE event in CanvasHistoryManager.onLoadFinished")
                        }
                        Log.i("CanvasHistoryManager", "\t|-->|recovered ${(undoList.last() as CreateEvent).circle.print()}")
                    }
                    NAME_FINGER_EVENT -> {
                        Log.i("CanvasHistoryManager", "\t|recovering Finger event...")
                        if (data.moveToNext()) {
                            val circleIdx: Int =
                                data.getInt(data.getColumnIndex(CirclesContract.CircleHistoryEntry.COLUMN_CIRCLE_INDEX))
                            val startCircle: Circle = getCircleFromCursor(data)
                            Log.i("CanvasHistoryManager", "\t|-->|recovered first circle: ${startCircle.print()}")
                            if (data.moveToNext()) {
                                val endCircle = getCircleFromCursor(data)
                                Log.i("CanvasHistoryManager", "\t|-->|recovered first circle: ${endCircle.print()}")
                                undoList.add(FingerEvent(circleIdx, startCircle, endCircle))
                            } else {
                                throw Exception("Unable to restore Finger Event")
                            }
                        } else {
                            throw Exception("Unable to restore Finger Event")
                        }
                    }
                    NAME_CLEAR_EVENT -> {
                        Log.i("CanvasHistoryManager", "\t|recovering Clear event...")
                        val deletedCircles: MutableList<Circle> = mutableListOf()
                        while (data.moveToNext()) {
                            Log.i("CanvasHistoryManager", "\t|-->|recovered first circle: ${getCircleFromCursor(data).print()}")
                            deletedCircles.add(getCircleFromCursor(data))
                        }
                        undoList.add(ClearEvent(deletedCircles))
                        Log.i("CanvasHistoryManager", "\t|recovered ${deletedCircles.size} circles")
                    }
                    else -> throw Exception("On Load Finished: Unknown event")

                }
            }
        }
        getNextEvent()
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {

    }



    fun initCircleHistoryLoader() {
        val cursorLoader: Loader<Cursor>? = loaderManager!!.getLoader<Cursor>(LOADER_CIRCLE_HISTORY_ID)
        if (cursorLoader == null) {
            loaderManager!!.initLoader(LOADER_CIRCLE_HISTORY_ID, null, this)
        } else {
            loaderManager!!.restartLoader(LOADER_CIRCLE_HISTORY_ID, null, this)
        }
    }
    private fun getNextEvent() {
        if (historyEventCursor!!.moveToNext()) {
            initCircleHistoryLoader()
        } else {
            historyEventCursor!!.close()
        }
    }

    fun undo(): Event {
        val undoneEvent = undoList.last()
        redoList.add(undoneEvent)
        undoList = undoList.dropLast(1).toMutableList()
        return undoneEvent
    }

    fun redo(): Event {
        val redoneEvent = redoList.last()
        undoList.add(redoneEvent)
        redoList = redoList.dropLast(1).toMutableList()
        return redoneEvent
    }

    fun newEvent(e: Event) {
        undoList.add(e)
        redoList.clear()
        e.printToLog()
    }


    fun clearHistory() {
        val contentResolver: ContentResolver = context.contentResolver
        contentResolver.delete(CirclesContract.EventHistoryEntry.CONTENT_URI, null, null)
        contentResolver.delete(CirclesContract.CircleHistoryEntry.CONTENT_URI, null, null)
    }
    fun saveHistory() {
        Log.i("CanvasHistoryManger", "Saving history")
        clearHistory()
        undoList.forEachIndexed { index, event -> event.save(context, index) }
    }





















    interface Event {
        val NAME: String
        fun printToLog() {
            Log.i(TAG, "New event: $NAME")
        }
        fun getEventHistoryContentValues(index: Int) : ContentValues {
            val cv = ContentValues()
            cv.put(CirclesContract.EventHistoryEntry.COLUMN_EVENT_ID, index)
            cv.put(CirclesContract.EventHistoryEntry.COLUMN_EVENT_TYPE, NAME)
            return cv
        }
        fun getCircleHistoryItemContentValues(circle: Circle, eventId: Int, circleIdx: Int? = null) : ContentValues {
            val cv = circle.toContentValues()
            cv.put(CirclesContract.CircleHistoryEntry.COLUMN_EVENT_ID, eventId)
            return cv
        }

        fun save(context: Context, eventId: Int) {
            val contentResolver = context.contentResolver
            contentResolver.insert(CirclesContract.EventHistoryEntry.CONTENT_URI, getEventHistoryContentValues(eventId))
        }
    }


    class ClearEvent(val deletedCircles: MutableList<Circle>) : Event {
        override val NAME = NAME_CLEAR_EVENT


        override fun save(context: Context, eventId: Int) {
            super.save(context, eventId)
            val contentResolver = context.contentResolver
            for (circle in deletedCircles) {
                contentResolver.insert(CirclesContract.CircleHistoryEntry.CONTENT_URI, getCircleHistoryItemContentValues(circle, eventId))
            }
        }


    }

    class CreateEvent(val circle: Circle) : Event {
        override val NAME = NAME_CREATE_EVENT

        override fun save(context: Context, eventId: Int) {
            super.save(context, eventId)
            val contentResolver = context.contentResolver
            contentResolver.insert(CirclesContract.CircleHistoryEntry.CONTENT_URI, getCircleHistoryItemContentValues(circle, eventId))
        }

    }

    class FingerEvent(val circleIdx: Int, val startCircle: Circle, val endCircle: Circle) : Event {
        override val NAME = NAME_FINGER_EVENT

        override fun getCircleHistoryItemContentValues(circle: Circle, eventId: Int, circleIdx: Int?): ContentValues {
            val cv = super.getCircleHistoryItemContentValues(circle, eventId, circleIdx)
            cv.put(CirclesContract.CircleHistoryEntry.COLUMN_CIRCLE_INDEX, circleIdx)
            return cv
        }

        override fun save(context: Context, eventId: Int) {
            super.save(context, eventId)
            val contentResolver = context.contentResolver
            contentResolver.insert(CirclesContract.CircleHistoryEntry.CONTENT_URI,
                getCircleHistoryItemContentValues(startCircle, eventId, circleIdx))
            contentResolver.insert(CirclesContract.CircleHistoryEntry.CONTENT_URI,
                getCircleHistoryItemContentValues(endCircle, eventId, circleIdx))
        }
    }
}