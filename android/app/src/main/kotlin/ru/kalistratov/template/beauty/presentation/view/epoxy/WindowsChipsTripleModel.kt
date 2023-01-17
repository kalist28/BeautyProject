package ru.kalistratov.template.beauty.presentation.view.epoxy

import android.view.View
import android.view.ViewParent
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.google.android.material.chip.Chip
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.ListItemWindowsChipsTripleBinding
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.SequenceDayWindow
import ru.kalistratov.template.beauty.infrastructure.extensions.clockTimeFormat
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.setMargins

data class WindowsChipsTripleModel(
    private val id: String? = null,
    private val windows: List<SequenceDayWindow>,
    private val selectedWindowId: Id? = null,
    private val clickAction: (Id?) -> Unit = { },
    private val marginsBundle: MarginsBundle? = null
) : EpoxyModelWithHolder<WindowsChipsTripleModel.Holder>() {

    init {
        id("window_ships_${id}")
    }

    override fun bind(holder: Holder): Unit = with(holder.binding) {
        first.bind(windows.getOrNull(0))
        second.bind(windows.getOrNull(1))
        third.bind(windows.getOrNull(2))
    }

    private fun Chip.bind(window: SequenceDayWindow?) {
        window?.run { text = startAt.format(clockTimeFormat) }

        isChecked = selectedWindowId != null && window != null &&
                selectedWindowId == window.id
        visibility = if (window == null) View.INVISIBLE else View.VISIBLE
        setOnClickListener {
            clickAction.invoke(
                when {
                    isChecked -> window?.id
                    selectedWindowId == window?.id -> null
                    else -> return@setOnClickListener
                }
            )
        }
    }

    override fun getDefaultLayout() = R.layout.list_item_windows_chips_triple

    override fun createNewHolder(parent: ViewParent) = Holder()

    inner class Holder : EpoxyHolder() {
        lateinit var binding: ListItemWindowsChipsTripleBinding
        override fun bindView(itemView: View) {
            binding = ListItemWindowsChipsTripleBinding.bind(itemView).apply {
                marginsBundle?.let(root::setMargins)
            }
        }

    }
}