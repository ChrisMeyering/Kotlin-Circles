package chris.meyering.mycircles

import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import chris.meyering.mycircles.data.provider.CirclesContract
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.lang.Exception

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {
    var isMenuExpanded = false
    lateinit var fabUndo: FloatingActionButton
    lateinit var fabList: FloatingActionButton
    lateinit var fabClear: FloatingActionButton
    lateinit var fabMenu: FloatingActionButton
    lateinit var fabRedo: FloatingActionButton
    lateinit var dv: DrawView

    companion object {
        const val LOADER_ALL_CIRCLES_ID = 11
        const val LOADER_EVENT_HISTORY_ID = 31
    }

    override fun onStop() {
        dv.save()
        super.onStop()
    }

    override fun onRestart() {
        initLoaders()
        super.onRestart()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(this, getQueryUri(id), null, null, null, null)
    }

    private fun getQueryUri(id: Int): Uri {
        return when (id) {
            LOADER_ALL_CIRCLES_ID -> CirclesContract.VisibleCirclesEntry.CONTENT_URI
            LOADER_EVENT_HISTORY_ID -> CirclesContract.EventHistoryEntry.CONTENT_URI
            else -> throw Exception("Unknown Loader id: $id")
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        if (data != null && !data.isClosed) {
            when (loader.id) {
                LOADER_ALL_CIRCLES_ID -> data.use {dv.initCircles(it)}
                LOADER_EVENT_HISTORY_ID -> dv.setHistoryCursor(data)
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()
        initLoaders()
    }

    fun initLoader(loaderID: Int) {
        val lm: LoaderManager = LoaderManager.getInstance(this)
        val cursorLoader: Loader<Cursor>? = lm.getLoader<Cursor>(loaderID)
        if (cursorLoader == null) {
            lm.initLoader(loaderID, null, this)
        } else {
            lm.restartLoader(loaderID, null, this)
        }
    }



    fun initLoaders() {
        dv.setHistoryLoaderManager(LoaderManager.getInstance(this))
        initLoader(LOADER_ALL_CIRCLES_ID)
//        initLoader(LOADER_CIRCLE_HISTORY_ID)
        initLoader(LOADER_EVENT_HISTORY_ID)
    }

    fun bindViews() {
        fabUndo = findViewById(R.id.fab_undo)
        fabRedo = findViewById(R.id.fab_redo)
        fabList = findViewById(R.id.fab_list)
        fabClear = findViewById(R.id.fab_clear)
        fabMenu = findViewById(R.id.fab_menu)
        dv = findViewById(R.id.dv)

        dv.setOnClickListener {
            if (isMenuExpanded)
                hideMenuItems()
        }

        fabList.setOnClickListener {
            startActivity(CircleListActivity.newIntent(this))
        }
        fabUndo.setOnClickListener {
            if (!dv.undo()) {

            }
            updateFABs()

        }
        fabRedo.setOnClickListener {
            if (!dv.redo()) {

            }
            updateFABs()
        }
        fabClear.setOnClickListener{
            if (dv.clear()) {

            }
            updateFABs()
        }

        fabMenu.setOnClickListener{
            onMenuFabClicked()
        }
    }

    private fun updateFAB(fab: FloatingActionButton, useable: Boolean) {
        if (useable) {
            fab.setColorFilter(ContextCompat.getColor(this, R.color.colorClickable))
            fab.isClickable = true
        } else {
            fab.setColorFilter(ContextCompat.getColor(this, R.color.colorUnclickable))
            fab.isClickable = false
        }
    }
    private fun updateFABs() {
        updateFAB(fabUndo, dv.ch.chm.undoable)
        updateFAB(fabRedo, dv.ch.chm.redoable)
        updateFAB(fabClear, dv.ch.clearable)
    }

    fun hideMenuItems() {
        isMenuExpanded = false

        fabRedo.animate().translationX(0f)
        fabUndo.animate().translationX(0f)
        fabClear.animate().translationY(0f)
        fabList.animate().translationX(0f)
        fabList.animate().translationY(0f)

        fabUndo.hide()
        fabClear.hide()
        fabList.hide()
        fabRedo.hide()
    }

    fun showMenuItems() {
        isMenuExpanded = true
        updateFABs()

        fabUndo.show()
        fabClear.show()
        fabList.show()
        fabRedo.show()

        fabRedo.animate().translationX(180f)
        fabUndo.animate().translationX(-180f)
        fabClear.animate().translationY(-180f)
        fabList.animate().translationX(-127.2792f)
        fabList.animate().translationY(-127.2792f)
    }

    fun onMenuFabClicked() {
        if (isMenuExpanded) {
            hideMenuItems()
        } else {
            showMenuItems()
        }
    }
}
