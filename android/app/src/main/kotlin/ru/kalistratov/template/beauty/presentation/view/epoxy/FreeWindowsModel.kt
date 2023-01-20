package ru.kalistratov.template.beauty.presentation.view.epoxy

import android.view.View
import android.view.ViewParent
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.soywiz.klock.Time
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.ViewCardFreeWindowsBinding
import ru.kalistratov.template.beauty.divider
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.SequenceDayWindow
import ru.kalistratov.template.beauty.presentation.extension.connect
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.text
import ru.kalistratov.template.beauty.title

data class FreeWindowsModel(
    private val id: String,
    private val windows: List<SequenceDayWindow>,
    private val selectedWindowId: Id? = null,
) : EpoxyModelWithHolder<FreeWindowsModel.Holder>() {

    private companion object {
        private val timeEarlyMorning = Time(6)
        private val timeLunch = Time(12)
        private val timeEvening = Time(18)
        private val timeLateEvening = Time(22)
    }

    init {
        id("free_windows_$id")
    }

    override fun bind(holder: Holder) {
        with(holder.controller) {
            windows = this@FreeWindowsModel.windows
            selectedWindowId = this@FreeWindowsModel.selectedWindowId
            requestModelBuild()
        }
    }

    override fun getDefaultLayout() = R.layout.view_card_free_windows

    override fun createNewHolder(parent: ViewParent) = Holder()

    class Controller : EpoxyController() {

        var selectedWindowId: Id? = null
        var windows = emptyList<SequenceDayWindow>()

        override fun buildModels() {
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
                        selectedWindowId = selectedWindowId,
                        // clickAction = windowClicksMutableFlow::tryEmit,
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

    inner class Holder : EpoxyHolder() {
        val controller = Controller()
        lateinit var binding: ViewCardFreeWindowsBinding
        override fun bindView(itemView: View) {
            binding = ViewCardFreeWindowsBinding.bind(itemView)
                .apply { recycler.connect(controller) }
        }
    }
}