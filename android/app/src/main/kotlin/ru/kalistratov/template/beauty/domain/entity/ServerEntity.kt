package ru.kalistratov.template.beauty.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Data<T>(val data: T)

@Serializable
data class DataList<T>(val data: List<T>) {
    companion object {
        fun <T> empty() = DataList<T>(emptyList())
    }
}
