package uz.androdev.testproject

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import uz.androdev.testproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() = with(binding) {
        btnCreateEvents.setOnClickListener {
            checkCalendarPermissions {
                viewModel.createCalendarEvents(applicationContext)
            }
        }
        btnDeleteEvents.setOnClickListener {
            checkCalendarPermissions {
                viewModel.deleteCalendarEvents(applicationContext)
            }
        }

        observeUIState()
        consumeEvents()
    }

    private inline fun checkCalendarPermissions(onGranted: () -> Unit) {
        val permissions = arrayOf(
            Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR
        )
        val permissionDenied = permissions.any {
            ContextCompat.checkSelfPermission(
                applicationContext, it
            ) != PackageManager.PERMISSION_GRANTED
        }
        if (permissionDenied) {
            showChoiceAlertNew(
                message = getString(R.string.permission_message),
                positiveButtonTitle = getString(R.string.ok),
                negativeButtonTitle = getString(R.string.cancel),
                onNegativeButtonClicked = {},
                onPositiveButtonClicked = {
                    val isRationale = permissions.any {
                        ActivityCompat.shouldShowRequestPermissionRationale(this, it)
                    }
                    if (isRationale) {
                        tryOpenPermissionSettings()
                    } else {
                        permissionLauncher.launch(permissions)
                    }
                }
            )
        } else {
            onGranted()
        }
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}

    private fun observeUIState() = with(binding) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isProcessing.collect {
                    btnCreateEvents.isClickable = !it
                    btnDeleteEvents.isClickable = !it
                    viewLoading.isVisible = it
                }
            }
        }
    }

    private fun consumeEvents() = lifecycleScope.launch {
        var dialog: Dialog? = null
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.event.filterNotNull().collect {
                when (it) {
                    MainScreenEvent.AppCalendarEventsCreateFailed -> R.string.calendar_event_create_failed
                    MainScreenEvent.AppCalendarEventsCreated -> R.string.calendar_events_created
                    MainScreenEvent.AppCalendarEventsDeleted -> R.string.calendar_events_deleted
                    MainScreenEvent.GoogleCalendarNotFound -> R.string.google_calendar_not_found
                }.also { messageId ->
                    dialog?.dismiss()
                    dialog = showOkAlert(message = getString(messageId))
                }
                viewModel.setScreenEventConsumed()
            }
        }
    }
}