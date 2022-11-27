package ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows.view

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.FragmentEditSequenceDayWindowsBinding
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.SequenceDayWindow
import ru.kalistratov.template.beauty.domain.entity.WeekDay
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.infrastructure.di.UserComponent
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelFactory
import ru.kalistratov.template.beauty.infrastructure.entity.TimeRange
import ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows.EditWorkdayWindowsRouter
import ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows.EditWorkdayWindowsState
import ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows.EditWorkdayWindowsViewModel
import ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows.di.EditSequenceDayWindowsModule
import ru.kalistratov.template.beauty.presentation.view.LoadingAlertDialog
import ru.kalistratov.template.beauty.presentation.view.time.SequenceDayWindowCreatorDialog
import ru.kalistratov.template.beauty.presentation.view.time.SequenceDayWindowCreatorDialog.Callback
import ru.kalistratov.template.beauty.presentation.view.workdaywindows.IdSelector
import ru.kalistratov.template.beauty.presentation.view.workdaywindows.WorkdayWindowsView
import javax.inject.Inject

sealed class EditSequenceDayWindowsIntent : BaseIntent {
    data class InitData(val dayNumber: Int) : EditSequenceDayWindowsIntent()
    data class AddWindow(val window: SequenceDayWindow) : EditSequenceDayWindowsIntent()
    data class UpdateWindow(val window: SequenceDayWindow) : EditSequenceDayWindowsIntent()
    data class WindowClick(val id: Id) : EditSequenceDayWindowsIntent()
    data class SelectedWindowsUpdated(val list: List<Id>) : EditSequenceDayWindowsIntent()

    data class WindowListStateChanged(
        val state: IdSelector.State
    ) : EditSequenceDayWindowsIntent()

    object AddWindowDialogClick : EditSequenceDayWindowsIntent()
    object RemoveWindows : EditSequenceDayWindowsIntent()
}

class EditSequenceDayWindowsFragment : BaseFragment(),
    BaseView<EditSequenceDayWindowsIntent, EditWorkdayWindowsState> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var editWindowsRouter: EditWorkdayWindowsRouter

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[EditWorkdayWindowsViewModel::class.java]
    }

    private val args: EditSequenceDayWindowsFragmentArgs by navArgs()

    private val defaultTitle by lazy { getString(WeekDay.fromIndex(args.dayNumber)!!.tittleResId) }

    private val binding: FragmentEditSequenceDayWindowsBinding by viewBinding(CreateMethod.INFLATE)
    private val loadingDialog by lazy { LoadingAlertDialog(requireContext()) }

    private val addWindowClicks = mutableSharedFlow<Unit>()
    private val removeWindowsClicks = mutableSharedFlow<Unit>()

    private var selectedWindowsCount = 0

    override fun injectUserComponent(userComponent: UserComponent) =
        userComponent.plus(EditSequenceDayWindowsModule(this)).inject(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAppBar(defaultTitle)

        with(viewModel) {
            viewModelScope.launch {
                stateUpdates()
                    .collect(::render)
            }.addTo(jobComposite)
            processIntent(intents())
        }
    }

    override fun onAppBarBackPressed() = with(binding.workdayWindows) {
        when (selector.state == IdSelector.State.SELECTOR) {
            true -> cancelSelection()
            false -> editWindowsRouter.back()
        }
    }

    override fun appBarMenu() = R.menu.edit_sequence_day_windows_menu

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val listIsSelector = binding.workdayWindows.selector.state == IdSelector.State.SELECTOR
        menu.findItem(R.id.add_window)?.isVisible = !listIsSelector
        menu.findItem(R.id.remove_windows)?.isVisible = listIsSelector
    }

    override fun onAppBarMenuItemClick(item: MenuItem) = when (item.itemId) {
        R.id.add_window -> {
            addWindowClicks.tryEmit(Unit)
            true
        }
        R.id.remove_windows -> {
            if (selectedWindowsCount > 0) showRemoveWindowsAlertDialog()
            true
        }
        else -> super.onAppBarMenuItemClick(item)
    }

    override fun intents(): Flow<EditSequenceDayWindowsIntent> = merge(
        flowOf(EditSequenceDayWindowsIntent.InitData(args.dayNumber)),
        addWindowClicks.map { EditSequenceDayWindowsIntent.AddWindowDialogClick },
        removeWindowsClicks.map { EditSequenceDayWindowsIntent.RemoveWindows },
        SequenceDayWindowCreatorDialog.saves.map {
            when (it) {
                is Callback.Add -> EditSequenceDayWindowsIntent.AddWindow(it.window)
                is Callback.Update -> EditSequenceDayWindowsIntent.UpdateWindow(it.window)
            }
        },
        binding.workdayWindows.clicks()
            .map { EditSequenceDayWindowsIntent.WindowClick(it) },
        binding.workdayWindows.stateChanges()
            .map { EditSequenceDayWindowsIntent.WindowListStateChanged(it) },
        binding.workdayWindows.selectChanges()
            .map { EditSequenceDayWindowsIntent.SelectedWindowsUpdated(it) }
    )

    override fun render(state: EditWorkdayWindowsState) = with(state) {
        selectedWindowsCount = selectedWindows.size

        if (showAddWindowDialog) SequenceDayWindowCreatorDialog(
            selectedWindow,
            day.windows,
            day.let { TimeRange(it.startAt, it.finishAt) },
            requireContext()
        ).show()

        when (state.loading) {
            true -> loadingDialog.show()
            false -> loadingDialog.hide()
        }

        val listIsSelector = windowsListState == IdSelector.State.SELECTOR

        when (listIsSelector) {
            true -> getString(R.string.selected_count, selectedWindowsCount)
            false -> defaultTitle
        }.let(::updateAppBarTitle)

        if (needInvalidateOptionMenu) requireActivity()
            .invalidateOptionsMenu()

        binding.toolbar.subtitle = if (listIsSelector) null else day.toContentTimeRange()
        binding.workdayWindows.updateWorkdayWindows(day.windows)
    }

    private fun showRemoveWindowsAlertDialog() {
        val windowsText = resources.getQuantityString(
            R.plurals.day_windows_plurals,
            selectedWindowsCount,
            selectedWindowsCount
        )
        val message = getString(R.string.delete_windows_warning, windowsText)

        MaterialAlertDialogBuilder(
            requireContext(),
            R.style.BaseDialog
        ).setIcon(R.drawable.ic_delete_forever)
            .setTitle(R.string.warning)
            .setMessage(message)
            .setNegativeButton(R.string.cancel) { d, _ -> }
            .setPositiveButton(R.string.confirm) { d, _ ->
                removeWindowsClicks.tryEmit(Unit)
                binding.workdayWindows.cancelSelection()
            }
            .show()
    }
}
