package com.stepanov.bbf.coverage

import com.stepanov.bbf.coverage.impl.BranchBasedCoverage
import com.stepanov.bbf.coverage.impl.MethodBasedCoverage
import kotlinx.serialization.UpdateMode
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.modules.SerializersModule

val coverageSerializationFormat = Cbor(
        updateMode = UpdateMode.BANNED,
        encodeDefaults = true,
        context = SerializersModule {
            polymorphic(ProgramCoverage::class) {
                addSubclass(MethodBasedCoverage.serializer())
                addSubclass(BranchBasedCoverage.serializer())
            }
        }
)