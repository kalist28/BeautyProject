package ru.kalistratov.template.beauty.presentation.view.weeksequence

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.entity.WeekSequence

fun interface OnDayClickListener {
    fun onDayClick(day: Int)
}

class WeekSequenceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {
    private val controller by lazy { WeekSequenceController() }
    private val loadingView: View by lazy { findViewById(R.id.loading_view) }
    private val recyclerView: RecyclerView by lazy { findViewById(R.id.recycler_view) }

    companion object {
        private val clicks = MutableSharedFlow<Int>(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }

    init {
        inflate(context, R.layout.view_week_sequence, this)
        with(recyclerView) {
            adapter = controller.adapter
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(context)
        }
    }

    fun requestModelBuild(weekSequence: WeekSequence) = with(controller) {
        this.weekSequence = weekSequence
        requestModelBuild()
    }

    fun setLoading(loading: Boolean) {
        loadingView.isVisible = loading
        recyclerView.isVisible = !loading
    }

    fun clicks(): Flow<Int> = clicks

    inner class WeekSequenceController : EpoxyController() {
        var weekSequence: WeekSequence = WeekSequence()

        override fun buildModels() {
            weekSequence.days.forEach {
                add(WeekSequenceDayModel(it) { clicks.tryEmit(it) })
            }
        }
    }
}
