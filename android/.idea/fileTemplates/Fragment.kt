#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME}
#end

import javax.inject.Inject

sealed interface ${NAME}Intent: BaseIntent {
    
}

class ${NAME}Fragment : BaseFragment(), BaseView<${NAME}Intent, ${NAME}State> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    
    @Inject
    lateinit var router: ${NAME}Router
    
    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[${NAME}ViewModel::class.java]
    }

    private val binding: Binding by viewBinding(CreateMethod.INFLATE)

    override fun injectUserComponent(userComponent: UserComponent) =
        userComponent.plus(ClientPickerModule(this)).inject(this)
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with (viewModel) {
            router = this@ClientPickerFragment.router
            connectInto(this@ClientPickerFragment)
        }
    }


    override fun intents(): Flow<${NAME}Intent> {
        TODO("Not yet implemented")
    }

    override fun render(state: ${NAME}State) {
        TODO("Not yet implemented")
    }
}