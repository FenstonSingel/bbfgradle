package com.stepanov.bbf.coverage

// TODO Serialization of a new coverage implementation.

//import com.stepanov.bbf.coverage.data.Coverage

//private val json = Json(JsonConfiguration.Stable)
//private val cbor = Cbor()
//private val defaultSerializer = Coverage.serializer().list
//private val mutantsSerializer = MutantCoverages.serializer().list
//
//enum class Format {
//    JSON,
//    CBOR;
//
//}
//
//fun serializeCoverage(coverage: Coverage, coverageFilePath: String, format: Format = Format.CBOR) {
//    serializeCoverage(listOf(coverage), coverageFilePath, format)
//}
//
//fun serializeCoverage(coverages: List<Coverage>, coverageFilePath: String, format: Format = Format.CBOR) {
//    val file = File(coverageFilePath)
//    file.delete()
//    when (format) {
//        Format.JSON -> {
//            file.writeText(json.stringify(defaultSerializer, coverages))
//        }
//        Format.CBOR -> {
//            file.writeBytes(cbor.dump(defaultSerializer, coverages))
//        }
//    }
//}
//
//fun deserializeCoverage(coverageFilePath: String, format: Format = Format.CBOR): List<Coverage> {
//    return when (format) {
//        Format.JSON -> {
//            json.parse(defaultSerializer, File(coverageFilePath).readText())
//        }
//        Format.CBOR -> {
//            cbor.load(defaultSerializer, File(coverageFilePath).readBytes())
//        }
//    }
//}
//
//fun serializeMutantCoverages(coverage: MutantCoverages, coverageFilePath: String, format: Format = Format.CBOR) {
//    serializeMutantCoverages(listOf(coverage), coverageFilePath, format)
//}
//
//fun serializeMutantCoverages(coverages: List<MutantCoverages>, coverageFilePath: String, format: Format = Format.CBOR) {
//    val file = File(coverageFilePath)
//    file.delete()
//    when (format) {
//        Format.JSON -> {
//            file.writeText(json.stringify(mutantsSerializer, coverages))
//        }
//        Format.CBOR -> {
//            file.writeBytes(cbor.dump(mutantsSerializer, coverages))
//        }
//    }
//}
//
//fun deserializeMutantCoverages(coverageFilePath: String, format: Format = Format.CBOR): List<MutantCoverages> {
//    return when (format) {
//        Format.JSON -> {
//            json.parse(mutantsSerializer, File(coverageFilePath).readText())
//        }
//        Format.CBOR -> {
//            cbor.load(mutantsSerializer, File(coverageFilePath).readBytes())
//        }
//    }
//}
