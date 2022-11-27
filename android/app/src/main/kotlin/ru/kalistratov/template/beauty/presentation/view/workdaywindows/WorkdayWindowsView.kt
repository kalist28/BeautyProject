package ru.kalistratov.template.beauty.presentation.view.workdaywindows

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyVisibilityTracker
import kotlinx.coroutines.flow.asSharedFlow
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.SequenceDayWindow
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow

class WorkdayWindowsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {
    private val controller by lazy { WorkdayWindowsController() }
    private val loadingView: View by lazy { findViewById(R.id.loading_view) }
    private val recyclerView: RecyclerView by lazy { findViewById(R.id.recycler_view) }

    private val windowClicks = mutableSharedFlow<Id>()
    private val stateChanges = mutableSharedFlow<IdSelector.State>()
    private val selectChanges = mutableSharedFlow<List<Id>>()

    val selector = IdSelector()

    init {
        inflate(context, R.layout.view_sequence_week, this)
        val epoxyVisibilityTracker = EpoxyVisibilityTracker()
        with(recyclerView) {
            epoxyVisibilityTracker.attach(this)
            adapter = controller.adapter
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(context)
            overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }
        setLoading(false)
    }

    fun updateWorkdayWindows(
        windows: List<SequenceDayWindow>
    ) = with(controller) {
        this.windows = windows
        requestModelBuild()
    }

    fun setLoading(loading: Boolean) {
        loadingView.isVisible = loading
        recyclerView.isVisible = !loading
    }

    fun clicks() = windowClicks.asSharedFlow()
    fun stateChanges() = stateChanges.asSharedFlow()
    fun selectChanges() = selectChanges.asSharedFlow()

    fun cancelSelection() {
        changeState()
        controller.requestModelBuild()
    }

    private fun onWindowClick(id: Id) {
        when (selector.isSelector()) {
            true -> with(selector) {
                windowClicked(id)
                selectChanges.tryEmit(getSelected())
                controller.requestModelBuild()
            }
            false -> windowClicks.tryEmit(id)
        }
    }

    private fun onWindowLongClick(id: Id) {
        changeState()
        onWindowClick(id)
    }

    private fun changeState() = stateChanges
        .tryEmit(selector.changeState())

    inner class WorkdayWindowsController : EpoxyController() {
        var windows: List<SequenceDayWindow> = emptyList()

        override fun buildModels() {
            windows.forEachIndexed { index, window ->
                createNewModel(index, window)
                    .addTo(this)
            }
        }

        private fun createNewModel(
            number: Int, window: SequenceDayWindow
        ) = SequenceDayWindowModel(
            number, window,
            ::onWindowClick,
            ::onWindowLongClick,
            selector.isSelector(),
            selector.contains(window.id)
        )

    }
}

class IdSelector {

    enum class State {
        LIST,
        SELECTOR
    }

    private val selectedIds = mutableListOf<Id>()

    var state = State.LIST

    fun getSelected(): List<Id> = selectedIds.toList()

    fun contains(id: Id) = selectedIds.contains(id)

    fun isSelector() = state == State.SELECTOR

    fun windowClicked(id: Id) {
        if (contains(id)) selectedIds.remove(id)
        else selectedIds.add(id)
    }

    fun changeState(): State {
        state = when (state == State.SELECTOR) {
            true -> State.LIST
            false -> State.SELECTOR
        }
        if (!isSelector()) selectedIds.clear()
        return state
    }
}