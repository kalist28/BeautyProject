package ru.kalistratov.template.beauty.presentation.view.weeksequence

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModelWithView
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.filterNotNull
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.entity.WeekSequence
import ru.kalistratov.template.beauty.domain.entity.WorkDaySequence

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

    fun clicks(): Flow<Int> = controller.clicks()
}

class WeekSequenceController : EpoxyController() {

    private val _clicks = callbackFlow {
        onDayClickListener = OnDayClickListener { trySend(it) }
        awaitClose { onDayClickListener = null }
    }.conflate()

    private var onDayClickListener: OnDayClickListener? = null

    var weekSequence: WeekSequence = WeekSequence()

    override fun buildModels() {
        weekSequence.days.forEach {
            add(WeekSequenceDayModel(it, onDayClickListener))
        }
    }

    fun clicks(): Flow<Int> = _clicks.filterNotNull()
}

class WeekSequenceDayModel(
    private val workDay: WorkDaySequence,
    private val clickListener: OnDayClickListener?,
) : EpoxyModelWithView<WorkDaySequenceView>() {

    private val dayId = workDay.day.index

    init {
        id(workDay.hashCode())
    }

    override fun buildView(parent: ViewGroup): WorkDaySequenceView =
        WorkDaySequenceView(workDay, parent.context).apply {
            setOnClickListener {
                clickListener?.onDayClick(dayId)
            }
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as WeekSequenceDayModel

        if (workDay != other.workDay) return false
        if (clickListener != other.clickListener) return false
        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + workDay.hashCode()
        result = 31 * result + (clickListener?.hashCode() ?: 0)
        result = 31 * result + dayId
        return result
    }
}
