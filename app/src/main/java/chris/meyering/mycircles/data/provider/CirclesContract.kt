package chris.meyering.mycircles.data.provider

import android.net.Uri
import android.provider.BaseColumns

class CirclesContract {
    companion object {
        const val CONTENT_AUTHORITY: String = "chris.meyering.mycircles.data.provider"
        val BASE_CONTENT_URI: Uri = Uri.parse("content://$CONTENT_AUTHORITY")

        const val PATH_VISIBLE_CIRCLES: String = "visible_circles"
        const val PATH_EVENT_HISTORY: String = "event_history"
        const val PATH_CIRCLE_HISTORY: String = "circle_history"
    }


    class VisibleCirclesEntry : BaseColumns {
        companion object {
            val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VISIBLE_CIRCLES).build()
            const val TABLE_NAME = PATH_VISIBLE_CIRCLES

            const val COLUMN_X = "x"
            const val COLUMN_Y = "y"
            const val COLUMN_R = "radius"
            const val COLUMN_COLOR = "color"

            fun selectionWithId(): String {
                return "${BaseColumns._ID} = ?"
            }

            fun selection(): String? {
                return null
            }

            fun selectionArgs(): Array<String>? {
                return null
            }
        }
    }

    class EventHistoryEntry : BaseColumns {
        companion object {
            const val TABLE_NAME = PATH_EVENT_HISTORY
            val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENT_HISTORY).build()

            const val COLUMN_EVENT_TYPE = "event_type"
            const val COLUMN_EVENT_ID = "event_id"

            fun selectionWithId(): String {
                return "${BaseColumns._ID} = ?"
            }

            fun selection(): String? {
                return null
            }

            fun selectionArgs(): Array<String>? {
                return null
            }
        }
    }

    class CircleHistoryEntry : BaseColumns {
        companion object {
            const val TABLE_NAME = PATH_CIRCLE_HISTORY
            val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CIRCLE_HISTORY).build()
            const val COLUMN_EVENT_ID = "event_id"
            const val COLUMN_CIRCLE_INDEX = "circle_idx"
            const val COLUMN_X = "x"
            const val COLUMN_Y = "y"
            const val COLUMN_R = "radius"
            const val COLUMN_COLOR = "color"

            fun getUriWithId(id: Int) : Uri = CONTENT_URI.buildUpon().appendPath(id.toString()).build()
            fun selectionWithId(): String {
                return "$COLUMN_EVENT_ID = ?"
            }

            fun selection(): String? {
                return null
            }

            fun selectionArgs(): Array<String>? {
                return null
            }
        }
    }
}

