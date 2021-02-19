package com.stepanov.bbf.bugfinder.isolation

class ExcessiveMutationException(msg: String) : Exception(msg)

class NoBugFoundException(msg: String) : IllegalArgumentException(msg)

class PSICreatorException(reason: Throwable) : IllegalArgumentException(reason)
