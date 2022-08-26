package ru.kalistratov.template.beauty.presentation.feature.personalarea.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.di.UserComponent
import ru.kalistratov.template.beauty.domain.di.ViewModelFactory
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.presentation.extension.clicks
import ru.kalistratov.template.beauty.presentation.extension.find
import ru.kalistratov.template.beauty.presentation.feature.personalarea.PersonalAreaRouter
import ru.kalistratov.template.beauty.presentation.feature.personalarea.PersonalAreaState
import ru.kalistratov.template.beauty.presentation.feature.personalarea.PersonalAreaViewModel
import ru.kalistratov.template.beauty.presentation.feature.personalarea.di.PersonalAreaModule

sealed class PersonalAreaIntent : BaseIntent {
    data class MenuItemClick(val id: Int) : PersonalAreaIntent()

    object InitData : PersonalAreaIntent()
    object UserPanelClick : PersonalAreaIntent()
}

class PersonalAreaFragment : BaseFragment(), BaseView<PersonalAreaIntent, PersonalAreaState> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var profileRouter: PersonalAreaRouter

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[PersonalAreaViewModel::class.java]
    }

    private lateinit var userPanel: View
    private lateinit var recyclerView: RecyclerView

    private val menuController = PersonalAreaMenuController()

    override fun findViews() {
        userPanel = find(R.id.user_panel)
        recyclerView = find(R.id.recycler_view)
        find<BottomNavigationView>(R.id.bottom_nav_view).apply {
            selectedItemId = R.id.menu_personal_area
            setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_profile -> profileRouter.openProfile()
                    R.id.menu_calendar -> profileRouter.openCalendar()
                    R.id.menu_timetable -> profileRouter.openTimetable()
                }
                return@setOnItemSelectedListener true
            }
        }
    }

    override fun injectUserComponent(userComponent: UserComponent) =
        userComponent.plus(PersonalAreaModule(this)).inject(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_personal_area, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewModel) {
            viewModelScope.launch(Dispatchers.Main) {
                stateUpdates()
                    .collect(::render)
            }.addTo(jobComposite)
            processIntent(intents())
        }

        recyclerView.apply {
            adapter = menuController.adapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun intents(): Flow<PersonalAreaIntent> = merge(
        flowOf(PersonalAreaIntent.InitData),
        userPanel.clicks().map { PersonalAreaIntent.UserPanelClick },
        menuController.clicks().map { PersonalAreaIntent.MenuItemClick(it) },
    )

    override fun render(state: PersonalAreaState) {
        menuController.items = state.menuItems
        menuController.requestModelBuild()
    }
}
