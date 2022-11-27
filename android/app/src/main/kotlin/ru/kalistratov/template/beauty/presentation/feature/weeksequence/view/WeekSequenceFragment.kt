package ru.kalistratov.template.beauty.presentation.feature.weeksequence.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.di.UserComponent
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelFactory
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.presentation.extension.find
import ru.kalistratov.template.beauty.presentation.extension.showBottomSheet
import ru.kalistratov.template.beauty.presentation.feature.weeksequence.WeekSequenceRouter
import ru.kalistratov.template.beauty.presentation.feature.weeksequence.WeekSequenceState
import ru.kalistratov.template.beauty.presentation.feature.weeksequence.WeekSequenceViewModel
import ru.kalistratov.template.beauty.presentation.feature.weeksequence.di.WeekSequenceModule
import ru.kalistratov.template.beauty.presentation.view.weeksequence.EditSequenceDayBottomSheet
import ru.kalistratov.template.beauty.presentation.view.weeksequence.WeekSequenceView
import javax.inject.Inject

sealed class WeekSequenceIntent : BaseIntent {
    data class WorkDaySequenceClick(val dayIndex: Int) : WeekSequenceIntent()

    data class WorkDayBottomSheetClick(
        val intent: EditSequenceDayBottomSheet.ClickIntent
    ) : WeekSequenceIntent()

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

    override fun findViews() {
        weekSequenceView = find(R.id.week_sequence_view)
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
        setAppBar(getString(R.string.week_sequence))
        with(viewModel) {
            viewModelScope.launch {
                stateUpdates()
                    .collect(::render)
            }.addTo(jobComposite)
            router = profileRouter
            processIntent(intents())
        }
    }

    override fun onAppBarBackPressed() = profileRouter.back()

    override fun intents(): Flow<WeekSequenceIntent> = merge(
        flowOf(WeekSequenceIntent.InitData),
        weekSequenceView.clicks().map { WeekSequenceIntent.WorkDaySequenceClick(it) },
        EditSequenceDayBottomSheet.clicks.map {
            loge("asdasd - $it")
            WeekSequenceIntent.WorkDayBottomSheetClick(it) }
    )

    override fun render(state: WeekSequenceState) {
        weekSequenceView.setLoading(state.weekSequenceLoading)
        weekSequenceView.requestModelBuild(state.weekSequence)
        if (state.openEditWorkDaySequenceBottomSheet)
            state.editWorkdaySequence?.let {
                showBottomSheet(EditSequenceDayBottomSheet(state.editWorkdaySequence))
            }
    }
}
