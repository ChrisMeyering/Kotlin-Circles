package chris.meyering.mycircles

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import chris.meyering.mycircles.objects.Circle

class CircleDetailActivity : AppCompatActivity() {

    companion object {
        private val INTENT_CIRCLE_DATA = "circle_data"

        fun newIntent(context: Context, circle: Circle): Intent
                = Intent(context, CircleDetailActivity::class.java).apply {putExtra(INTENT_CIRCLE_DATA, circle)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circle_detail)
    }
}
