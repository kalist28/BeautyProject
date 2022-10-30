package ru.kalistratov.template.beauty.domain.repository

import ru.kalistratov.template.beauty.domain.entity.Contact

interface ContactsRepository {
    fun getAll(): List<Contact>
    fun getPermissionGranted(): Boolean
}