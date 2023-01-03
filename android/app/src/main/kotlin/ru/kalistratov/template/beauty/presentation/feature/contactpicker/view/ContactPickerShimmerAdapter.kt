package ru.kalistratov.template.beauty.presentation.feature.contactpicker.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.guilhe.views.PlaceHolderAdapter
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.setMargins

class ContactPickerShimmerAdapter :
    RecyclerView.Adapter<ContactPickerShimmerAdapter.Holder>(),
    PlaceHolderAdapter {

    class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        val shimmer by lazy { view.findViewById<ShimmerFrameLayout>(R.id.shimmer_view_container) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(
        LayoutInflater.from(parent.context).inflate(R.layout.view_contact_shimmer, parent, false)
    )

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.view.setMargins(MarginsBundle.base)
        holder.shimmer.apply {
            if (!isShimmerStarted) startShimmer()
        }
    }

    override fun getItemCount() = 10
}