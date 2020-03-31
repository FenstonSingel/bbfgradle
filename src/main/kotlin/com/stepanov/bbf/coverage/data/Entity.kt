package com.stepanov.bbf.coverage.data

import kotlinx.serialization.Serializable

@Serializable
class Entity() {

    lateinit var name: String

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Entity

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    constructor(
        name: String
    ) : this() {
        this.name = name
    }

    fun copy(name: String = this.name): Entity =
        Entity(name)

    override fun toString(): String =
        name

}