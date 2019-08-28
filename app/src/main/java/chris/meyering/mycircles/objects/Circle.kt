package chris.meyering.mycircles.objects

import android.content.ContentValues
import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import chris.meyering.mycircles.data.provider.CirclesContract

data class Circle(var p: Point, var r: Float = 30f, var color: Int = 0, var isVisible: Boolean = true) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Point::class.java.classLoader)!!,
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte()
    )

    fun intersects(p: Point): Boolean = p.distance(this.p) < r
    fun moveTo(p: Point) {
        this.p = p
    }

    operator fun plus(p: Point): Point = Point(this.p.x + p.x, this.p.y + p.y)

    operator fun minus(p: Point): Point = Point(this.p.x - p.x, this.p.y - p.y)
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(p, flags)
        parcel.writeFloat(r)
        parcel.writeInt(color)
        parcel.writeByte(if (isVisible) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }


    fun toContentValues(): ContentValues {
        val cv = ContentValues()
        cv.put(CirclesContract.VisibleCirclesEntry.COLUMN_X, p.x)
        cv.put(CirclesContract.VisibleCirclesEntry.COLUMN_Y, p.y)
        cv.put(CirclesContract.VisibleCirclesEntry.COLUMN_R, r)
        cv.put(CirclesContract.VisibleCirclesEntry.COLUMN_COLOR, color)
        return cv
    }

    companion object CREATOR : Parcelable.Creator<Circle> {
        override fun createFromParcel(parcel: Parcel): Circle {
            return Circle(parcel)
        }

        override fun newArray(size: Int): Array<Circle?> {
            return arrayOfNulls(size)
        }
    }

    fun print(): String {
        return "Circle(${p.print()}, $r, $color, $isVisible)"
    }
}

