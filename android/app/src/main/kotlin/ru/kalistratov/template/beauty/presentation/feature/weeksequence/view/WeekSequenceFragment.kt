package ru.kalistratov.template.beauty.presentation.feature.weeksequence.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.di.UserComponent
import ru.kalistratov.template.beauty.domain.di.ViewModelFactory
import ru.kalistratov.template.beauty.domain.entity.WorkdaySequence
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.extensions.log
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.presentation.extension.clicks
import ru.kalistratov.template.beauty.presentation.extension.find
import ru.kalistratov.template.beauty.presentation.extension.showBottomSheet
import ru.kalistratov.template.beauty.presentation.feature.weeksequence.WeekSequenceRouter
import ru.kalistratov.template.beauty.presentation.feature.weeksequence.WeekSequenceState
import ru.kalistratov.template.beauty.presentation.feature.weeksequence.WeekSequenceViewModel
import ru.kalistratov.template.beauty.presentation.feature.weeksequence.di.WeekSequenceModule
import ru.kalistratov.template.beauty.presentation.view.bottomsheet.EditWorkDaySequenceBottomSheet
import ru.kalistratov.template.beauty.presentation.view.weeksequence.WeekSequenceView

sealed class WeekSequenceIntent : BaseIntent {
    data class WorkDaySequenceClick(val dayIndex: Int) : WeekSequenceIntent()
    data class UpdateWorkDaySequence(val day: WorkdaySequence) : WeekSequenceIntent()

    data class WorkDayBottomSheetClick(
        val intent: EditWorkDaySequenceBottomSheet.ClickIntent
    ) : WeekSequenceIntent()

    object BackPressed : WeekSequenceIntent()
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

    lateinit var weekSequenceView: WeekSequenceView
    lateinit var backButton: View

    override fun findViews() {
        find<TextView>(R.id.topic_text_view).text = "Рабочая неделя"
        weekSequenceView = find(R.id.week_sequence_view)
        backButton = find(R.id.back_button)
    }

    override fun injectUserComponent(userComponent: UserComponent) =
        userComponent.plus(WeekSequenceModule(this)).inject(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_weeksequence, container, false)

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

    override fun intents(): Flow<WeekSequenceIntent> = merge(
        flowOf(WeekSequenceIntent.InitData),
        backButton.clicks().map { WeekSequenceIntent.BackPressed },
        weekSequenceView.clicks().map { WeekSequenceIntent.WorkDaySequenceClick(it) },
        EditWorkDaySequenceBottomSheet.savingDay()
            .map { WeekSequenceIntent.UpdateWorkDaySequence(it) },
        EditWorkDaySequenceBottomSheet.clicks()
            .map { WeekSequenceIntent.WorkDayBottomSheetClick(it) }
    )

    override fun render(state: WeekSequenceState) {
        weekSequenceView.setLoading(state.weekSequenceLoading)
        weekSequenceView.requestModelBuild(state.weekSequence)

        if (state.openEditWorkDaySequenceBottomSheet)
            state.editWorkdaySequence?.let {
                showBottomSheet(EditWorkDaySequenceBottomSheet(state.editWorkdaySequence))
            }
    }
}
