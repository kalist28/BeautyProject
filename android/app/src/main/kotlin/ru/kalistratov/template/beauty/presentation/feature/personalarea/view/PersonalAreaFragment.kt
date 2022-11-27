package ru.kalistratov.template.beauty.presentation.feature.personalarea.view

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.*
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.FragmentPersonalAreaBinding
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.di.UserComponent
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelFactory
import ru.kalistratov.template.beauty.presentation.extension.clicks
import ru.kalistratov.template.beauty.presentation.feature.personalarea.PersonalAreaRouter
import ru.kalistratov.template.beauty.presentation.feature.personalarea.PersonalAreaState
import ru.kalistratov.template.beauty.presentation.feature.personalarea.PersonalAreaViewModel
import ru.kalistratov.template.beauty.presentation.feature.personalarea.di.PersonalAreaModule
import javax.inject.Inject


sealed class PersonalAreaIntent : BaseIntent {
    data class MenuItemClick(val id: Int) : PersonalAreaIntent()

    object InitData : PersonalAreaIntent()
    object UserPanelClick : PersonalAreaIntent()
}

class PersonalAreaFragment : BaseFragment(),
    BaseView<PersonalAreaIntent, PersonalAreaState> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var profileRouter: PersonalAreaRouter

    private lateinit var binding: FragmentPersonalAreaBinding

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[PersonalAreaViewModel::class.java]
    }

    private val menuController = PersonalAreaMenuController()

    override fun initViews() {
        binding.run {
            bottomNavView.apply {
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

            recyclerView.apply {
                adapter = menuController.adapter
                layoutManager = LinearLayoutManager(context)
            }
        }
    }

    override fun injectUserComponent(userComponent: UserComponent) =
        userComponent.plus(PersonalAreaModule(this)).inject(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentPersonalAreaBinding
        .inflate(inflater, container, false)
        .let {
            binding = it
            it.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        viewModel.also {
            it.router = profileRouter
            it.connectInto(this)
        }
    }

    override fun intents(): Flow<PersonalAreaIntent> = merge(
        flowOf(PersonalAreaIntent.InitData),
        binding.userIcon.clicks().map { PersonalAreaIntent.UserPanelClick },
        menuController.clicks.map { PersonalAreaIntent.MenuItemClick(it) },
    )

    override fun render(state: PersonalAreaState) {
        menuController.items = state.menuItems
        menuController.requestModelBuild()
        binding.toolbar.title = state.user
            ?.let { "${it.name} ${it.surname}" } ?: ""
    }
}
