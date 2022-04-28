package ru.kalistratov.template.beauty.presentation.view.workdaywindows

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.entity.WorkdayWindow
import ru.kalistratov.template.beauty.domain.extension.toDateTime
import ru.kalistratov.template.beauty.infrastructure.extensions.loge

class WorkdayWindowsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {
    private val controller by lazy { WorkdayWindowsController() }
    private val loadingView: View by lazy { findViewById(R.id.loading_view) }
    private val recyclerView: RecyclerView by lazy { findViewById(R.id.recycler_view) }

    val windowClicks = MutableSharedFlow<Long>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        inflate(context, R.layout.view_week_sequence, this)
        with(recyclerView) {
            adapter = controller.adapter
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(context)
        }
        setLoading(false)
    }

    fun updateWorkdayWindows(windows: List<WorkdayWindow>) {
        loge(windows)
        controller.windows = windows.sortedWith(
            Comparator { day1, day2 ->
                val time1 = day1.startAt
                val time2 = day2.startAt
                return@Comparator time1.compareTo(time2)
            }
        )
        controller.requestModelBuild()
    }

    fun setLoading(loading: Boolean) {
        loadingView.isVisible = loading
        recyclerView.isVisible = !loading
    }

    inner class WorkdayWindowsController : EpoxyController() {
        var windows: List<WorkdayWindow> = emptyList()

        override fun buildModels() {
            windows.forEach { createNewModel(it).addTo(this) }
        }

        private fun createNewModel(window: WorkdayWindow) =
            WorkdayWindowModel(window) { windowClicks.tryEmit(it) }
    }
}


