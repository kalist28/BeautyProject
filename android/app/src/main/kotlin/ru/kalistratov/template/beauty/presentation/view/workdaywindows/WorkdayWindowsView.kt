package ru.kalistratov.template.beauty.presentation.view.workdaywindows

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
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.entity.WorkdayWindow

class WorkdayWindowsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {
    private val controller by lazy { WorkdayWindowsController() }
    private val loadingView: View by lazy { findViewById(R.id.loading_view) }
    private val recyclerView: RecyclerView by lazy { findViewById(R.id.recycler_view) }

    private val windowClicks = MutableSharedFlow<Long>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        inflate(context, R.layout.view_sequence_week, this)
        with(recyclerView) {
            adapter = controller.adapter
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(context)
            overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }
        setLoading(false)
    }

    fun updateWorkdayWindows(
        windows: List<WorkdayWindow>
    ) = with(controller) {
        this.windows = windows
        requestModelBuild()
    }

    fun setLoading(loading: Boolean) {
        loadingView.isVisible = loading
        recyclerView.isVisible = !loading
    }

    fun clicks() = windowClicks.asSharedFlow()

    inner class WorkdayWindowsController : EpoxyController() {
        var windows: List<WorkdayWindow> = emptyList()

        override fun buildModels() {
            windows.forEachIndexed { index, window ->
                createNewModel(index, window)
                    .addTo(this)
                if (index != windows.lastIndex) SeparatorModel(index)
                    .addTo(this)
            }
        }

        private fun createNewModel(number: Int, window: WorkdayWindow) =
            SequenceDayWindowModel(number, window) { windowClicks.tryEmit(it) }
    }

    inner class SeparatorModel(id: Int) : EpoxyModelWithHolder<SeparatorModel.ViewHolder>() {

        init {
            id(id)
        }

        override fun getDefaultLayout() = R.layout.view_menu_separator

        override fun createNewHolder(parent: ViewParent) = ViewHolder()

        inner class ViewHolder : EpoxyHolder() {
            override fun bindView(itemView: View) = Unit
        }
    }
}
