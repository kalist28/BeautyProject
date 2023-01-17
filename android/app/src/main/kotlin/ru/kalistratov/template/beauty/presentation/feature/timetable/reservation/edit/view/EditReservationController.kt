package ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.edit.view

import android.content.Context
import android.view.View
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
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.TextFieldDrawableBundle
import ru.kalistratov.template.beauty.presentation.view.epoxy.ClientCardModel
import ru.kalistratov.template.beauty.presentation.view.epoxy.TextContainerModel
import ru.kalistratov.template.beauty.presentation.view.epoxy.WindowsChipsTripleModel
import ru.kalistratov.template.beauty.presentation.view.epoxy.setMargins

class EditReservationController(
    private val context: Context
) : EpoxyController() {

    private companion object {
        private val timeEarlyMorning = Time(6)
        private val timeLunch = Time(12)
        private val timeEvening = Time(18)
        private val timeLateEvening = Time(22)
    }

    private enum class ClickType {
        SAVE,
        DATE,
        OFFER,
        CLIENT,
    }

    private val windowClicksMutableFlow = mutableSharedFlow<Id?>()
    private val clicksMutableFlow = mutableSharedFlow<ClickType>()

    var date: Date? = null
    var windows: List<SequenceDayWindow>? = null
    var window: SequenceDayWindow? = null
    var category: OfferCategory? = null
    var offerItem: OfferItem? = null
    var client: Client? = null

    fun saveClicks() = clicksMutableFlow.filter { it == ClickType.SAVE }.map { }
    fun dateClicks() = clicksMutableFlow.filter { it == ClickType.DATE }.map { }
    fun offerClicks() = clicksMutableFlow.filter { it == ClickType.OFFER }.map { }
    fun clientClicks() = clicksMutableFlow.filter { it == ClickType.CLIENT }.map { }
    fun windowClicks() = windowClicksMutableFlow.asSharedFlow()

    override fun buildModels() {
        val dateText = date?.run { "${format(DateTimeFormat.DAY)} ${monthTitle(context, true)}" }
        TextContainerModel(
            id = "date",
            title = dateText,
            hint = context.getString(R.string.date),
            marginsBundle = MarginsBundle.base,
            clickAction = { clicksMutableFlow.tryEmit(ClickType.DATE) },
            drawableBundle = TextFieldDrawableBundle(
                right = R.drawable.ic_calendar_month
            )
        ).addTo(this)

        buildWindowsModels()


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

        ClientCardModel(
            client = client,
            clickAction = { clicksMutableFlow.tryEmit(ClickType.CLIENT) },
            marginsBundle = MarginsBundle.base,
        ).addTo(this)

        val saveBtnTitle = context.getString(R.string.save)
        val saveBtnEnable = date != null && window != null && offerTitle != null
        val clickListener = View.OnClickListener { clicksMutableFlow.tryEmit(ClickType.SAVE) }
        simpleButton {
            id("save")
            text(saveBtnTitle)
            enable(saveBtnEnable)
            onClick(clickListener)
            onBind { _, holder, _ -> holder.setMargins(MarginsBundle.base) }
        }
    }

    private fun buildWindowsModels() {
        val windows = windows ?: emptyList()

        loge(window)

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

        title {
            id("title")
            titleText("Окно")
            onBind { _, holder, _ -> holder.setMargins(MarginsBundle.base) }
        }

        divider {
            id("top")
            onBind { _, holder, _ -> holder.setMargins(MarginsBundle.base) }
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
            text {
                id(title)
                text(title)
                onBind { _, holder, _ -> holder.setMargins(MarginsBundle.base) }
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
                    selectedWindowId = window?.id,
                    clickAction = windowClicksMutableFlow::tryEmit,
                    marginsBundle = MarginsBundle.baseHorizontal
                ).addTo(this)
            }
        }

        divider {
            id("bottom")
            onBind { _, holder, _ -> holder.setMargins(MarginsBundle.base) }
        }
    }
}