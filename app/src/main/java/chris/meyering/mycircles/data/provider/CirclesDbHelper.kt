package chris.meyering.mycircles.data.provider

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns


class CirclesDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_VISIBLE_CIRCLES_TABLE)
        db.execSQL(SQL_CREATE_EVENT_HISTORY_TABLE)
        db.execSQL(SQL_CREATE_CIRCLE_HISTORY_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_VISIBLE_CIRCLES_TABLE)
        db.execSQL(SQL_DELETE_EVENT_HISTORY_TABLE)
        db.execSQL(SQL_DELETE_CIRCLE_HISTORY_TABLE)
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "circles.db"
        private const val DATABASE_VERSION = 2

        /*********************************
         *      Visible Circles SQL
         *********************************/
        const val SQL_CREATE_VISIBLE_CIRCLES_TABLE = ("CREATE TABLE ${CirclesContract.VisibleCirclesEntry.TABLE_NAME} ("
                + "${BaseColumns._ID} INTEGER PRIMARY KEY, "
                + "${CirclesContract.VisibleCirclesEntry.COLUMN_X} REAL NOT NULL, "
                + "${CirclesContract.VisibleCirclesEntry.COLUMN_Y} REAL NOT NULL, "
                + "${CirclesContract.VisibleCirclesEntry.COLUMN_R} REAL NOT NULL, "
                + "${CirclesContract.VisibleCirclesEntry.COLUMN_COLOR} INTEGER NOT NULL"
                + ");")

        const val SQL_DELETE_VISIBLE_CIRCLES_TABLE =
            "DROP TABLE IF EXISTS ${CirclesContract.VisibleCirclesEntry.TABLE_NAME}"


        /*********************************
         *    Event History SQL
         *********************************/
        const val SQL_CREATE_EVENT_HISTORY_TABLE = ("CREATE TABLE ${CirclesContract.EventHistoryEntry.TABLE_NAME} ("
                + "${BaseColumns._ID} INTEGER PRIMARY KEY, "
                + "${CirclesContract.EventHistoryEntry.COLUMN_EVENT_ID} INTEGER NOT NULL UNIQUE, "
                + "${CirclesContract.EventHistoryEntry.COLUMN_EVENT_TYPE} TEXT NOT NULL"
                + ");")

        const val SQL_DELETE_EVENT_HISTORY_TABLE =
            "DROP TABLE IF EXISTS ${CirclesContract.EventHistoryEntry.TABLE_NAME}"


        /*********************************
         *     Circle History SQL
         *********************************/
        const val SQL_CREATE_CIRCLE_HISTORY_TABLE = ("CREATE TABLE ${CirclesContract.CircleHistoryEntry.TABLE_NAME} ("
                + "${BaseColumns._ID} INTEGER PRIMARY KEY, "
                + "${CirclesContract.CircleHistoryEntry.COLUMN_EVENT_ID} INTEGER NOT NULL, "
                + "${CirclesContract.CircleHistoryEntry.COLUMN_CIRCLE_INDEX} INTEGER, "
                + "${CirclesContract.CircleHistoryEntry.COLUMN_X} REAL NOT NULL, "
                + "${CirclesContract.CircleHistoryEntry.COLUMN_Y} REAL NOT NULL, "
                + "${CirclesContract.CircleHistoryEntry.COLUMN_R} REAL NOT NULL, "
                + "${CirclesContract.CircleHistoryEntry.COLUMN_COLOR} INTEGER NOT NULL"
                + ");")

        const val SQL_DELETE_CIRCLE_HISTORY_TABLE = "DROP TABLE IF EXISTS ${CirclesContract.CircleHistoryEntry.TABLE_NAME}"

    }
}