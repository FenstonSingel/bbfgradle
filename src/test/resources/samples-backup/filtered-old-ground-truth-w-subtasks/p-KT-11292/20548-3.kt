// Original bug: KT-20548
// Duplicated bug: KT-20548

import java.lang.annotation.ElementType
import javax.annotation.Nonnull
import javax.annotation.meta.TypeQualifierDefault
import javax.annotation.meta.When

@Nonnull(`when` = When.MAYBE)
@TypeQualifierDefault(ElementType.FIELD // closing bracket ommited 
annotation class MaybeAnn
