#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME}
#end
#parse("File Header.java")

data class ${NAME}State() : BaseState

sealed class ${NAME}Action : BaseAction

class ${NAME}ViewModel : BaseViewModel<${NAME}Intent, ${NAME}Action, ${NAME}State> {
    override fun reduce(state: ${NAME}State, action: ${NAME}Action): ${NAME}State {
        TODO("Method reduce in ${NAME}ViewModel is not yet implemented")
    }
}
