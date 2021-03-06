package ru.kalistratov.template.beauty.presentation.feature.profile.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.di.UserComponent
import ru.kalistratov.template.beauty.domain.di.ViewModelFactory
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.presentation.extension.find
import ru.kalistratov.template.beauty.presentation.feature.profile.ProfileRouter
import ru.kalistratov.template.beauty.presentation.feature.profile.ProfileState
import ru.kalistratov.template.beauty.presentation.feature.profile.ProfileViewModel
import ru.kalistratov.template.beauty.presentation.feature.profile.di.ProfileModule
import javax.inject.Inject

sealed class ProfileIntent : BaseIntent

class ProfileFragment : BaseFragment(), BaseView<ProfileIntent, ProfileState> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var profileRouter: ProfileRouter

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[ProfileViewModel::class.java]
    }

    override fun findViews() {

        find<BottomNavigationView>(R.id.bottom_nav_view).apply {
            selectedItemId = R.id.menu_profile
            setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_timetable -> profileRouter.openTimetable()
                    R.id.menu_personal_area -> profileRouter.openPersonalArea()
                    R.id.menu_calendar -> profileRouter.openCalendar()
                }
                return@setOnItemSelectedListener true
            }
        }
    }

    override fun injectUserComponent(userComponent: UserComponent) =
        userComponent.plus(ProfileModule(this)).inject(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewModel) {
            viewModelScope.launch {
                stateUpdates()
                    .collect(::render)
            }.addTo(jobComposite)
            processIntent(intents())
        }
    }

    override fun intents(): Flow<ProfileIntent> = emptyFlow()

    override fun render(state: ProfileState) {
    }
}
