package chris.meyering.mycircles.objects

import android.os.Parcel
import android.os.Parcelable
import kotlin.math.sqrt

class Point(val x: Float = 0f, val y: Float = 0f): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readFloat(),
        parcel.readFloat()
    )

    fun distance(p: Point) : Float {
        val dx: Float = p.x - x
        val dy: Float = p.y - y
        return sqrt(dx * dx + dy * dy)
    }

    operator fun plus (p: Point) : Point = Point(x + p.x, y +  p.y)

    operator fun minus (p: Point) : Point = Point(x - p.x, y - p.y)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(x)
        parcel.writeFloat(y)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Point> {
        override fun createFromParcel(parcel: Parcel): Point {
            return Point(parcel)
        }

        override fun newArray(size: Int): Array<Point?> {
            return arrayOfNulls(size)
        }
    }

}
