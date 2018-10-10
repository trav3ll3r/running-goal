package au.com.beba.runninggoal.stylist

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.StyleRes

@StyleRes
fun Context.getThemeStyle(@AttrRes styleId: Int?): Int? {
    if (styleId != null) {
        val typedValue = TypedValue()
        theme.resolveAttribute(styleId, typedValue, true)
        return typedValue.data
    }
    return null
}

@ColorInt
fun Context.getStyleColour(@StyleRes styleId: Int, @AttrRes attrId: Int, @ColorInt defaultColor: Int = Color.TRANSPARENT): Int {
    // The attributes we want to retrieve
    val attrs = intArrayOf(attrId)

    // Parse the Style, using Context.obtainStyledAttributes()
    val typedArray = obtainStyledAttributes(styleId, attrs)

    // Fetch the colour from the style
    val result = typedArray.getColor(0, defaultColor)

    typedArray.recycle()

    return result
}

@Dimension
fun Context.getStyleDimensionPixelSize(@StyleRes styleId: Int, @AttrRes attrId: Int, @Dimension default: Int = 0): Int {
    // The attributes we want to retrieve
    val attrs = intArrayOf(attrId)

    // Parse the Style, using Context.obtainStyledAttributes()
    val typedArray = obtainStyledAttributes(styleId, attrs)

    // Fetch the colour from the style
    val result = typedArray.getDimensionPixelSize(0, default)

    typedArray.recycle()

    return result
}

@Dimension
fun Context.getStyleTextSize(@StyleRes styleId: Int, @AttrRes attrId: Int, @Dimension default: Float = 0f): Float {
    // The attributes we want to retrieve
    val attrs = intArrayOf(attrId)

    // Parse the Style, using Context.obtainStyledAttributes()
    val typedArray = obtainStyledAttributes(styleId, attrs)

    // Fetch the colour from the style
    val result = typedArray.getDimension(0, default)

    typedArray.recycle()

    return result
}

@Dimension
fun Context.getStyleInteger(@StyleRes styleId: Int, @AttrRes attrId: Int, default: Int = 0): Int {
    // The attributes we want to retrieve
    val attrs = intArrayOf(attrId)

    // Parse the Style, using Context.obtainStyledAttributes()
    val typedArray = obtainStyledAttributes(styleId, attrs)

    // Fetch the colour from the style
    val result = typedArray.getInt(0, default)

    typedArray.recycle()

    return result
}