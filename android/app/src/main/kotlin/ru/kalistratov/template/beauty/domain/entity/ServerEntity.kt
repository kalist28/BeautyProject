package ru.kalistratov.template.beauty.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Data<T>(val data: T)
