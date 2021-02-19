// Original bug: KT-18389
// Duplicated bug: KT-18050

class FakeMemberCodegen(
            private val delegate: MemberCodegen<*>,
            declaration: KtElement,
            codegenContext: FieldOwnerContext<*>,
            private val className: String
    ) : org.jetbrains.kotlin.codegen.MemberCodegen<*>(delegate, declaration, codegenContext) {

        override fun generateDeclaration() {
            throw IllegalStateException()
        }

        override fun generateBody() {
            throw IllegalStateException()
        }

        override fun generateKotlinMetadataAnnotation() {
            throw IllegalStateException()
        }

        override fun getInlineNameGenerator(): NameGenerator {
            return delegate.inlineNameGenerator
        }

        override //TODO: obtain name from context
        fun getClassName(): String {
            return className
        }
    }

