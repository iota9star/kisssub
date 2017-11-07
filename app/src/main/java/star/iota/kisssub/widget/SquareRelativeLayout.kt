package star.iota.kisssub.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout

class SquareRelativeLayout : RelativeLayout {

    constructor(context: Context, attrs: AttributeSet, defStyle: Int)
            : super(context, attrs, defStyle)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context) : super(context)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wms: Int
        val hms: Int = widthMeasureSpec
        setMeasuredDimension(View.getDefaultSize(0, widthMeasureSpec),
                View.getDefaultSize(0, heightMeasureSpec))

        val childWidthSize = measuredWidth
        wms = View.MeasureSpec.makeMeasureSpec(
                childWidthSize, View.MeasureSpec.EXACTLY)
        super.onMeasure(wms, hms)
    }
}