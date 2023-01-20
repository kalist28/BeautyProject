package ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.edit.view

import android.os.Bundle
import android.os.Parcel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.soywiz.klock.DateTime
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.common.DateTimeFormat
import ru.kalistratov.template.beauty.databinding.FragmentReservationEditBinding
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.SequenceWeek
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.base.SingleAction
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.infrastructure.di.UserComponent
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelFactory
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.infrastructure.extensions.weekDay
import ru.kalistratov.template.beauty.presentation.extension.connect
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.edit.EditReservationRouter
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.edit.EditReservationState
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.edit.EditReservationViewModel
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.edit.di.EditReservationModule
import javax.inject.Inject

sealed interface EditReservationIntent : BaseIntent {
    data class DateSelected(val unix: Long) : EditReservationIntent
    data class SequenceDayWindowSelected(val id: Id?) : EditReservationIntent
    object ShowOfferItemPicker : EditReservationIntent
    object ShowDatePicker : EditReservationIntent
    object ShowClientPicker : EditReservationIntent
    object SaveClick : EditReservationIntent
    object InitData : EditReservationIntent
}

sealed interface EditReservationSingleAction : SingleAction {
    data class ShowDatePicker(val week: SequenceWeek) : EditReservationSingleAction
}

class EditReservationFragment : BaseFragment(),
    BaseView<EditReservationIntent, EditReservationState> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var editReservationRouter: EditReservationRouter

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[EditReservationViewModel::class.java]
    }

    private val binding: FragmentReservationEditBinding by viewBinding(CreateMethod.INFLATE)

    private val controller by lazy { EditReservationController(requireContext()) }

    private val intentsMutableFlow = mutableSharedFlow<EditReservationIntent>()

    private val saveClicksMutableFlow = mutableSharedFlow<Unit>()

    private var datePickerIsHide = true

    override fun injectUserComponent(userComponent: UserComponent) =
        userComponent.plus(EditReservationModule(this)).inject(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onAppBarBackPressed() = editReservationRouter.back()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAppBar(R.string.reservation)

        binding.recycler.connect(controller)

        binding.save.setOnClickListener { saveClicksMutableFlow.tryEmit(Unit) }

        with(viewModel) {
            router = editReservationRouter
            connectDialogLoadingDisplay()
            viewModelScope.launch {
                setSingleActionProcessor(::processSingleAction, this)
            }.addTo(jobComposite)
            connectInto(this@EditReservationFragment)
        }
    }

    override fun intents(): Flow<EditReservationIntent> = merge(
        intentsMutableFlow,
        flowOf(EditReservationIntent.InitData),
        saveClicksMutableFlow.map { EditReservationIntent.SaveClick },
        controller.dateClicks().map { EditReservationIntent.ShowDatePicker },
        controller.clientClicks().map { EditReservationIntent.ShowClientPicker },
        controller.offerClicks().map { EditReservationIntent.ShowOfferItemPicker },
        controller.windowClicks().map(EditReservationIntent::SequenceDayWindowSelected)

    ).onEach { loge(it) }

    override fun render(state: EditReservationState) {
        with(controller) {
            date = state.date
            client = state.client
            category = state.offerItemCategory
            offerItem = state.offerItem
            selectedWindow = state.window
            windows = state.freeWindows
            requestModelBuild()
        }

        val isSaveEnable = state.run {
            date != null && window != null && offerItem != null && client != null
        }
        binding.save.apply {
            isClickable = isSaveEnable
            setBackgroundColor(
                requireContext().getColor(
                    if (isSaveEnable) R.color.saveButton else R.color.subtitle
                )
            )
        }

    }

    private fun processSingleAction(action: EditReservationSingleAction) {
        when (action) {
            is EditReservationSingleAction.ShowDatePicker ->
                if (datePickerIsHide) showDatePicker(action.week)
        }
    }

    /*private fun showFreeWindowsDialog(
        windows: List<String>
    ) = MaterialAlertDialogBuilder(requireContext())
        .setTitle(R.string.slc_free_window)
        .setItems(windows.toTypedArray()) { _, index ->
            intentsMutableFlow.tryEmit(
                EditReservationIntent.SequenceDayWindowSelected(index)
            )
        }
        .show()*/

    private fun showDatePicker(week: SequenceWeek) = MaterialDatePicker.Builder
        .datePicker()
        .setCalendarConstraints(getDateValidator(week))
        .build()
        .apply {
            addOnDismissListener { datePickerIsHide = true }
            addOnPositiveButtonClickListener {
                intentsMutableFlow.tryEmit(
                    EditReservationIntent.DateSelected(it)
                )
            }
            datePickerIsHide = false
        }
        .show(childFragmentManager, "date_picker")

    private fun getDateValidator(week: SequenceWeek) = CalendarConstraints.Builder()
        .setValidator(object : CalendarConstraints.DateValidator {
            private val today = DateTime.now()
            override fun describeContents(): Int = 0
            override fun writeToParcel(dest: Parcel, flags: Int) = Unit

            override fun isValid(timestamp: Long): Boolean {
                val dateTime = DateTime(timestamp)
                val isFuture = dateTime >= today.copyDayOfMonth(hours = 0)
                val sequenceDay = week.find { it.day == dateTime.weekDay }
                val isNotHoliday = sequenceDay?.isHoliday?.not() ?: false
                val isExist = sequenceDay?.isNotExist()?.not() ?: false
                return isFuture && isNotHoliday && isExist
            }
        })
        .build()
}