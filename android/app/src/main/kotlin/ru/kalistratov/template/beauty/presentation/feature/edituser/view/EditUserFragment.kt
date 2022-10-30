package ru.kalistratov.template.beauty.presentation.feature.edituser.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.databinding.FragmentEditUserBinding
import ru.kalistratov.template.beauty.infrastructure.di.UserComponent
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelFactory
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.presentation.extension.clicks
import ru.kalistratov.template.beauty.presentation.feature.edituser.EditUserState
import ru.kalistratov.template.beauty.presentation.feature.edituser.EditUserViewModel
import ru.kalistratov.template.beauty.presentation.feature.edituser.di.EditUserModule
import ru.kalistratov.template.beauty.presentation.feature.edituser.entity.EditUserData
import ru.kalistratov.template.beauty.presentation.feature.edituser.entity.EditUserListItemType
import javax.inject.Inject

sealed interface EditUserIntent : BaseIntent {
    object InitData : EditUserIntent
    object BackPressed : EditUserIntent

    data class DataChanges(val data: EditUserData) : EditUserIntent
    data class ButtonClick(val type: EditUserListItemType) : EditUserIntent
}

class EditUserFragment : BaseFragment(),
    BaseView<EditUserIntent, EditUserState> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: FragmentEditUserBinding

    private val controller = EditUserItemsController()

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[EditUserViewModel::class.java]
    }

    override fun injectUserComponent(userComponent: UserComponent) =
        userComponent.plus(EditUserModule(this)).inject(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentEditUserBinding
        .inflate(inflater, container, false)
        .let {
            binding = it
            it.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.apply {
            adapter = controller.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        with(viewModel) {
            viewModelScope.launch {
                stateUpdates()
                    .collect(::render)
            }.addTo(jobComposite)
            processIntent(intents())
        }
    }

    override fun intents(): Flow<EditUserIntent> = merge(
        flowOf(EditUserIntent.InitData),
        controller.dataUpdates.map { EditUserIntent.DataChanges(it) },
        controller.buttonClicks.map { EditUserIntent.ButtonClick(it) },
        binding.upBar.backButton.clicks().map { EditUserIntent.BackPressed }
    )

    override fun render(state: EditUserState) {
        with(controller) {
            items = state.settingItems
            itemsData = state.settingData
            allowSaveChanges = state.allowSaveChanges
            requestModelBuild()
        }
    }
}
