package ru.kalistratov.template.beauty.presentation.feature.editworkdaywindows.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.di.UserComponent
import ru.kalistratov.template.beauty.domain.di.ViewModelFactory
import ru.kalistratov.template.beauty.domain.extension.toClockFormat
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.presentation.extension.clicks
import ru.kalistratov.template.beauty.presentation.extension.find
import ru.kalistratov.template.beauty.presentation.extension.showBottomSheet
import ru.kalistratov.template.beauty.presentation.feature.editworkdaywindows.EditWorkdayWindowsState
import ru.kalistratov.template.beauty.presentation.feature.editworkdaywindows.EditWorkdayWindowsViewModel
import ru.kalistratov.template.beauty.presentation.feature.editworkdaywindows.di.EditWorkdayWindowsModule
import ru.kalistratov.template.beauty.presentation.view.bottomsheet.TimePickerSpinnerBottomSheet
import ru.kalistratov.template.beauty.presentation.view.bottomsheet.TimePickerSpinnerBottomSheet.Companion.FROM_TIME_TAG
import ru.kalistratov.template.beauty.presentation.view.bottomsheet.TimePickerSpinnerBottomSheet.Companion.TO_TIME_TAG
import ru.kalistratov.template.beauty.presentation.view.workdaywindows.WorkdayWindowsView

sealed class EditWorkdayWindowsIntent : BaseIntent {
    data class InitData(val daySequenceId: Int) : EditWorkdayWindowsIntent()
    data class TimeSelected(
        val result: TimePickerSpinnerBottomSheet.TimePickerResult
    ) : EditWorkdayWindowsIntent()

    object SaveWindowClick : EditWorkdayWindowsIntent()
}

class EditWorkdayWindowsFragment :
    BaseFragment(),
    BaseView<EditWorkdayWindowsIntent, EditWorkdayWindowsState> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[EditWorkdayWindowsViewModel::class.java]
    }

    private val args: EditWorkdayWindowsFragmentArgs by navArgs()

    private var workdayWindows: WorkdayWindowsView? = null
    private var topicTextView: TextView? = null
    private var toTimeTextView: TextView? = null
    private var fromTimeTextView: TextView? = null
    private var workTimeTextView: TextView? = null
    private var saveWindowButton: Button? = null
    private var addWindowPanel: View? = null

    override fun injectUserComponent(userComponent: UserComponent) =
        userComponent.plus(EditWorkdayWindowsModule(this)).inject(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(
        R.layout.fragment_edit_workday_windows,
        container,
        false
    )

    override fun findViews() {
        topicTextView = find(R.id.topic_text_view)
        workdayWindows = find(R.id.workday_windows_view)
        toTimeTextView = find(R.id.to_text_view)
        addWindowPanel = find(R.id.create_window_card)
        workTimeTextView = find(R.id.workday_time_text_view)
        fromTimeTextView = find(R.id.from_text_view)
        saveWindowButton = find(R.id.saving_button)

        find<ImageView>(R.id.add_window_btn)
            .setOnClickListener { addWindowPanel?.isVisible = true }

        find<ImageView>(R.id.close_btn)
            .setOnClickListener { addWindowPanel?.isVisible = false }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toTimeTextView?.setOnClickListener {
            showBottomSheet(TimePickerSpinnerBottomSheet(TO_TIME_TAG))
        }

        fromTimeTextView?.setOnClickListener {
            showBottomSheet(TimePickerSpinnerBottomSheet(FROM_TIME_TAG))
        }

        addWindowPanel?.isVisible = false

        with(viewModel) {
            viewModelScope.launch {
                stateUpdates()
                    .collect(::render)
            }.addTo(jobComposite)
            processIntent(intents())
        }
    }

    override fun intents(): Flow<EditWorkdayWindowsIntent> = merge(
        flowOf(EditWorkdayWindowsIntent.InitData(args.daySequence)),
        TimePickerSpinnerBottomSheet.savingTime().map { EditWorkdayWindowsIntent.TimeSelected(it) },
        saveWindowButton?.clicks()?.map { EditWorkdayWindowsIntent.SaveWindowClick } ?: emptyFlow()
    )

    override fun render(state: EditWorkdayWindowsState) {
        toTimeTextView?.text = state.toTime.toClockFormat()
        fromTimeTextView?.text = state.fromTime.toClockFormat()

        with(state.workdaySequence) {
            workTimeTextView?.text = "$startAt - $finishAt"
            topicTextView?.text = requireContext().getString(day.tittleResId)
        }

        saveWindowButton?.isEnabled = state.canAddWindow

        workdayWindows?.updateWorkdayWindows(state.windows)
    }
}
