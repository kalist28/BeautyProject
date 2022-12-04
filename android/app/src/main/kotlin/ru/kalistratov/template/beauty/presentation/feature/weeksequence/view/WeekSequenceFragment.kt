package ru.kalistratov.template.beauty.presentation.feature.weeksequence.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.soywiz.klock.Time
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.FragmentWeeksequenceBinding
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.TimeSource
import ru.kalistratov.template.beauty.domain.entity.TimeSourceType
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.infrastructure.di.UserComponent
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelFactory
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.presentation.feature.weeksequence.WeekSequenceRouter
import ru.kalistratov.template.beauty.presentation.feature.weeksequence.WeekSequenceState
import ru.kalistratov.template.beauty.presentation.feature.weeksequence.WeekSequenceViewModel
import ru.kalistratov.template.beauty.presentation.feature.weeksequence.di.WeekSequenceModule
import ru.kalistratov.template.beauty.presentation.view.LoadingAlertDialog
import ru.kalistratov.template.beauty.presentation.view.weeksequence.EditSequenceDayBottomSheet
import javax.inject.Inject

sealed class WeekSequenceIntent : BaseIntent {
    data class DayClicked(val dayIndex: Int) : WeekSequenceIntent()
    data class DayTimeClicked(val type: TimeSourceType) : WeekSequenceIntent()
    data class DayHolidayChanged(val isHoliday: Boolean) : WeekSequenceIntent()
    data class TimePicked(val source: TimeSource) : WeekSequenceIntent()
    data class EditWindows(val day: Int) : WeekSequenceIntent()

    object UpdateDay : WeekSequenceIntent()
    object InitData : WeekSequenceIntent()
}

class WeekSequenceFragment : BaseFragment(), BaseView<WeekSequenceIntent, WeekSequenceState> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var profileRouter: WeekSequenceRouter

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[WeekSequenceViewModel::class.java]
    }

    private lateinit var binding: FragmentWeeksequenceBinding

    private val controller by lazy { WeekSequenceController(resources) }

    private val loadingDialog by lazy { LoadingAlertDialog(requireContext()) }

    private val timePicked = mutableSharedFlow<TimeSource>()
    private val updateDayClicks = mutableSharedFlow<Unit>()

    override fun injectUserComponent(userComponent: UserComponent) =
        userComponent.plus(WeekSequenceModule(this)).inject(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentWeeksequenceBinding
        .inflate(layoutInflater, container, false)
        .let {
            binding = it
            it.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAppBar(getString(R.string.week_sequence))

        binding.recycler.apply {
            adapter = controller.adapter
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(requireContext())
        }
        with(viewModel) {
            viewModelScope.launch {
                stateUpdates()
                    .collect(::render)
            }.addTo(jobComposite)
            router = profileRouter
            processIntent(intents())
        }
    }

    override fun onAppBarBackPressed() {
        when (controller.selectedDay == null) {
            true -> profileRouter.back()
            false -> updateDayClicks.tryEmit(Unit)
        }
    }

    override fun intents(): Flow<WeekSequenceIntent> = merge(
        flowOf(WeekSequenceIntent.InitData),
        timePicked.map(WeekSequenceIntent::TimePicked),
        updateDayClicks.map { WeekSequenceIntent.UpdateDay },
        controller.clicks.map(WeekSequenceIntent::DayClicked),
        controller.dayTimeClicks.map(WeekSequenceIntent::DayTimeClicked),
        controller.isHolidayClicks.map(WeekSequenceIntent::DayHolidayChanged),
        controller.editWindowsClicks.map(WeekSequenceIntent::EditWindows),
    )

    override fun render(state: WeekSequenceState) {
        loadingDialog.show(state.loading)
        controller.sequenceWeek = state.weekSequence
        controller.selectedDay = state.selectedDay
        controller.requestModelBuild()

        state.timeForShowTimePicker?.let(::showTimePicker)
    }

    private fun showTimePicker(source: TimeSource) = MaterialTimePicker
        .Builder()
        .setHour(source.time.hour)
        .setMinute(source.time.minute)
        .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
        .setTimeFormat(TimeFormat.CLOCK_24H)
        .build()
        .apply {
            addOnPositiveButtonClickListener {
                loge(
                    timePicked.tryEmit(
                        source.copy(
                            time = Time(
                                hour = hour,
                                minute = minute
                            )
                        )
                    )
                )
            }

        }
        .show(childFragmentManager, "DayTimePicker")
}
