package ru.kalistratov.template.beauty.presentation.feature.main.view

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.di.ViewModelFactory
import ru.kalistratov.template.beauty.infrastructure.base.BaseActivity
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.presentation.entity.OnBackPressCallback
import ru.kalistratov.template.beauty.presentation.extension.onBackPressClicks
import ru.kalistratov.template.beauty.presentation.feature.main.MainState
import ru.kalistratov.template.beauty.presentation.feature.main.MainViewModel
import ru.kalistratov.template.beauty.presentation.feature.main.di.MainModule

sealed class MainIntent : BaseIntent {
    object OnBackPressed : MainIntent()
}

class MainActivity : BaseActivity(), BaseView<MainIntent, MainState> {
    private lateinit var navController: NavController

    private var confirmBackPress: Boolean = false

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    private val onBackPressCallback = OnBackPressCallback()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        val user = authSettingsService.getUser()
        if (user.isNullOrBlank()) navGraph.setStartDestination(R.id.authFragment)
        else navGraph.setStartDestination(R.id.timetableFragment)
        navController.setGraph(navGraph, null)

        with(viewModel) {
            viewModelScope.launch {
                stateUpdates()
                    .collect(::render)
            }.addTo(jobComposite)
            processIntent(intents())
        }
    }

    override fun injectComponent() = appComponent.plus(MainModule()).inject(this)

    override fun onBackPressed() {
        if (confirmBackPress) return super.onBackPressed()
        val popBackStackResult = navController.popBackStack()
        if (!popBackStackResult) {
            Toast.makeText(
                applicationContext,
                "Нажмите повторно для выхода.",
                Toast.LENGTH_SHORT
            ).show()
            onBackPressCallback.listener?.OnBackPressed()
        }
    }

    override fun intents(): Flow<MainIntent> = merge(
        onBackPressClicks(onBackPressCallback).map { MainIntent.OnBackPressed }
    )

    override fun render(state: MainState) {
        confirmBackPress = state.allowOnBackPress
    }
}
