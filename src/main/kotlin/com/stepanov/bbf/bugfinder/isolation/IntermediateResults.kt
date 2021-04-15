package com.stepanov.bbf.bugfinder.isolation

import com.stepanov.bbf.coverage.ProgramCoverage
import com.stepanov.bbf.coverage.coverageSerializationFormat
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.Cbor
import java.io.File
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

@Serializable
data class MutantsForIsolation(
        val exportTag: String, // for the purposes of following serializations
        val originalSample: String,
        val mutants: List<String>
) {
    fun export(filePath: String) {
        File(filePath).writeBytes(Cbor().dump(serializer(), this))
    }

    companion object {
        fun import(filePath: String): MutantsForIsolation {
            return Cbor().load(serializer(), File(filePath).readBytes())
        }
    }
}

@Serializable
data class CoveragesForIsolation(
        val exportTag: String, // for the purposes of following serializations
        val originalSampleCoverage: ProgramCoverage,
        val mutantsWithBugCoverages: List<ProgramCoverage>,
        val mutantsWithoutBugCoverages: List<ProgramCoverage>
) {
    fun export(filePath: String) {
        File(filePath).writeBytes(coverageSerializationFormat.dump(serializer(), this))
    }

    fun exportCompressed(filePath: String) {
        GZIPOutputStream(File(filePath).outputStream()).use {
            it.write(coverageSerializationFormat.dump(serializer(), this))
        }
    }

    companion object {
        fun import(filePath: String): CoveragesForIsolation {
            return coverageSerializationFormat.load(
                serializer(),
                File(filePath).readBytes()
            )
        }

        fun importCompressed(filePath: String): CoveragesForIsolation {
            return coverageSerializationFormat.load(
                serializer(),
                GZIPInputStream(File(filePath).inputStream()).use { it.readBytes() }
            )
        }
    }
}