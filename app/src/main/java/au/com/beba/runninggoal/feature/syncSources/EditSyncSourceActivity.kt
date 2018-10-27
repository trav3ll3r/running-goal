package au.com.beba.runninggoal.feature.syncSources

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.domain.workout.sync.SourceType
import au.com.beba.runninggoal.domain.workout.sync.SyncSource
import au.com.beba.runninggoal.feature.sync.SyncFeature
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_edit_sync_source.*
import org.jetbrains.anko.find
import timber.log.Timber
import javax.inject.Inject


class EditSyncSourceActivity : AppCompatActivity() {

    @Inject
    lateinit var syncFeature: SyncFeature

    private lateinit var syncSource: SyncSource
    private lateinit var spinnerTypes: AppCompatSpinner

    companion object {
        private const val SYNC_SOURCE_ID = "SYNC_SOURCE_ID"

        fun buildIntent(context: Context, syncSourceId: Long?): Intent {
            val intent = Intent(context, EditSyncSourceActivity::class.java)
            if (syncSourceId != null) {
                intent.putExtra(SYNC_SOURCE_ID, syncSourceId)
            }
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_sync_source)

        val syncSourceId = extractIntentData(intent)

        initForm()

        syncSource = resolveSyncSource(syncSourceId)
        //launch(UI) {
        bindData()
        //}
    }

    private fun extractIntentData(intent: Intent): Long {
        var syncSourceId: Long = 0
        val extras = intent.extras
        if (extras != null) {
            syncSourceId = extras.getLong(SYNC_SOURCE_ID, 0)
            Timber.d("extractIntentData | syncSourceId=%s".format(syncSourceId))
        }

        return syncSourceId
    }

    private fun resolveSyncSource(syncSourceId: Long): SyncSource {
        Timber.i("resolveSyncSource")
        val result = syncFeature.getById(syncSourceId)
        return result ?: SyncSource()
    }

    /**
     * Load all SyncSource Types and populate dropdown
     */
    private fun initForm() {
        spinnerTypes = find(R.id.sync_source_type)

        val sourceTypes = syncFeature.getSyncSourceTypes()

        val adapter = SourceTypesAdapter(this, sourceTypes)
        spinnerTypes.adapter = adapter
//        spinnerTypes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}
//        }

    }

    private fun bindData() {
        Timber.i("bindData")
        val sourceIndex = if (syncSource.type.isEmpty()) {
            0
        } else {
            val sourceTypes = syncFeature.getSyncSourceTypes()
            sourceTypes.indexOfFirst { it.type == syncSource.type }
        }
        spinnerTypes.setSelection(sourceIndex)

        sync_source_nickname.setText(syncSource.nickname)
        sync_source_access_token.setText(syncSource.accessToken)
        sync_source_is_default.isChecked = syncSource.isDefault
        btn_ok.setOnClickListener {
            updateAndClose()
            finish()
        }
    }

    private fun updateAndClose() {
        Timber.i("updateAndClose")
        syncSource.nickname = sync_source_nickname.text.toString()
        syncSource.accessToken = sync_source_access_token.text.toString()
        syncSource.type = (spinnerTypes.selectedItem as SourceType).type
        syncSource.isDefault = sync_source_is_default.isChecked
        syncFeature.storeSyncSource(syncSource)
    }
}