package ru.kalistratov.template.beauty.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewParent
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import kotlinx.coroutines.flow.asSharedFlow
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.ViewNestingListItemBinding
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow

class BreadCrumbsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val recycler by lazy { findViewById<RecyclerView>(R.id.recycler_view) }
    private val controller = Controller()
    private val clicks = mutableSharedFlow<Id>()

    fun update(nesting: List<Pair<Id, String>>) = with(controller) {
        this.nesting = nesting
        requestModelBuild()
    }

    fun getClickUpdates() = clicks.asSharedFlow()

    private val manager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

    init {
        inflate(context, R.layout.view_nesting_container, this)

        recycler.layoutManager = manager
        recycler.adapter = controller.adapter

        controller.addModelBuildListener { scrollToLast() }
        controller.removeModelBuildListener { scrollToLast() }
    }

    private fun scrollToLast() = manager.scrollToPosition(controller.nesting.size)

    private inner class Controller : EpoxyController() {

        var nesting: List<Pair<Id, String>> = emptyList()

        override fun buildModels() {
            //TODO Вынести и сделать настраиваемым
            ChipModel(
                "-1", "Категории", nesting.isEmpty(), clicks::tryEmit
            ).addTo(this)
            nesting.forEachIndexed { index, pair ->
                ChipModel(
                    pair.first, pair.second, index == nesting.lastIndex, clicks::tryEmit
                ).addTo(this)
            }
        }
    }

    private data class ChipModel(
        private val id: Id,
        private val title: String,
        private val isLast: Boolean,
        private val clickAction: (Id) -> Unit,
    ) : EpoxyModelWithHolder<ChipModel.Holder>() {

        init {
            id(id)
        }

        override fun bind(holder: Holder) = with(holder.binding) {
            chip.apply {
                text = title
                isChecked = isLast
                isClickable = !isLast
                if (!isLast) setOnClickListener {
                    clickAction.invoke(this@ChipModel.id)
                }
            }
            arrow.isVisible = !isLast
        }

        override fun getDefaultLayout() = R.layout.view_nesting_list_item

        override fun createNewHolder(parent: ViewParent) = Holder()

        class Holder : EpoxyHolder() {
            lateinit var binding: ViewNestingListItemBinding
            override fun bindView(itemView: View) {
                binding = ViewNestingListItemBinding.bind(itemView)
            }
        }
    }
}