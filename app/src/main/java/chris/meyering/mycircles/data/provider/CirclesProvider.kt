package chris.meyering.mycircles.data.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.net.Uri

class CirclesProvider : ContentProvider() {
    private fun getTableName(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            CODE_VISIBLE, CODE_VISIBLE_WITH_ID -> CirclesContract.VisibleCirclesEntry.TABLE_NAME
            CODE_CIRCLE_HISTORY, CODE_CIRCLE_HISTORY_WITH_ID -> CirclesContract.CircleHistoryEntry.TABLE_NAME
            CODE_EVENT_HISTORY, CODE_EVENT_HISTORY_WITH_ID -> CirclesContract.EventHistoryEntry.TABLE_NAME
            else -> throw SQLException("Unrecognized URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = mOpenHelper.writableDatabase
        val id = db.insert(getTableName(uri), null, values)
        if (id >= 0) {
            context!!.contentResolver.notifyChange(uri, null)
            return uri.buildUpon().appendPath(id.toString()).build()
        } else {
            throw SQLException("Failed to insert row into uri: $uri")
        }
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {

        when (uriMatcher.match(uri)) {
            CODE_CIRCLE_HISTORY_WITH_ID -> {
                val cursor: Cursor = mOpenHelper.readableDatabase.query(
                    getTableName(uri),
                    projection,
                    CirclesContract.CircleHistoryEntry.selectionWithId(),
                    Array<String>(1){uri.lastPathSegment.toString()},
                    null,
                    null,
                    sortOrder
                )
                cursor.setNotificationUri(context!!.contentResolver, uri);
                return cursor
            }
            else -> {
                val cursor: Cursor = mOpenHelper.readableDatabase.query(
                    getTableName(uri),
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )
                cursor.setNotificationUri(context!!.contentResolver, uri);
                return cursor
            }

        }
    }

    override fun onCreate(): Boolean {
        mOpenHelper = CirclesDbHelper(context!!)
        return true
    }

    //    db.update("people", contentValues, "id = ?", new String[] {"1"});
    override fun update(uri: Uri, values: ContentValues?, whereClause: String?, whereArgs: Array<out String>?): Int {
        val db = mOpenHelper.writableDatabase
        return db.update(getTableName(uri), values, whereClause, whereArgs)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        when (uriMatcher.match(uri)) {
            CODE_VISIBLE, CODE_EVENT_HISTORY, CODE_CIRCLE_HISTORY-> {
                val db = mOpenHelper.writableDatabase
                return db.delete(
                    getTableName(uri),
                    null,
                    null
                )
            }
            CODE_VISIBLE_WITH_ID, CODE_EVENT_HISTORY_WITH_ID, CODE_CIRCLE_HISTORY_WITH_ID -> {
                val db = mOpenHelper.writableDatabase
                return db.delete(
                    getTableName(uri),
                    CirclesContract.VisibleCirclesEntry.selectionWithId(),
                    arrayOf(uri.lastPathSegment.toString())
                )
            }
        }
        return 0
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    companion object {
        const val CODE_VISIBLE = 100
        const val CODE_VISIBLE_WITH_ID = 101
        const val CODE_EVENT_HISTORY = 200
        const val CODE_EVENT_HISTORY_WITH_ID = 201
        const val CODE_CIRCLE_HISTORY = 300
        const val CODE_CIRCLE_HISTORY_WITH_ID = 301
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        private lateinit var mOpenHelper: CirclesDbHelper
    }

    init {
        uriMatcher.addURI(CirclesContract.CONTENT_AUTHORITY,
            CirclesContract.PATH_VISIBLE_CIRCLES, CODE_VISIBLE)
        uriMatcher.addURI(CirclesContract.CONTENT_AUTHORITY,
            "${CirclesContract.PATH_VISIBLE_CIRCLES}/#", CODE_VISIBLE_WITH_ID)

        uriMatcher.addURI(CirclesContract.CONTENT_AUTHORITY,
            CirclesContract.PATH_EVENT_HISTORY, CODE_EVENT_HISTORY)
        uriMatcher.addURI(CirclesContract.CONTENT_AUTHORITY,
            "${CirclesContract.PATH_EVENT_HISTORY}/#", CODE_EVENT_HISTORY_WITH_ID)

        uriMatcher.addURI(CirclesContract.CONTENT_AUTHORITY,
            CirclesContract.PATH_CIRCLE_HISTORY, CODE_CIRCLE_HISTORY)
        uriMatcher.addURI(CirclesContract.CONTENT_AUTHORITY,
            "${CirclesContract.PATH_CIRCLE_HISTORY}/#", CODE_CIRCLE_HISTORY_WITH_ID)

    }
}