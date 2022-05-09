package ru.kalistratov.template.beauty.presentation.feature.editworkdaywindows.view

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.FragmentEditWorkdayWindowsBinding
import ru.kalistratov.template.beauty.domain.di.UserComponent
import ru.kalistratov.template.beauty.domain.di.ViewModelFactory
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.WorkdayWindow
import ru.kalistratov.template.beauty.domain.extension.toClockFormat
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.entity.TimeRange
import ru.kalistratov.template.beauty.presentation.extension.clicks
import ru.kalistratov.template.beauty.presentation.extension.find
import ru.kalistratov.template.beauty.presentation.feature.editworkdaywindows.EditWorkdayWindowsState
import ru.kalistratov.template.beauty.presentation.feature.editworkdaywindows.EditWorkdayWindowsViewModel
import ru.kalistratov.template.beauty.presentation.feature.editworkdaywindows.di.EditWorkdayWindowsModule
import ru.kalistratov.template.beauty.presentation.view.time.WorkdayWindowDialog
import ru.kalistratov.template.beauty.presentation.view.time.WorkdayWindowDialog.Callback

sealed class EditWorkdayWindowsIntent : BaseIntent {
    data class InitData(val daySequenceId: Int) : EditWorkdayWindowsIntent()
    data class AddWindow(val window: WorkdayWindow) : EditWorkdayWindowsIntent()
    data class UpdateWindow(val window: WorkdayWindow) : EditWorkdayWindowsIntent()
    data class WindowClick(val id: Id) : EditWorkdayWindowsIntent()

    object AddWindowDialogClick : EditWorkdayWindowsIntent()
}

class EditWorkdayWindowsFragment :
    BaseFragment(R.layout.fragment_edit_workday_windows),
    BaseView<EditWorkdayWindowsIntent, EditWorkdayWindowsState> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[EditWorkdayWindowsViewModel::class.java]
    }

    private val args: EditWorkdayWindowsFragmentArgs by navArgs()

    private val binding: FragmentEditWorkdayWindowsBinding by viewBinding()

    private var topicTextView: TextView? = null

    override fun injectUserComponent(userComponent: UserComponent) =
        userComponent.plus(EditWorkdayWindowsModule(this)).inject(this)

    override fun findViews() {
        topicTextView = find(R.id.topic_text_view)
    }

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

    override fun intents(): Flow<EditWorkdayWindowsIntent> = merge(
        flowOf(EditWorkdayWindowsIntent.InitData(args.daySequence)),
        binding.addWindowButton.clicks().map { EditWorkdayWindowsIntent.AddWindowDialogClick },
        WorkdayWindowDialog.saves.map {
            when (it) {
                is Callback.Add -> EditWorkdayWindowsIntent.AddWindow(it.window)
                is Callback.Update -> EditWorkdayWindowsIntent.UpdateWindow(it.window)
            }
        },
        binding.workdayWindows.clicks().map { EditWorkdayWindowsIntent.WindowClick(it) }
    )

    override fun render(state: EditWorkdayWindowsState) {

        with(state) {
            if (showAddWindowDialog) WorkdayWindowDialog(
                selectedWindow,
                windows,
                workdaySequence.let { TimeRange(it.startAt, it.finishAt) },
                requireContext()
            ).show()
        }

        with(state.workdaySequence) {
            val formatTime = "${startAt.toClockFormat()} - ${finishAt.toClockFormat()}"
            binding.workdayTimeTextView.text = formatTime
            topicTextView?.text = requireContext().getString(day.tittleResId)
        }

        binding.workdayWindows.updateWorkdayWindows(state.windows)
    }
}
