package chris.meyering.mycircles.objects

import android.os.Parcel
import android.os.Parcelable

class Circle(var p: Point?, var r: Float = 30f, var color: Int = 0, var isVisible: Boolean = true): Parcelable
{
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Point::class.java.classLoader),
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    fun intersects (p: Point): Boolean  = p.distance(this.p!!) < r
    fun moveTo(p: Point) {
        this.p = p
    }
    operator fun plus (p: Point): Point  = Point(this.p!!.x + p.x, this.p!!.y + p.y)

    operator fun minus (p: Point): Point = Point(this.p!!.x - p.x, this.p!!.y - p.y)
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(p, flags)
        parcel.writeFloat(r)
        parcel.writeInt(color)
        parcel.writeByte(if (isVisible) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Circle> {
        override fun createFromParcel(parcel: Parcel): Circle {
            return Circle(parcel)
        }

        override fun newArray(size: Int): Array<Circle?> {
            return arrayOfNulls(size)
        }
    }
}

