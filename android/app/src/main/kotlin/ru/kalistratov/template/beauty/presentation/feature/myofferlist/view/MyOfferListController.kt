package ru.kalistratov.template.beauty.presentation.feature.myofferlist.view

import android.content.Context
import android.view.View
import com.airbnb.epoxy.EpoxyController
import kotlinx.coroutines.flow.asSharedFlow
import ru.kalistratov.template.beauty.*
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.presentation.entity.PriceContainer
import ru.kalistratov.template.beauty.presentation.extension.toContentString
import ru.kalistratov.template.beauty.presentation.feature.myofferlist.entity.CreatingClickType
import ru.kalistratov.template.beauty.presentation.feature.myofferlist.entity.MyOfferListViewTypeState
import ru.kalistratov.template.beauty.presentation.feature.myofferlist.entity.OfferTypeContainer
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.epoxy.TextFieldModel
import ru.kalistratov.template.beauty.presentation.view.epoxy.PriceModel
import ru.kalistratov.template.beauty.presentation.view.epoxy.TextContainerModel
import ru.kalistratov.template.beauty.presentation.view.epoxy.setMargins

class MyOfferListController(
    private val context: Context
) : EpoxyController() {

    private companion object {
        val marginsBundle = MarginsBundle.base
        val propertyMarginsBundle = marginsBundle.copy(startMarginDp = 36)
    }

    var state: MyOfferListViewTypeState = MyOfferListViewTypeState.ListItems(emptyList())

    private val itemClicks = mutableSharedFlow<Id>()
    private val saveClicks = mutableSharedFlow<Unit>()
    private val creatingClicks = mutableSharedFlow<CreatingClickType>()
    private val priceSelections = mutableSharedFlow<PriceContainer>()
    private val descriptionUpdates = mutableSharedFlow<String>()

    override fun buildModels() {
        when (val state = state) {
            is MyOfferListViewTypeState.ListItems -> buildList(state)
            is MyOfferListViewTypeState.EditingItem -> buildEditingViews(state)
            is MyOfferListViewTypeState.CreatingItem -> buildCreatingViews(state)
        }
    }

    private val saveClickListener = View.OnClickListener { saveClicks.tryEmit(Unit) }


    private fun buildList(state: MyOfferListViewTypeState.ListItems) {
        val containers = state.containers
        containers.forEach { categoryContainer ->
            val category = categoryContainer.category
            title {
                id(category.id)
                titleText(category.title)
                onBind { _, holder, _ -> holder.setMargins(marginsBundle) }
            }

            if (categoryContainer.types.isNotEmpty()) divider {
                id("divider_$category")
                onBind { _, h, _ -> h.setMargins(marginsBundle) }
            }
            buildTypeContainers(categoryContainer.types)
        }
    }

    private fun buildTypeContainers(
        containers: List<OfferTypeContainer>
    ) = containers.forEachIndexed { index, typeContainer ->
        when (typeContainer) {
            is OfferTypeContainer.Single -> buildContainerSingle(typeContainer)
            is OfferTypeContainer.WithProperties -> buildContainerWithProperties(typeContainer)
        }
        divider {
            id("divider_$index")
            onBind { _, h, _ -> h.setMargins(marginsBundle) }
        }
    }

    private fun buildContainerSingle(container: OfferTypeContainer.Single) {
        val type = container.type
        val priceTextValue = container.price.toContentString(context)
        val listener = View.OnClickListener { itemClicks.tryEmit(container.itemId) }
        typeWithPrice {
            id("type_${type.id}")
            name(type.name)
            price(priceTextValue)
            onBind { _, holder, _ ->
                holder.setMargins(marginsBundle)
                holder.dataBinding.root.setOnClickListener(listener)
            }
        }
    }

    private fun buildContainerWithProperties(container: OfferTypeContainer.WithProperties) {
        val type = container.type
        text {
            id("type_${type.id}")
            text(type.name)
            onBind { _, holder, _ -> holder.setMargins(marginsBundle) }
        }
        container.properties.forEach { propertyContainer ->
            val property = propertyContainer.property
            val priceTextValue = propertyContainer.price.toContentString(context)
            val listener = View.OnClickListener { itemClicks.tryEmit(propertyContainer.itemId) }
            typePropertyWithPrice {
                id("type_property_${property.id}")
                name(property.name)
                price(priceTextValue)
                onBind { _, holder, _ ->
                    holder.setMargins(propertyMarginsBundle)
                    holder.dataBinding.root.setOnClickListener(listener)
                }
            }
        }
    }

    private fun buildCreatingViews(state: MyOfferListViewTypeState.CreatingItem) {
        val category = state.category
        val type = state.type
        val typeProperty = state.typeProperty
        val marginsBundle = MarginsBundle.base
        val priceSelection = state.priceContainer

        TextContainerModel(
            id = "category",
            title = category?.title,
            hint = "Категория",
            clickAction = { creatingClicks.tryEmit(CreatingClickType.SelectCategory) },
            marginsBundle = marginsBundle
        ).addTo(this)

        if (category == null) return

        TextContainerModel(
            id = "type",
            title = type?.name,
            hint = "Услуга",
            clickAction = { creatingClicks.tryEmit(CreatingClickType.SelectType) },
            marginsBundle = marginsBundle
        ).addTo(this)

        if (type == null) return

        if (type.properties.isNotEmpty()) {
            TextContainerModel(
                id = "typeProperty",
                title = typeProperty?.name,
                hint = "Параметр",
                clickAction = { creatingClicks.tryEmit(CreatingClickType.SelectTypeProperty) },
                marginsBundle = marginsBundle
            ).addTo(this)
            if (typeProperty == null) return
        }

        PriceModel(
            "offer_price",
            priceSelection,
            priceSelections::tryEmit,
            marginsBundle = marginsBundle
        ).addTo(this)

        TextFieldModel(
            id = "description",
            text = state.description,
            changes = { text -> descriptionUpdates.tryEmit(text) },
            marginsBundle = marginsBundle,
            hintId = R.string.offer_description
        ).addTo(this)

        val listener = saveClickListener
        simpleButton {
            id("save_button")
            text("Сохранить")
            enable(state.isAllowToSave())
            onBind { _, holder, _ -> holder.setMargins(marginsBundle) }
            onClick(listener)
        }
    }

    private fun buildEditingViews(state: MyOfferListViewTypeState.EditingItem) {
        val marginsBundle = MarginsBundle.base
        val priceSelection = state.priceContainer

        TextContainerModel(
            id = "category",
            title = state.categoryTitle,
            hint = "Категория",
            clickAction = { },
            marginsBundle = marginsBundle
        ).addTo(this)

        TextContainerModel(
            id = "type",
            title = state.offerItem.type.name,
            hint = "Услуга",
            clickAction = { },
            marginsBundle = marginsBundle
        ).addTo(this)

        state.offerItem.typeProperty?.let {
            TextContainerModel(
                id = "typeProperty",
                title = it.name,
                hint = "Параметр",
                clickAction = { creatingClicks.tryEmit(CreatingClickType.SelectTypeProperty) },
                marginsBundle = marginsBundle
            ).addTo(this)
        }

        PriceModel(
            "offer_price",
            priceSelection,
            priceSelections::tryEmit,
            marginsBundle = marginsBundle
        ).addTo(this)

        TextFieldModel(
            id = "description",
            text = state.offerItem.description,
            changes = { text -> descriptionUpdates.tryEmit(text) },
            marginsBundle = marginsBundle,
            hintId = R.string.offer_description
        ).addTo(this)

        val listener = saveClickListener
        simpleButton {
            id("save_button")
            text("Сохранить")
            enable(state.isAllowToSave())
            onBind { _, holder, _ -> holder.setMargins(marginsBundle) }
            onClick(listener)
        }
    }

    fun itemClicks() = itemClicks.asSharedFlow()
    fun saveClicks() = saveClicks.asSharedFlow()
    fun creatingClicks() = creatingClicks.asSharedFlow()
    fun priceSelections() = priceSelections.asSharedFlow()
    fun descriptionUpdates() = descriptionUpdates.asSharedFlow()
}