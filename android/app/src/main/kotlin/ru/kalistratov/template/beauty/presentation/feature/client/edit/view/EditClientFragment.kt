package ru.kalistratov.template.beauty.presentation.feature.client.edit.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.flow.*
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.FragmentListBaseBinding
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.di.UserComponent
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelFactory
import ru.kalistratov.template.beauty.presentation.extension.connect
import ru.kalistratov.template.beauty.presentation.extension.showLoading
import ru.kalistratov.template.beauty.presentation.feature.client.edit.EditClientRouter
import ru.kalistratov.template.beauty.presentation.feature.client.edit.EditClientState
import ru.kalistratov.template.beauty.presentation.feature.client.edit.EditClientViewModel
import ru.kalistratov.template.beauty.presentation.feature.client.edit.di.EditClientModule
import ru.kalistratov.template.beauty.presentation.feature.client.edit.entity.ClientChange
import javax.inject.Inject

sealed interface EditClientIntent : BaseIntent {
    data class DataChanges(val change: ClientChange) : EditClientIntent
    data class InitData(val id: Id?) : EditClientIntent
    object ToPicker : EditClientIntent
    object Save : EditClientIntent
}

class EditClientFragment : BaseFragment(), BaseView<EditClientIntent, EditClientState> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var editClientRouter: EditClientRouter

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[EditClientViewModel::class.java]
    }

    private val binding: FragmentListBaseBinding by viewBinding(CreateMethod.INFLATE)
    private val args by navArgs<EditClientFragmentArgs>()

    private val initDataFlow by lazy { flowOf(EditClientIntent.InitData(args.id)) }

    private val controller by lazy { EditClientController(requireContext()) }

    override fun injectUserComponent(userComponent: UserComponent) = userComponent
        .plus(EditClientModule(this)).inject(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAppBar(
            if (args.id == null) R.string.client_create
            else R.string.client_edit
        )

        binding.recycler.connect(controller)

        controller.buildFinishedUpdates()
            .onEach { showLoading(false) }
            .launchIn(viewModel.viewModelScope)
            .addTo(jobComposite)

        with(viewModel) {
            connectNotifications()
            connectDialogLoadingDisplay()
            router = editClientRouter
            connectInto(this@EditClientFragment)
        }
    }

    override fun onAppBarBackPressed() = editClientRouter.back()

    override fun intents(): Flow<EditClientIntent> = merge(
        initDataFlow,
        controller.saveClicks().map { EditClientIntent.Save },
        controller.dataChanges().map(EditClientIntent::DataChanges),
        controller.toPickerClicks().map { EditClientIntent.ToPicker }
    )

    override fun render(state: EditClientState) {
        with(controller) {
            client = state.client
            controller.requestModelBuild()
        }
    }
}