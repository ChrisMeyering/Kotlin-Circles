package chris.meyering.mycircles

import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import chris.meyering.mycircles.objects.CanvasHistoryManager
import chris.meyering.mycircles.objects.Circle

class CircleListAdapter(val context: Context): RecyclerView.Adapter<CircleListAdapter.CircleViewHolder>() {

    var circles: Cursor? = null
    val clickHandler: CircleListAdapterClickHandler = context as CircleListAdapterClickHandler

    interface CircleListAdapterClickHandler {
        fun onClick(c: Circle)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircleViewHolder {
        Log.i("CircleListAdapter", "creating viewholder")

        val v: View = LayoutInflater.from(context).inflate(R.layout.circle_list_item, parent, false)
        return CircleViewHolder(v)
    }

    override fun getItemCount(): Int {
        if (circles == null) return 0
        return circles!!.count
    }


    fun swapCursor(c: Cursor) {
        Log.i("CircleListAdapter", "Updating cursor")

        circles = c
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: CircleViewHolder, position: Int) {
        if (circles!!.moveToPosition(position)) {
            holder.bind(CanvasHistoryManager.getCircleFromCursor(circles!!))
        }

    }

    inner class CircleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private var circleView: View = itemView.findViewById(R.id.v_circle)
        private var circleX: TextView = itemView.findViewById(R.id.tv_x)
        private var circleY: TextView = itemView.findViewById(R.id.tv_y)
        private var circleR: TextView = itemView.findViewById(R.id.tv_radius)
        private var circleRed: TextView = itemView.findViewById(R.id.tv_colorR)
        private var circleGreen: TextView = itemView.findViewById(R.id.tv_colorG)
        private var circleBlue: TextView = itemView.findViewById(R.id.tv_colorB)

        fun bind(c: Circle) {
            circleView.setBackgroundColor(c.color)
            circleX.text = c.p.x.toString()
            circleY.text = c.p.y.toString()
            circleR.text = c.r.toString()
            circleRed.text = Color.red(c.color).toString()
            circleGreen.text = Color.green(c.color).toString()
            circleBlue.text = Color.blue(c.color).toString()
        }
        override fun onClick(v: View?) {
            if (circles!!.moveToPosition(adapterPosition)) {
                clickHandler.onClick(CanvasHistoryManager.getCircleFromCursor(circles!!))
            }
        }

    }

}