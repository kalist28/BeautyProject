package ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.edit.view

import android.content.Context
import android.view.View
import com.airbnb.epoxy.EpoxyController
import com.soywiz.klock.Date
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.common.DateTimeFormat
import ru.kalistratov.template.beauty.domain.entity.Client
import ru.kalistratov.template.beauty.domain.entity.OfferCategory
import ru.kalistratov.template.beauty.domain.entity.OfferItem
import ru.kalistratov.template.beauty.domain.entity.SequenceDayWindow
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.infrastructure.extensions.monthTitle
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.epoxy.DateTimeModel
import ru.kalistratov.template.beauty.presentation.view.epoxy.TextContainerModel
import ru.kalistratov.template.beauty.presentation.view.epoxy.setMargins
import ru.kalistratov.template.beauty.simpleButton

class EditReservationController(
    private val context: Context
) : EpoxyController() {

    private enum class ClickType {
        SAVE,
        DATE,
        TIME,
        OFFER,
        CLIENT,
    }

    private val clicksMutableFlow = mutableSharedFlow<ClickType>()

    var date: Date? = null
    var sequenceDayWindow: SequenceDayWindow? = null
    var category: OfferCategory? = null
    var offerItem: OfferItem? = null
    var client: Client? = null

    fun saveClicks() = clicksMutableFlow.filter { it == ClickType.SAVE }.map { }
    fun dateClicks() = clicksMutableFlow.filter { it == ClickType.DATE }.map { }
    fun timeClicks() = clicksMutableFlow.filter { it == ClickType.TIME }.map { }
    fun offerClicks() = clicksMutableFlow.filter { it == ClickType.OFFER }.map { }
    fun clientClicks() = clicksMutableFlow.filter { it == ClickType.CLIENT }.map { }

    override fun buildModels() {
        DateTimeModel(
            time = sequenceDayWindow?.toContentTimeRange(),
            date = date?.run { "${format(DateTimeFormat.DAY)} ${monthTitle(context, true)}" },
            dateClickAction = { clicksMutableFlow.tryEmit(ClickType.DATE) },
            timeClickAction = { clicksMutableFlow.tryEmit(ClickType.TIME) },
            marginsBundle = MarginsBundle.baseHorizontal,
            timeHint = R.string.window
        ).addTo(this)

        val offerTitle = category?.title?.run {
            offerItem?.getContentText().let { offerText ->
                "$this | $offerText"
            }
        }
        TextContainerModel(
            id = "offer",
            title = offerTitle,
            clickAction = { clicksMutableFlow.tryEmit(ClickType.OFFER) },
            hint = context.getString(R.string.offer),
            marginsBundle = MarginsBundle.base,
        ).addTo(this)

        TextContainerModel(
            id = "client",
            title = client?.fullname,
            clickAction = { clicksMutableFlow.tryEmit(ClickType.CLIENT) },
            hint = context.getString(R.string.client),
            marginsBundle = MarginsBundle.base,
        ).addTo(this)

        val saveBtnTitle = context.getString(R.string.save)
        val saveBtnEnable = date != null && sequenceDayWindow != null && offerTitle != null
        val clickListener = View.OnClickListener { clicksMutableFlow.tryEmit(ClickType.SAVE) }
        simpleButton {
            id("save")
            text(saveBtnTitle)
            enable(saveBtnEnable)
            onClick(clickListener)
            onBind { _, holder, _ -> holder.setMargins(MarginsBundle.base) }
        }
    }
}