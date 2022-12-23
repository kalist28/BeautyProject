package ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows.view

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.soywiz.klock.Time
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.FragmentEditSequenceDayWindowsBinding
import ru.kalistratov.template.beauty.domain.entity.*
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.infrastructure.di.UserComponent
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelFactory
import ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows.EditWorkdayWindowsRouter
import ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows.EditWorkdayWindowsState
import ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows.EditWorkdayWindowsViewModel
import ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows.di.EditSequenceDayWindowsModule
import ru.kalistratov.template.beauty.presentation.view.LoadingAlertDialog
import javax.inject.Inject

enum class State {
    LIST,
    EDIT,
    CREATE,
    SELECTOR
}

sealed class EditSequenceDayWindowsIntent : BaseIntent {
    data class InitData(val dayNumber: Int) : EditSequenceDayWindowsIntent()
    data class TimePicked(val source: TimeSource) : EditSequenceDayWindowsIntent()
    data class TimeClicked(val type: TimeSourceType) : EditSequenceDayWindowsIntent()
    data class WindowClick(val id: Id) : EditSequenceDayWindowsIntent()
    data class SelectedWindowsUpdated(val list: List<Id>) : EditSequenceDayWindowsIntent()
    data class WindowListStateChanged(val state: State) : EditSequenceDayWindowsIntent()

    object AddWindowDialogClick : EditSequenceDayWindowsIntent()
    object RemoveWindows : EditSequenceDayWindowsIntent()
    object RemoveWindow : EditSequenceDayWindowsIntent()
    object CreateWindow : EditSequenceDayWindowsIntent()
    object PushWindow : EditSequenceDayWindowsIntent()
    object ExitEditor : EditSequenceDayWindowsIntent()
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

    private val controller by lazy { EditSequenceDayWindowsController() }
    private val loadingDialog by lazy { LoadingAlertDialog(requireContext()) }

    private val timePicked = mutableSharedFlow<TimeSource>()
    private val exitEditorClicks = mutableSharedFlow<Unit>()
    private val createWindowClicks = mutableSharedFlow<Unit>()
    private val removeIntentsFlow = mutableSharedFlow<EditSequenceDayWindowsIntent>()

    private var selectedWindowsCount = 0

    private var state = State.LIST

    override fun injectUserComponent(userComponent: UserComponent) = userComponent
        .plus(EditSequenceDayWindowsModule(this))
        .inject(this)

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

        with(binding.recycler) {
            EpoxyVisibilityTracker().attach(this)
            adapter = controller.adapter
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(context)
            overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }

        with(viewModel) {
            viewModelScope.launch {
                stateUpdates()
                    .collect(::render)
            }.addTo(jobComposite)
            processIntent(intents())
        }
    }

    override fun onAppBarBackPressed(): Unit = with(controller) {
        when (state) {
            State.CREATE, State.EDIT -> exitEditorClicks.tryEmit(Unit)
            State.SELECTOR -> cancelSelection()
            State.LIST -> editWindowsRouter.back()
        }
    }

    override fun appBarMenu() = R.menu.edit_sequence_day_windows_menu

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val addWindow = menu.findItem(R.id.add_window)
        val remove = menu.findItem(R.id.remove_windows)
        when (state) {
            State.SELECTOR -> {
                remove?.isVisible = true
                addWindow?.isVisible = false
            }
            State.LIST -> {
                remove?.isVisible = false
                addWindow?.isVisible = true
            }
            State.EDIT -> {
                remove?.isVisible = true
                addWindow?.isVisible = false
            }
            State.CREATE -> {
                remove?.isVisible = false
                addWindow?.isVisible = false
            }
        }
    }

    override fun onAppBarMenuItemClick(item: MenuItem) = when (item.itemId) {
        R.id.add_window -> createWindowClicks.tryEmit(Unit)
        R.id.remove_windows -> {
            when (state) {
                State.SELECTOR -> if (selectedWindowsCount > 0) showRemoveWindowsAlertDialog()
                State.EDIT -> removeIntentsFlow.tryEmit(EditSequenceDayWindowsIntent.RemoveWindow)
                else -> Unit
            }
            true
        }
        else -> super.onAppBarMenuItemClick(item)
    }

    override fun intents(): Flow<EditSequenceDayWindowsIntent> = merge(
        flowOf(EditSequenceDayWindowsIntent.InitData(args.dayNumber)),
        removeIntentsFlow,
        timePicked.map(EditSequenceDayWindowsIntent::TimePicked),
        exitEditorClicks.map { EditSequenceDayWindowsIntent.ExitEditor },
        createWindowClicks.map { EditSequenceDayWindowsIntent.CreateWindow },
        controller.clicks().map(EditSequenceDayWindowsIntent::WindowClick),
        controller.saveClicks().map { EditSequenceDayWindowsIntent.PushWindow },
        controller.timeClicks().map(EditSequenceDayWindowsIntent::TimeClicked),
        controller.stateChanges().map(EditSequenceDayWindowsIntent::WindowListStateChanged),
        controller.selectChanges().map(EditSequenceDayWindowsIntent::SelectedWindowsUpdated)
    )

    override fun render(state: EditWorkdayWindowsState): Unit = with(state) {
        this@EditSequenceDayWindowsFragment.state = state.displayState
        selectedWindowsCount = selectedWindows.size

        when (state.loading) {
            true -> loadingDialog.show()
            false -> loadingDialog.hide()
        }

        timeForShowTimePicker?.let(::showTimePicker)

        val listIsSelector = displayState == State.SELECTOR

        when (listIsSelector) {
            true -> getString(R.string.selected_count, selectedWindowsCount)
            false -> defaultTitle
        }.let(::updateAppBarTitle)

        if (needInvalidateOptionMenu) requireActivity()
            .invalidateOptionsMenu()

        binding.toolbar.subtitle = if (listIsSelector) null else day.toContentTimeRange()
        controller.also {
            it.day = day
            it.state = displayState
            it.windows = day.windows
            it.selectedWindow = selectedWindow
            it.requestModelBuild()
        }
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
            .setNegativeButton(R.string.cancel) { _, _ -> }
            .setPositiveButton(R.string.confirm) { _, _ ->
                removeIntentsFlow.tryEmit(EditSequenceDayWindowsIntent.RemoveWindows)
                controller.cancelSelection()
            }
            .show()
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
                timePicked.tryEmit(
                    source.copy(
                        time = Time(
                            hour = hour,
                            minute = minute
                        )
                    )
                )
            }
        }
        .show(childFragmentManager, "DayTimePicker")
}
