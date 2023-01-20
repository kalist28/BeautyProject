package ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.edit.view

import android.content.Context
import com.airbnb.epoxy.EpoxyController
import com.soywiz.klock.Date
import com.soywiz.klock.Time
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import ru.kalistratov.template.beauty.*
import ru.kalistratov.template.beauty.common.DateTimeFormat
import ru.kalistratov.template.beauty.domain.entity.*
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.infrastructure.extensions.monthTitle
import ru.kalistratov.template.beauty.presentation.view.Margins
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.epoxy.*

class EditReservationController(
    private val context: Context
) : EpoxyController() {

    private companion object {
        private val timeEarlyMorning = Time(6)
        private val timeLunch = Time(12)
        private val timeEvening = Time(18)
        private val timeLateEvening = Time(22)
        private val overSmallTextMargin = MarginsBundle
            .horizontalOf(22)
            .copy(topMarginDp = 8, bottomMarginDp = 4)
    }

    private enum class ClickType {
        DATE,
        OFFER,
        CLIENT,
    }

    private val windowClicksMutableFlow = mutableSharedFlow<Id?>()
    private val clicksMutableFlow = mutableSharedFlow<ClickType>()

    var date: Date? = null
    var windows: List<SequenceDayWindow>? = null
    var selectedWindow: SequenceDayWindow? = null
    var category: OfferCategory? = null
    var offerItem: OfferItem? = null
    var client: Client? = null

    fun dateClicks() = clicksMutableFlow.filter { it == ClickType.DATE }.map { }
    fun offerClicks() = clicksMutableFlow.filter { it == ClickType.OFFER }.map { }
    fun clientClicks() = clicksMutableFlow.filter { it == ClickType.CLIENT }.map { }
    fun windowClicks() = windowClicksMutableFlow.asSharedFlow()

    override fun buildModels() {
        loge("dsfasdfasdf")
        val dateText = date?.run { "${format(DateTimeFormat.DAY)} ${monthTitle(context, true)}" }
        TextCardContainerModel(
            id = "date",
            title = dateText,
            hint = context.getString(R.string.date),
            marginsBundle = MarginsBundle.baseVertical,
            clickAction = { clicksMutableFlow.tryEmit(ClickType.DATE) },
            drawable = R.drawable.ic_calendar_month
        ).addTo(this)

        buildWindowsModels()

        val offerTitle = category?.title?.run {
            offerItem?.getContentText().let { offerText ->
                "$this | $offerText"
            }
        }
        TextCardContainerModel(
            id = "offer",
            title = offerTitle,
            hint = context.getString(R.string.offer),
            clickAction = { clicksMutableFlow.tryEmit(ClickType.OFFER) },
            marginsBundle = MarginsBundle.baseVertical,
            drawable = R.drawable.ic_offer
        ).addTo(this)

        val clientTitle = context.getString(R.string.client)
        overSmallTitle {
            id("client_title")
            title(clientTitle)
            onBind { _, holder, _ -> holder.setMargins(overSmallTextMargin) }
        }
        ClientCardModel(
            client = client,
            clickAction = { clicksMutableFlow.tryEmit(ClickType.CLIENT) },
            marginsBundle = MarginsBundle.baseHorizontal,
        ).addTo(this)
    }

    private fun buildWindowsModels() {
        val windows = windows ?: emptyList()


        val morningList = mutableListOf<SequenceDayWindow>()
        val dayList = mutableListOf<SequenceDayWindow>()
        val eveningList = mutableListOf<SequenceDayWindow>()
        val other = mutableListOf<SequenceDayWindow>()
        windows.forEach {
            val start = it.startAt
            val isMorning = start >= timeEarlyMorning && start < timeLunch
            val isDay = start >= timeLunch && start < timeEvening
            val isEvening = start >= timeEvening && start < timeLateEvening
            when {
                isMorning -> morningList.add(it)
                isDay -> dayList.add(it)
                isEvening -> eveningList.add(it)
                else -> other.add(it)
            }
        }
        if (windows.isEmpty()) text {
            id("empty")
            text("свободных окон нет")
            onBind { _, holder, _ -> holder.setMargins(MarginsBundle.base) }
        } else mapOf(
            "Утро" to morningList,
            "День" to dayList,
            "Вечер" to eveningList,
            "Поздние" to other
        ).forEach { title, windows ->
            if (windows.isEmpty()) return@forEach
            overSmallTitle {
                id(title)
                title(title)
                onBind { _, holder, _ -> holder.setMargins(overSmallTextMargin) }
            }
            (0..windows.size step 3).forEach {
                val windows = mutableListOf<SequenceDayWindow>().apply {
                    windows.getOrNull(it)?.let(::add)
                    windows.getOrNull(it + 1)?.let(::add)
                    windows.getOrNull(it + 2)?.let(::add)
                }
                if (windows.isNotEmpty()) WindowsChipsTripleModel(
                    id = "$title$it",
                    windows = windows,
                    selectedWindowId = selectedWindow?.id,
                    clickAction = windowClicksMutableFlow::tryEmit,
                    marginsBundle = MarginsBundle.baseHorizontal
                ).addTo(this)
            }
        }
    }
}