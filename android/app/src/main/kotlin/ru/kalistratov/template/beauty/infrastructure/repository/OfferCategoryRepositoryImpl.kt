package ru.kalistratov.template.beauty.infrastructure.repository

import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.OfferCategory
import ru.kalistratov.template.beauty.domain.repository.OfferCategoryRepository
import ru.kalistratov.template.beauty.infrastructure.extensions.process
import ru.kalistratov.template.beauty.infrastructure.extensions.processWithNull
import ru.kalistratov.template.beauty.infrastructure.helper.mapper.toLocal
import ru.kalistratov.template.beauty.interfaces.server.entity.IncludeType
import ru.kalistratov.template.beauty.interfaces.server.service.ApiOfferCategoryService
import javax.inject.Inject

class OfferCategoryRepositoryImpl @Inject constructor(
    private val apiOfferCategoryService: ApiOfferCategoryService
) : OfferCategoryRepository {

    var cache: List<OfferCategory> = emptyList()

    override suspend fun get(id: Id): OfferCategory? =
        cache.find { it.id == id } ?: apiOfferCategoryService.get(id)
            .processWithNull({ it.toLocal() })

    override suspend fun getAll(root: Id?): List<OfferCategory> {
        if (cache.isNotEmpty()) return cache
        val result = apiOfferCategoryService.getTree(
            id = root, includeType = IncludeType.ChildrenTypes
        )
        cache = result.process(
            success = { map { it.toLocal() } },
            error = { cache }
        ).let {
            val rootCategory = it.firstOrNull() ?: return@let it
            when (rootCategory.title == "root") {
                true -> rootCategory.children
                false -> it
            }
        }
        return cache
    }

    override suspend fun findNested(ids: List<Id>): List<OfferCategory> {
        if (ids.isEmpty()) return cache
        val nesting = mutableListOf<OfferCategory>()
        var categories = cache
        var hasNext: Boolean
        with(ids.iterator()) {
            do {
                val id = next()
                hasNext = hasNext()

                categories.find { it.id == id }.also {
                    it?.let { nesting.add(it) }
                    categories = it?.children ?: emptyList()
                }
            } while (hasNext)
        }
        return nesting
    }
}