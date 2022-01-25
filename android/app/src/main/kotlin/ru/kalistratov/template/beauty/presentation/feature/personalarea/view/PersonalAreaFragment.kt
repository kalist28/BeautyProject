package ru.kalistratov.template.beauty.presentation.feature.personalarea.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.di.UserComponent
import ru.kalistratov.template.beauty.domain.di.ViewModelFactory
import ru.kalistratov.template.beauty.domain.entity.WorkDaySequence
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.presentation.extension.find
import ru.kalistratov.template.beauty.presentation.extension.showBottomSheet
import ru.kalistratov.template.beauty.presentation.feature.personalarea.PersonalAreaRouter
import ru.kalistratov.template.beauty.presentation.feature.personalarea.PersonalAreaState
import ru.kalistratov.template.beauty.presentation.feature.personalarea.PersonalAreaViewModel
import ru.kalistratov.template.beauty.presentation.feature.personalarea.di.PersonalAreaModule
import ru.kalistratov.template.beauty.presentation.view.bottomsheet.EditWorkDaySequenceBottomSheet
import ru.kalistratov.template.beauty.presentation.view.workdaysequence.WeekSequenceView
import javax.inject.Inject

sealed class PersonalAreaIntent : BaseIntent {
    data class WorkDaySequenceClick(val dayIndex: Int) : PersonalAreaIntent()
    data class UpdateWorkDaySequence(val day: WorkDaySequence) : PersonalAreaIntent()
    object InitData : PersonalAreaIntent()
}

class PersonalAreaFragment : BaseFragment(), BaseView<PersonalAreaIntent, PersonalAreaState> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var profileRouter: PersonalAreaRouter

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[PersonalAreaViewModel::class.java]
    }

    lateinit var weekSequenceView: WeekSequenceView

    override fun findViews() {
        weekSequenceView = find(R.id.week_sequence_view)
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
            viewModelScope.launch {
                stateUpdates()
                    .collect(::render)
            }.addTo(jobComposite)
            processIntent(intents())
        }
    }

    override fun intents(): Flow<PersonalAreaIntent> = merge(
        flowOf(PersonalAreaIntent.InitData),
        weekSequenceView.clicks().map { PersonalAreaIntent.WorkDaySequenceClick(it) },
        EditWorkDaySequenceBottomSheet.savingDay()
            .map { PersonalAreaIntent.UpdateWorkDaySequence(it) }
    )

    override fun render(state: PersonalAreaState) {
        weekSequenceView.setLoading(state.weekSequenceLoading)
        weekSequenceView.requestModelBuild(state.weekSequence)

        if (state.openEditWorkDaySequenceBottomSheet)
            EditWorkDaySequenceBottomSheet().let {
                it.workDaySequence = state.editWorkDaySequence
                showBottomSheet(it)
            }
    }
}
