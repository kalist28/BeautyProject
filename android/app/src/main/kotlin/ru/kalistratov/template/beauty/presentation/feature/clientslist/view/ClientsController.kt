package ru.kalistratov.template.beauty.presentation.feature.clientslist.view

import android.view.View
import android.view.ViewParent
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.entity.Contact

class ClientsController: EpoxyController() {

    var clients: List<Contact> = emptyList()

    override fun buildModels() {
        clients.forEach {
            ContactModel(it).addTo(this)
        }
    }
}

class ContactModel(
    private val contact: Contact
): EpoxyModelWithHolder<ContactModel.ContactHolder>() {

    init {
        id(contact.name.hashCode())
    }

    override fun getDefaultLayout(): Int = R.layout.view_contact

    override fun createNewHolder(parent: ViewParent) = ContactHolder()

    override fun bind(holder: ContactHolder) {
        super.bind(holder)
        holder.nameTextView?.text = contact.name
        holder.numberTextView?.text = contact.number
        holder.numberImageView?.let {
            Glide.with(it)
                .load(contact.photoUri ?: "https://uxwing.com/wp-content/themes/uxwing/download/brands-and-social-media/google-contacts-icon.png")
                .circleCrop()
                .into(it)
        }
    }

    class ContactHolder : EpoxyHolder() {
        var root: View? = null
        var nameTextView: TextView? = null
        var numberTextView: TextView? = null
        var numberImageView: ImageView? = null
        override fun bindView(itemView: View) = with(itemView) {
            root = itemView
            nameTextView = findViewById(R.id.name)
            numberTextView = findViewById(R.id.number)
            numberImageView = findViewById(R.id.photo)
        }
    }
}