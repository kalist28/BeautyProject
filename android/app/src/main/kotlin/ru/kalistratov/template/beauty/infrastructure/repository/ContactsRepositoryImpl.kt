package ru.kalistratov.template.beauty.infrastructure.repository

import android.Manifest.permission.READ_CONTACTS
import android.content.Context
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID
import android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER
import android.provider.ContactsContract.CommonDataKinds.Phone.PHOTO_URI
import android.provider.ContactsContract.Contacts.DISPLAY_NAME
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import ru.kalistratov.template.beauty.domain.entity.Contact
import ru.kalistratov.template.beauty.domain.repository.ContactsRepository
import ru.kalistratov.template.beauty.domain.service.PermissionsService
import ru.kalistratov.template.beauty.infrastructure.extensions.loge

class ContactsRepositoryImpl(
    private val context: Context,
    private val permissionsService: PermissionsService
) : ContactsRepository {

    companion object {
        private val PROJECTION = arrayOf(CONTACT_ID, DISPLAY_NAME, NUMBER, PHOTO_URI)
    }

    private val contentResolver = context.contentResolver


    override fun getAll(): List<Contact> {
        if (!getPermissionGranted()) return emptyList()
        return contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            PROJECTION, null, null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )?.use { cursor ->
            mutableSetOf<Contact>().apply {
                val nameIndex = cursor.getColumnIndex(DISPLAY_NAME)
                val numberIndex = cursor.getColumnIndex(NUMBER)
                val photoUriIndex = cursor.getColumnIndex(PHOTO_URI)
                while (cursor.moveToNext()) {
                    val name = cursor.getString(nameIndex)
                    val photoUri = cursor.getString(photoUriIndex)
                    val number = cursor.getString(numberIndex).replace(" ", "")
                    add(Contact(name, number, photoUri))
                }
            }
        }?.toList() ?: emptyList()
    }

    override fun getPermissionGranted(): Boolean = ContextCompat
        .checkSelfPermission(context, READ_CONTACTS)
        .let { it == PermissionChecker.PERMISSION_GRANTED }
        .also {
            loge("************ $it")
            if(!it) permissionsService.requestContactsPermission() }
}