package com.stepanov.bbf.bugfinder.isolation

//import com.stepanov.bbf.coverage.util.Format
//import kotlinx.serialization.Serializable
//import kotlinx.serialization.cbor.Cbor
//import kotlinx.serialization.json.Json
//import kotlinx.serialization.json.JsonConfiguration
//import kotlinx.serialization.list
//import java.io.File
//
//@Serializable
//class MutationStatistics() {
//
//    companion object {
//
//        private val listSerializer = serializer().list
//        private val json = Json(JsonConfiguration.Stable)
//        private val cbor = Cbor()
//
//        fun serialize(list: List<MutationStatistics>, filePath: String, format: Format = Format.CBOR) {
//            val file = File(filePath)
//            file.delete()
//            when (format) {
//                Format.JSON -> {
//                    file.writeText(json.stringify(listSerializer, list))
//                }
//                Format.CBOR -> {
//                    file.writeBytes(cbor.dump(listSerializer, list))
//                }
//            }
//        }
//
//        fun deserialize(filePath: String, format: Format = Format.CBOR): List<MutationStatistics> {
//            return when (format) {
//                Format.JSON -> {
//                    json.parse(listSerializer, File(filePath).readText())
//                }
//                Format.CBOR -> {
//                    cbor.load(listSerializer, File(filePath).readBytes())
//                }
//            }
//        }
//
//    }
//
//    lateinit var name: String
//
//    constructor(nameInit: String) : this() {
//        name = nameInit
//    }
//
//    val successes = mutableListOf<Double>()
//
//    val failures = mutableListOf<Double>()
//
//}