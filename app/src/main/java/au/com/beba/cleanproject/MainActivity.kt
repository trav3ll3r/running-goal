package au.com.beba.cleanproject

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import au.com.beba.cleanproject.feature.LocalPreferences
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject lateinit var localPreferences: LocalPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}