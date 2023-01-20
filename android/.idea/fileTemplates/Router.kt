#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME}
#end

interface ${NAME}Router {
}

class ${NAME}RouterImpl(
    override val fragmentName: String,
    private val navController: NavController
): SafetyRouter(), ${NAME}Router