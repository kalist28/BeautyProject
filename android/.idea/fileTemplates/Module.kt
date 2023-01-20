#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME}

#end
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap

@Subcomponent(
    modules = [
        ${NAME}Module::class,
        ${NAME}BindsModule::class,
        ${NAME}PresentationModule::class,
    ]
)
interface ${NAME}Component {
    fun inject(fragment: ${NAME}Fragment)
}

@Module
class ${NAME}Module(private val fragment: ${NAME}Fragment) {

    @Provides
    fun provideRouter(): ${NAME}Router = ${NAME}RouterImpl(
        "${NAME}Fragment",
        fragment.findNavController()
    )
}

@Module
interface ${NAME}BindsModule {

    @Binds
    fun provideInteractor(impl: ${NAME}InteractorImpl): ${NAME}Interactor
}

@Module
abstract class ${NAME}PresentationModule {
    @Binds
    @IntoMap
    @ViewModelKey(${NAME}ViewModel::class)
    abstract fun bindViewModel(viewModel: ${NAME}ViewModel): ViewModel
}
