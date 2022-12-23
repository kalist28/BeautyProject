package ru.kalistratov.template.beauty.presentation.feature.myofferlist.di

import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import ru.kalistratov.template.beauty.domain.feature.myofferlist.MyOfferListInteractor
import ru.kalistratov.template.beauty.domain.repository.OfferCategoryRepository
import ru.kalistratov.template.beauty.domain.repository.OfferItemRepository
import ru.kalistratov.template.beauty.domain.repository.OfferTypeRepository
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelKey
import ru.kalistratov.template.beauty.presentation.feature.myofferlist.MyOfferListInteractorImpl
import ru.kalistratov.template.beauty.presentation.feature.myofferlist.MyOfferListRouter
import ru.kalistratov.template.beauty.presentation.feature.myofferlist.MyOfferListRouterImpl
import ru.kalistratov.template.beauty.presentation.feature.myofferlist.MyOfferListViewModel
import ru.kalistratov.template.beauty.presentation.feature.myofferlist.view.MyOfferListFragment

@Subcomponent(
    modules = [
        MyOfferListModule::class,
        MyOfferListPresentationModule::class
    ]
)
interface MyOfferListComponent {
    fun inject(fragment: MyOfferListFragment)
}

@Module
class MyOfferListModule(private val fragment: MyOfferListFragment) {

    @Provides
    fun provideInteractor(
        offerTypeRepository: OfferTypeRepository,
        offerItemRepository: OfferItemRepository,
        offerCategoryRepository: OfferCategoryRepository
    ): MyOfferListInteractor = MyOfferListInteractorImpl(
        offerTypeRepository,
        offerItemRepository,
        offerCategoryRepository
    )

    @Provides
    fun provideRouter(): MyOfferListRouter = MyOfferListRouterImpl(
        "MyOfferListFragment",
        fragment.findNavController()
    )
}

@Module
abstract class MyOfferListPresentationModule {
    @Binds
    @IntoMap
    @ViewModelKey(MyOfferListViewModel::class)
    abstract fun bindViewModel(viewModel: MyOfferListViewModel): ViewModel
}