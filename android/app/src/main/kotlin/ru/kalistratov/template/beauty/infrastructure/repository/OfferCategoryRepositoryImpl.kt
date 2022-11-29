package ru.kalistratov.template.beauty.infrastructure.repository

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.OfferCategory
import ru.kalistratov.template.beauty.domain.repository.OfferCategoryRepository
import ru.kalistratov.template.beauty.infrastructure.helper.mapper.toLocal
import ru.kalistratov.template.beauty.interfaces.server.entity.IncludeType
import ru.kalistratov.template.beauty.interfaces.server.service.ApiOfferCategoryService

class OfferCategoryRepositoryImpl(
    private val apiOfferCategoryService: ApiOfferCategoryService
) : OfferCategoryRepository {

    var cache: List<OfferCategory> = emptyList()

    override suspend fun get(root: Id?): List<OfferCategory> {
        if (cache.isNotEmpty()) return cache
        val result = apiOfferCategoryService.get(
            id = root,
            includeType = IncludeType.ChildrenTypes
        )
        cache = when (result is NetworkResult.Success) {
            false -> cache
            true -> result.value.map { it.toLocal() }
        }.let {
            val rootCategoty = it.firstOrNull() ?: return@let it
            when (rootCategoty.title == "root") {
                true -> rootCategoty.children
                false -> it
            }
        }
        return cache
        /* return mutableListOf<OfferCategory>().apply {

            val a = aaaa
            a.firstOrNull()?.let { add(it) }
            a.firstOrNull()?.let { add(it.copy(title = "Маникюр", id = "1")) }
            a.firstOrNull()?.let { add(it.copy(title = "Брови", id = "2")) }

            val aaa = a.first().copy(
                title = "root3 sdfg sdfg sdfg",
                id = "3",
                children = mutableListOf<OfferCategory>().apply {
                    add(
                        a.first().copy(
                            title = "Маникюр",
                            id = "sdfa"
                        )
                    )
                    add(
                        a.first().copy(
                            title = "Брови",
                            id = "cxvb"
                        )
                    )
                    add(a.first().copy(title = "678dfg sdf gsdfg sdf", id = "vnm"))
                })

            a.firstOrNull()?.let {
                add(
                    it.copy(
                        title = "Брови",
                        id = "3",
                        children = mutableListOf<OfferCategory>().apply {
                            add(it.copy(title = "Детские брови", id = "vnm", children = listOf(aaa)))
                        })
                )
            }

            forEach(::repl)
            cache = this
        }*/
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

                categories.find { it.id == id }
                    .also {
                        it?.let { nesting.add(it) }
                        categories = it?.children ?: emptyList()
                    }
            } while (hasNext)
        }
        return nesting
    }

    var count = 0
    fun repl(cat: OfferCategory) {
        cat.id = count++.toString()
        cat.children.forEach(::repl)
    }
}