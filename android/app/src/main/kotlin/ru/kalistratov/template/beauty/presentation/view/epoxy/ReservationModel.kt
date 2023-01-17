package ru.kalistratov.template.beauty.presentation.view.epoxy

import android.view.View
import android.view.ViewParent
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.ViewReservationCardBinding
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.Reservation
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.setMargins

data class ReservationModel(
    private val reservation: Reservation,
    private val clickAction: (Id) -> Unit = { },
    private val marginsBundle: MarginsBundle? = null
) : EpoxyModelWithHolder<ReservationModel.Holder>() {

    init {
        id("reservation_${reservation.id}")
    }

    override fun bind(holder: Holder) = with(holder.binding) {
        time.text = reservation.window.toContentTimeRange()
    }

    override fun getDefaultLayout() = R.layout.view_reservation_card

    override fun createNewHolder(parent: ViewParent) = Holder()

    inner class Holder : EpoxyHolder() {
        lateinit var binding: ViewReservationCardBinding
        override fun bindView(itemView: View) {
            binding = ViewReservationCardBinding.bind(itemView).apply {
                marginsBundle?.let(root::setMargins)
                root.setOnClickListener { clickAction.invoke(reservation.id) }
            }
        }

    }
}