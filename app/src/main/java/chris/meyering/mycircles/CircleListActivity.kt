package chris.meyering.mycircles

import android.content.Context
import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import chris.meyering.mycircles.data.provider.CirclesContract
import chris.meyering.mycircles.objects.Circle

class CircleListActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor>, CircleListAdapter.CircleListAdapterClickHandler {
    val circleAdapter = CircleListAdapter(this)

    override fun onClick(c: Circle) {
        startActivity(CircleDetailActivity.newIntent(this, c))
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        Log.i("CircleListActivity", "Loader created")
        findViewById<ProgressBar>(R.id.pb_loading_circles).visibility = View.VISIBLE
        return CursorLoader(this, CirclesContract.VisibleCirclesEntry.CONTENT_URI, null, null, null, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        Log.i("CircleListActivity", "Load finished --- got ${data?.count} items")
        findViewById<ProgressBar>(R.id.pb_loading_circles).visibility = View.INVISIBLE
        if (data != null && !data.isClosed) {
            data.setNotificationUri(contentResolver, CirclesContract.VisibleCirclesEntry.CONTENT_URI)
            circleAdapter.swapCursor(data)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {

    }

    lateinit var rvCircles: RecyclerView
    companion object {
        const val LOADER_ALL_CIRCLES_ID = 14
        fun newIntent(context: Context): Intent
                = Intent(context, CircleListActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circle_list)
        bindViews()
        initLoader()
    }

    fun initLoader() {
        val lm: LoaderManager = LoaderManager.getInstance(this)
        val cursorLoader: Loader<Cursor>? = lm.getLoader<Cursor>(LOADER_ALL_CIRCLES_ID)
        if (cursorLoader == null) {
            lm.initLoader(LOADER_ALL_CIRCLES_ID, null, this)
        } else {
            lm.restartLoader(LOADER_ALL_CIRCLES_ID, null, this)
        }
    }

    fun bindViews() {
        rvCircles = findViewById(R.id.rv_circles)
        rvCircles.setHasFixedSize(true)
        rvCircles.setItemViewCacheSize(30)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, 2)
        layoutManager.isAutoMeasureEnabled = true
        rvCircles.layoutManager = layoutManager
        rvCircles.adapter= circleAdapter
    }
}
