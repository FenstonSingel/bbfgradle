
private val objectExpression = object {
        inline fun inlineFunction() = Unit
    }
fun run() = 
objectExpression.inlineFunction()
