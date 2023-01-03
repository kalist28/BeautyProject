package ru.kalistratov.template.beauty.presentation.feature.contactpicker.view

import android.view.View
import android.view.ViewParent
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asSharedFlow
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.ViewContactBinding
import ru.kalistratov.template.beauty.domain.entity.Contact
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.setMargins

class ContactPickerController : EpoxyController() {

    private val clicksMutableFlow = mutableSharedFlow<Id>()

    var contacts: List<Contact> = emptyList()

    override fun buildModels() = contacts.forEach {
        ContactModel(it, clicksMutableFlow::tryEmit)
            .addTo(this)
    }


    fun clicks(): Flow<Id> = clicksMutableFlow.asSharedFlow()
}

class ContactModel(
    private val contact: Contact,
    private val clickAction: (Id) -> Unit,
) : EpoxyModelWithHolder<ContactModel.ContactHolder>() {

    init {
        id(contact.id)
    }

    override fun getDefaultLayout(): Int = R.layout.view_contact

    override fun createNewHolder(parent: ViewParent) = ContactHolder()

    override fun bind(holder: ContactHolder): Unit = with(holder.binding) {
        root.setMargins(MarginsBundle.base)
        root.setOnClickListener {
            clickAction.invoke(contact.id)
        }
        name.text = contact.name
        number.text = contact.number
        photo.let {
            Glide.with(it)
                .load(
                    contact.photoUri
                        ?: "https://uxwing.com/wp-content/themes/uxwing/download/brands-and-social-media/google-contacts-icon.png"
                )
                .circleCrop()
                .into(it)
        }
    }

    class ContactHolder : EpoxyHolder() {
        lateinit var binding: ViewContactBinding
        override fun bindView(itemView: View) {
            binding = ViewContactBinding.bind(itemView)
        }
    }
}