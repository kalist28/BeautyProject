package ru.kalistratov.template.beauty.infrastructure.helper.mapper

import ru.kalistratov.template.beauty.domain.entity.Client
import ru.kalistratov.template.beauty.infrastructure.extensions.replaceBlankToNull
import ru.kalistratov.template.beauty.infrastructure.extensions.toFormat
import ru.kalistratov.template.beauty.infrastructure.extensions.toNumbers
import ru.kalistratov.template.beauty.interfaces.server.dto.ServerClient
import ru.kalistratov.template.beauty.interfaces.server.dto.ServerClientDataBundle

fun ServerClient.toLocal() = Client(
    id = id,
    name = name,
    surname = surname,
    patronymic = patronymic,
    number = phone_number.toFormat(),
    note = note
)

fun Client.toBundle() = ServerClientDataBundle(
    name = name,
    surname = surname?.replaceBlankToNull(),
    patronymic = patronymic?.replaceBlankToNull(),
    phone_number = number.toNumbers(),
    note = note?.replaceBlankToNull()
)