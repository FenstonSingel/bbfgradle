// Original bug: KT-17267
// Duplicated bug: KT-14612

Exception while analyzing expression at (50,5) in C:/Users/Mikhail.Glukhikh/Projects/ea75803/src/Main.kt:
typealias S = @S Suppress;
: Recursive call in a lazy value under LockBasedStorageManager@ccc318c (<unknown creating class>)
java.lang.IllegalStateException: Recursive call in a lazy value under LockBasedStorageManager@ccc318c (<unknown creating class>)
	at org.jetbrains.kotlin.resolve.lazy.descriptors.LazyAnnotationDescriptor.getType(LazyAnnotations.kt:138)
	at org.jetbrains.kotlin.resolve.TypeAliasExpander.combineAnnotations(TypeAliasExpander.kt:129)
	at org.jetbrains.kotlin.resolve.TypeAliasExpander.expandRecursively(TypeAliasExpander.kt:56)
	at org.jetbrains.kotlin.resolve.TypeAliasExpander.expand(TypeAliasExpander.kt:32)
	at org.jetbrains.kotlin.resolve.TypeResolver.resolveTypeForTypeAlias(TypeResolver.kt:557)
	at org.jetbrains.kotlin.resolve.TypeResolver.resolveTypeForClassifier(TypeResolver.kt:416)
	at org.jetbrains.kotlin.resolve.TypeResolver$resolveTypeElement$1.visitUserType(TypeResolver.kt:234)
	at org.jetbrains.kotlin.psi.KtVisitorVoid.visitUserType(KtVisitorVoid.java:919)
	at org.jetbrains.kotlin.psi.KtVisitorVoid.visitUserType(KtVisitorVoid.java:21)
	at org.jetbrains.kotlin.psi.KtUserType.accept(KtUserType.java:42)
	at org.jetbrains.kotlin.psi.KtElementImplStub.accept(KtElementImplStub.java:58)
	at org.jetbrains.kotlin.resolve.TypeResolver.resolveTypeElement(TypeResolver.kt:216)
	at org.jetbrains.kotlin.resolve.TypeResolver.doResolvePossiblyBareType(TypeResolver.kt:140)
	at org.jetbrains.kotlin.resolve.TypeResolver.resolvePossiblyBareType(TypeResolver.kt:128)
	at org.jetbrains.kotlin.resolve.TypeResolver.resolveType(TypeResolver.kt:103)
	at org.jetbrains.kotlin.resolve.TypeResolver.resolveType(TypeResolver.kt:79)
	at org.jetbrains.kotlin.resolve.AnnotationResolverImpl.resolveAnnotationType(AnnotationResolverImpl.java:116)
	at org.jetbrains.kotlin.resolve.lazy.descriptors.LazyAnnotationDescriptor$type$1.invoke(LazyAnnotations.kt:118)
	at org.jetbrains.kotlin.resolve.lazy.descriptors.LazyAnnotationDescriptor$type$1.invoke(LazyAnnotations.kt:108)
	at org.jetbrains.kotlin.storage.LockBasedStorageManager$LockBasedLazyValue.invoke(LockBasedStorageManager.java:323)
	at org.jetbrains.kotlin.storage.LockBasedStorageManager$LockBasedNotNullLazyValue.invoke(LockBasedStorageManager.java:364)
	at org.jetbrains.kotlin.resolve.lazy.descriptors.LazyAnnotationDescriptor.getType(LazyAnnotations.kt:138)
	at org.jetbrains.kotlin.resolve.lazy.descriptors.LazyAnnotationDescriptor.forceResolveAllContents(LazyAnnotations.kt:160)
...
