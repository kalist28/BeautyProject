package ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.edit.view

import android.os.Bundle
import android.os.Parcel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.soywiz.klock.DateTime
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.FragmentReservationEditBinding
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
import ru.kalistratov.template.beauty.presentation.extension.connect
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.edit.EditReservationRouter
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.edit.EditReservationState
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.edit.EditReservationViewModel
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.edit.di.EditReservationModule
import javax.inject.Inject

sealed interface EditReservationIntent : BaseIntent {
    data class DateSelected(val unix: Long) : EditReservationIntent
    data class SequenceDayWindowSelected(val index: Int) : EditReservationIntent
    object ShowOfferItemPicker : EditReservationIntent
    object ShowSequenceDayWindowPicker : EditReservationIntent
    object ShowDatePicker : EditReservationIntent
    object ShowClientPicker : EditReservationIntent
    object SaveClick : EditReservationIntent
    object InitData : EditReservationIntent
}

sealed interface EditReservationSingleAction : SingleAction {
    data class ShowDatePicker(val week: SequenceWeek) : EditReservationSingleAction
    data class ShowFreeWindowsDialog(val windows: List<String>) : EditReservationSingleAction
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
        setAppBar(R.string.timetable)

        binding.recycler.connect(controller)

        with(viewModel) {
            router = editReservationRouter
            connectDialogLoadingDisplay()
            connectInto(this@EditReservationFragment)
            viewModelScope.launch {
                setSingleActionProcessor(::processSingleAction, this)
            }.addTo(jobComposite)
        }
    }

    override fun intents(): Flow<EditReservationIntent> = merge(
        intentsMutableFlow,
        flowOf(EditReservationIntent.InitData),
        controller.saveClicks().map { EditReservationIntent.SaveClick },
        controller.dateClicks().map { EditReservationIntent.ShowDatePicker },
        controller.clientClicks().map { EditReservationIntent.ShowClientPicker },
        controller.offerClicks().map { EditReservationIntent.ShowOfferItemPicker },
        controller.timeClicks().map { EditReservationIntent.ShowSequenceDayWindowPicker },
    )

    override fun render(state: EditReservationState) {
        with(controller) {
            date = state.date
            client = state.client
            category = state.offerItemCategory
            offerItem = state.offerItem
            sequenceDayWindow = state.window
            if(category != null && offerItem != null) loge("*******")
            requestModelBuild()
        }
    }

    private fun processSingleAction(action: EditReservationSingleAction){
        when (action) {
            is EditReservationSingleAction.ShowDatePicker ->
                showDatePicker(action.week)
            is EditReservationSingleAction.ShowFreeWindowsDialog ->
                showFreeWindowsDialog(action.windows)
        }
    }

    private fun showFreeWindowsDialog(
        windows: List<String>
    ) = MaterialAlertDialogBuilder(requireContext())
        .setTitle(R.string.slc_free_window)
        .setItems(windows.toTypedArray()) { _, index ->
            intentsMutableFlow.tryEmit(
                EditReservationIntent.SequenceDayWindowSelected(index)
            )
        }
        .show()

    private fun showDatePicker(week: SequenceWeek) = MaterialDatePicker.Builder
        .datePicker()
        .setCalendarConstraints(getDateValidator(week))
        .build()
        .apply {
            addOnPositiveButtonClickListener {
                intentsMutableFlow.tryEmit(
                    EditReservationIntent.DateSelected(it)
                )
            }
        }
        .show(childFragmentManager, "date_picker")

    private fun getDateValidator(week: SequenceWeek) = CalendarConstraints.Builder()
        .setValidator(object : CalendarConstraints.DateValidator {
            private val today = DateTime.now()
            override fun describeContents(): Int = 0
            override fun writeToParcel(dest: Parcel, flags: Int) = Unit

            override fun isValid(timestamp: Long): Boolean {
                val dateTime = DateTime(timestamp)
                val isFuture = dateTime.date >= today.date
                val sequenceDay = week.getOrNull(dateTime.dayOfWeek.index1)
                val isNotHoliday = sequenceDay?.isHoliday?.not() ?: false
                val isExist = sequenceDay?.isNotExist()?.not() ?: false
                return isFuture && isNotHoliday && isExist
            }
        })
        .build()
}