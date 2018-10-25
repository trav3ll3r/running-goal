package au.com.beba.runninggoal.feature.syncSources

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.domain.workout.sync.SyncSource
import au.com.beba.runninggoal.feature.sync.SyncFeature
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_edit_sync_source.*
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import timber.log.Timber
import javax.inject.Inject


class EditSyncSourceActivity : AppCompatActivity() {

    @Inject
    lateinit var syncFeature: SyncFeature

    private lateinit var syncSource: SyncSource

    companion object {
        private const val SYNC_SOURCE_TYPE = "SYNC_SOURCE_TYPE"

        fun buildIntent(context: Context, syncSource: SyncSource): Intent {
            val intent = Intent(context, EditSyncSourceActivity::class.java)
            intent.putExtra(SYNC_SOURCE_TYPE, syncSource.type)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_sync_source)

        launch(UI) {
            loadSyncSource(intent.getStringExtra(SYNC_SOURCE_TYPE))
            bindData()
        }
    }

    private suspend fun loadSyncSource(syncSourceType: String) = withContext(Dispatchers.Default) {
        Timber.i("loadSyncSource")
        syncSource = withContext(Dispatchers.Default) { syncFeature.getSyncSourceForType(syncSourceType) }
    }

    private fun bindData() {
        Timber.i("bindData")
        sync_source_type.text = syncSource.type
        sync_source_access_token.setText(syncSource.accessToken)
        sync_source_is_active.isChecked = syncSource.isDefault
        btn_ok.setOnClickListener {
            launch(UI) {
                updateAndClose()
                finish()
            }
        }
    }

    private suspend fun updateAndClose() {
        Timber.i("updateAndClose")
        syncSource.accessToken = sync_source_access_token.text.toString()
        syncSource.isDefault = sync_source_is_active.isChecked
        withContext(Dispatchers.Default) { syncFeature.storeSyncSource(syncSource) }
    }
}