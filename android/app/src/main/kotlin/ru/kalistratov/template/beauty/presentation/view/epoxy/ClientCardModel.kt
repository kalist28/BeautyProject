package ru.kalistratov.template.beauty.presentation.view.epoxy

import android.view.View
import android.view.ViewParent
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.ViewClientCardBinding
import ru.kalistratov.template.beauty.domain.entity.Client
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.setMargins

data class ClientCardModel(
    private val client: Client?,
    private val clickAction: () -> Unit = { },
    private val marginsBundle: MarginsBundle? = null,
    private val idByClient: Boolean = false
) : EpoxyModelWithHolder<ClientCardModel.Holder>() {

    init {
        id("client_card" + if (idByClient) client?.id else "")
    }

    override fun getDefaultLayout() = R.layout.view_client_card

    override fun createNewHolder(parent: ViewParent) = Holder()

    override fun bind(holder: Holder): Unit = holder.binding.run {
        marginsBundle?.let(root::setMargins)
        root.setOnClickListener { clickAction() }

        fullname.text = client?.fullname
        fullname.isVisible = client != null

        number.text = client?.number
        number.isVisible = client != null

        title.text = client?.run {
            "${name.firstOrNull() ?: ""}${surname?.firstOrNull() ?: ""}"
        }
        title.isVisible = client != null
        icContainer.isVisible = client != null

        notSelectedMessage.isVisible = client == null
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: ViewClientCardBinding
        override fun bindView(itemView: View) {
            binding = ViewClientCardBinding.bind(itemView)
        }
    }
}