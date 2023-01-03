package ru.kalistratov.template.beauty.domain.feature.contactpicker

import ru.kalistratov.template.beauty.domain.entity.Contact

interface ContactPickerInteractor {
    suspend fun getAll(): List<Contact>
}