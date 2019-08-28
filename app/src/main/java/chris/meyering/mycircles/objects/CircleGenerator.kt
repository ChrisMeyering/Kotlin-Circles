package chris.meyering.mycircles.objects

import android.graphics.Color
import java.util.*
import kotlin.math.max
import kotlin.math.min

open class CircleGenerator (){

    companion object {
        private const val MIN_RADIUS = 30.0f
        private const val MAX_RADIUS = 300.0f
    }

    var radius: Float = MIN_RADIUS
        set(value) {field = max(MIN_RADIUS, min(value, MAX_RADIUS))}


    private val rand : Random = Random()
    var color : Int = getRandColor()

    private fun getRandColor(): Int {
        return Color.rgb(getRand255(), getRand255(), getRand255())
    }

    private fun getRand255(): Int {
        return rand.nextInt(256)
    }

    fun getCircle(p: Point): Circle {
        return  Circle(p, radius, color)
    }

    fun nextColor() {
        color = getRandColor()
    }


}