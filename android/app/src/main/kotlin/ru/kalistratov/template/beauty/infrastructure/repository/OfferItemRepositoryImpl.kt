package ru.kalistratov.template.beauty.infrastructure.repository

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.*
import ru.kalistratov.template.beauty.domain.repository.OfferItemRepository
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.infrastructure.extensions.process
import ru.kalistratov.template.beauty.infrastructure.helper.mapper.toLocal
import ru.kalistratov.template.beauty.infrastructure.helper.mapper.toServer
import ru.kalistratov.template.beauty.interfaces.server.dto.offer.RemoveOfferItemsRequest
import ru.kalistratov.template.beauty.interfaces.server.entity.IncludeType
import ru.kalistratov.template.beauty.interfaces.server.service.ApiOfferItemService
import javax.inject.Inject

class OfferItemRepositoryImpl @Inject constructor(
    private val apiOfferItemService: ApiOfferItemService
) : OfferItemRepository {

    private val include = IncludeType.typeOfTypes(IncludeType.Type, IncludeType.TypeProperties)

    override suspend fun add(bundle: OfferItemDataBundle) = add(null, bundle)

    override suspend fun add(
        id: Id?,
        bundle: OfferItemDataBundle
    ) = apiOfferItemService
        .post(id, bundle.toServer(), IncludeType.Empty)
        .process(
            success = { toLocal() },
            error = { null }
        )

    override suspend fun get(id: Id): OfferItem? = apiOfferItemService
        .load(id, include).process(
            success = { toLocal() },
            error = { null }
        )

    override suspend fun getAll(category: Id?): List<OfferItem> = apiOfferItemService
        .loadAll(category, include).let { result ->
            when (result) {
                is NetworkResult.Success -> result.value.map { it.toLocal() }
                else -> emptyList()
            }
        }

    override suspend fun remove(id: Id) {
        apiOfferItemService.remove(RemoveOfferItemsRequest(listOf(id)))
    }

    override suspend fun removeAll(ids: List<Id>) {
        TODO("Not yet implemented")
    }
}