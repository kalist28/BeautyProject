package ru.kalistratov.template.beauty.presentation.feature.main.view

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.infrastructure.Application
import ru.kalistratov.template.beauty.infrastructure.base.BaseActivity
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelFactory
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.presentation.entity.OnBackPressedCallbackWrapper
import ru.kalistratov.template.beauty.presentation.entity.RequestPermission
import ru.kalistratov.template.beauty.presentation.entity.RequestPermissionsResult
import ru.kalistratov.template.beauty.presentation.extension.onBackPressClicks
import ru.kalistratov.template.beauty.presentation.feature.main.MainState
import ru.kalistratov.template.beauty.presentation.feature.main.MainViewModel
import ru.kalistratov.template.beauty.presentation.feature.main.di.MainModule
import ru.kalistratov.template.beauty.presentation.view.LoadingAlertDialog
import javax.inject.Inject


interface AdditionalBackPressCallBackOwner {
    fun addAdditionalCallback(callback: OnBackPressedCallback)
    fun removeAdditionalCallback()
}

sealed class MainIntent : BaseIntent {
    data class RequestPermissionsResultReceived(val result: RequestPermissionsResult) : MainIntent()

    object OnBackPressed : MainIntent()
}

class MainActivity : BaseActivity(), BaseView<MainIntent, MainState>,
    AdditionalBackPressCallBackOwner {
    private lateinit var navController: NavController

    private var confirmBackPress: Boolean = false

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    private var additionalCallback: OnBackPressedCallback? = null
    private val onBackPressedCallbackWrapper = OnBackPressedCallbackWrapper()

    val fragmentBackPress: () -> Boolean = { true }

    var loadingDialog: LoadingAlertDialog? = null

    private val requestPermissionsResults = mutableSharedFlow<RequestPermissionsResult>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initNavController()
        loadingDialog = LoadingAlertDialog(this)
        (applicationContext as Application).activity = this

        with(viewModel) {
            viewModelScope.launch {
                stateUpdates()
                    .collect(::render)
            }.addTo(jobComposite)
            processIntent(intents())
        }

        onBackPressedCallbackWrapper.callback
            ?.let { onBackPressedDispatcher.addCallback(this, it) }
    }

    fun updateNavGraph() {
        val finalHost = NavHostFragment.create(R.navigation.nav_graph)
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, finalHost)
            .setPrimaryNavigationFragment(finalHost) // equivalent to app:defaultNavHost="true"
            .commit()
    }

    fun initNavController() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        val user = authSettingsService.getUserId()
        loge("User - $user")
        if (user.isNullOrBlank()) navGraph.setStartDestination(R.id.authFragment)
        else navGraph.setStartDestination(R.id.reservationListFragment)
        navController.setGraph(navGraph, null)
        (applicationContext as Application).navController = navController
    }

    override fun injectComponent() = appComponent.plus(MainModule()).inject(this)

    override fun onBackPressed() {
        // if (fragmentBackPress.invoke()) return TODO
        if (confirmBackPress) return super.onBackPressed()
        if (additionalCallback?.isEnabled == true) return additionalCallback!!.handleOnBackPressed()
        val popBackStackResult = navController.popBackStack()
        if (!popBackStackResult) {
            Toast.makeText(
                applicationContext,
                "Нажмите повторно для выхода.",
                Toast.LENGTH_SHORT
            ).show()
            onBackPressedCallbackWrapper.callback?.handleOnBackPressed()
        }
    }

    override fun onPause() {
        loadingDialog?.show(false)
        super.onPause()
    }

    override fun onDestroy() {
        loadingDialog?.cancel()
        super.onDestroy()
    }

    override fun intents(): Flow<MainIntent> = merge(
        onBackPressClicks(onBackPressedCallbackWrapper).map { MainIntent.OnBackPressed },
        requestPermissionsResults.map { MainIntent.RequestPermissionsResultReceived(it) }
    )

    override fun render(state: MainState) {
        confirmBackPress = state.allowOnBackPress
        state.requestPermission?.let { requestPermission(it) }
    }

    override fun addAdditionalCallback(callback: OnBackPressedCallback) {
        additionalCallback = callback
    }

    override fun removeAdditionalCallback() {
        additionalCallback = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val isGranted = when (grantResults.isEmpty()) {
            true -> false
            false -> grantResults.first() == PackageManager.PERMISSION_GRANTED
        }

        requestPermissionsResults.tryEmit(
            RequestPermissionsResult(
                requestCode,
                isGranted
            )
        )
    }

    private fun requestPermission(request: RequestPermission) = ActivityCompat
        .requestPermissions(
            this,
            request.permissions.toTypedArray(),
            request.code
        )

}

