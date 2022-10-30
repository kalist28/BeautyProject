package ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.databinding.FragmentEditSequenceDayWindowsBinding
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.SequenceDayWindow
import ru.kalistratov.template.beauty.domain.extension.toClockFormat
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.di.UserComponent
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelFactory
import ru.kalistratov.template.beauty.infrastructure.entity.TimeRange
import ru.kalistratov.template.beauty.presentation.extension.clicks
import ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows.EditWorkdayWindowsState
import ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows.EditWorkdayWindowsViewModel
import ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows.di.EditSequenceDayWindowsModule
import ru.kalistratov.template.beauty.presentation.view.time.SequenceDayWindowCreatorDialog
import ru.kalistratov.template.beauty.presentation.view.time.SequenceDayWindowCreatorDialog.Callback
import javax.inject.Inject

sealed class EditSequenceDayWindowsIntent : BaseIntent {
    data class InitData(val dayNumber: Int) : EditSequenceDayWindowsIntent()
    data class AddWindow(val window: SequenceDayWindow) : EditSequenceDayWindowsIntent()
    data class UpdateWindow(val window: SequenceDayWindow) : EditSequenceDayWindowsIntent()
    data class WindowClick(val id: Id) : EditSequenceDayWindowsIntent()

    object BackPressed : EditSequenceDayWindowsIntent()
    object AddWindowDialogClick : EditSequenceDayWindowsIntent()
}

class EditSequenceDayWindowsFragment : BaseFragment(),
    BaseView<EditSequenceDayWindowsIntent, EditWorkdayWindowsState> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[EditWorkdayWindowsViewModel::class.java]
    }

    private val args: EditSequenceDayWindowsFragmentArgs by navArgs()

    private val binding: FragmentEditSequenceDayWindowsBinding by viewBinding(createMethod = CreateMethod.INFLATE)

    override fun injectUserComponent(userComponent: UserComponent) =
        userComponent.plus(EditSequenceDayWindowsModule(this)).inject(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

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

    override fun intents(): Flow<EditSequenceDayWindowsIntent> = merge(
        flowOf(EditSequenceDayWindowsIntent.InitData(args.dayNumber)),
        binding.upBar.backButton.clicks().map { EditSequenceDayWindowsIntent.BackPressed },
        binding.addWindowButton.clicks().map { EditSequenceDayWindowsIntent.AddWindowDialogClick },
        SequenceDayWindowCreatorDialog.saves.map {
            when (it) {
                is Callback.Add -> EditSequenceDayWindowsIntent.AddWindow(it.window)
                is Callback.Update -> EditSequenceDayWindowsIntent.UpdateWindow(it.window)
            }
        },
        binding.workdayWindows.clicks().map { EditSequenceDayWindowsIntent.WindowClick(it) }
    )

    override fun render(state: EditWorkdayWindowsState) {

        with(state) {
            if (showAddWindowDialog) SequenceDayWindowCreatorDialog(
                selectedWindow,
                day.windows,
                day.let { TimeRange(it.startAt, it.finishAt) },
                requireContext()
            ).show()
        }

        with(state.day) {
            val formatTime = "${startAt.toClockFormat()} - ${finishAt.toClockFormat()}"
            binding.workdayTimeTextView.text = formatTime
            binding.upBar.topicTextView.text = requireContext().getString(day.tittleResId)
        }

        binding.workdayWindows.updateWorkdayWindows(state.day.windows)
    }
}
