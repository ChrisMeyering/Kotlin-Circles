package chris.meyering.mycircles

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.GridLayoutAnimationController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


/**
 * RecyclerView with support for grid animations.
 * <p>
 * Based on:
 * https://gist.github.com/Musenkishi/8df1ab549857756098ba
 * Credit to Freddie (Musenkishi) Lust-Hed
 * <p>
 * ...which in turn is based on the GridView implementation of attachLayoutParameters(...):
 * https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/widget/GridView.java
 * Copied on 04/25/2018 by Chris Meyering.
 * Source:https://gist.github.com/patrick-iv/e28cedb927acfca8a6a4778ef1855964#file-gridrecyclerview-java
 */

class GridRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0):
    RecyclerView (context, attrs, defStyle){

    override fun attachLayoutAnimationParameters(
        child: View?,
        params: ViewGroup.LayoutParams?,
        index: Int,
        count: Int
    ) {
        if (adapter != null && layoutManager is GridLayoutManager) {
            var animationParams: GridLayoutAnimationController.AnimationParameters? =
                params?.layoutAnimationParameters as GridLayoutAnimationController.AnimationParameters
            if (animationParams == null) {
                animationParams = GridLayoutAnimationController.AnimationParameters()
                params.layoutAnimationParameters = animationParams
            }

            animationParams.count = count
            animationParams.index = index
            val columns: Int = (layoutManager as GridLayoutManager).spanCount
            animationParams.columnsCount = columns
            animationParams.rowsCount = count / columns

            val invertedIdx: Int = count - 1 - index
            animationParams.column = columns - 1 - (invertedIdx % columns)
            animationParams.row = animationParams.rowsCount - 1 - invertedIdx / columns
        } else {
            super.attachLayoutAnimationParameters(child, params, index, count)
        }
    }
}