package com.stepanov.bbf.coverage.analysis

import java.lang.Double.NaN

class RankedEntity() {

    lateinit var name: String

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RankedEntity

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    var rank: Double = NaN // TODO check if this is where all the NaNs for Ochiai2 metric comes from

    constructor(
        name: String,
        rank: Double
    ) : this() {
        this.name = name
        this.rank = rank
    }

    fun copy(name: String = this.name,
             rank: Double = this.rank): RankedEntity =
        RankedEntity(name, rank)

    override fun toString(): String =
        "entity $name; ranked $rank"

}