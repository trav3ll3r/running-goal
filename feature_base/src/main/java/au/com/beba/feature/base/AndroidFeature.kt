package au.com.beba.feature.base

import android.content.Context
import android.view.View

interface AndroidFeature : Feature {

    fun bootstrap(application: Context)

    fun getPromoView(context: Context): View? {
        return null
    }
}