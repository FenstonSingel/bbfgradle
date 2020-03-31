package com.stepanov.bbf.coverage.extraction

import com.stepanov.bbf.coverage.data.*
import org.jacoco.core.analysis.*
import org.jacoco.core.data.ExecutionDataStore
import org.jacoco.core.internal.analysis.CounterImpl
import java.io.File

class CoverageComposer private constructor(
    private val coverageBuilder: CoverageBuilder
) {

    companion object {

        private var noiseType: EntityType? = null

        private val noise = mutableSetOf<String>()

        fun defineNoise(coverage: Coverage) {
            noiseType = coverage.entityType
            for (entity in coverage) {
                noise += entity.name
            }
        }

        private val compilerSources = File("src/main/resources/kotlinc/lib/kotlin-compiler.jar")

        fun buildFrom(executionDataStore: ExecutionDataStore): CoverageComposer {
            val coverageBuilder = CoverageBuilder()
            val analyzer = Analyzer(executionDataStore, coverageBuilder)
            analyzer.analyzeAll(compilerSources)
            return CoverageComposer(coverageBuilder)
        }

        fun composeFrom(executionDataStore: ExecutionDataStore,
                        entityType: EntityType,
                        segmentType: SegmentType,
                        areEmptyEntriesNeeded: Boolean = false): Coverage =
            buildFrom(executionDataStore).compose(entityType, segmentType, areEmptyEntriesNeeded)

        private fun isEntityRelevant(entityName: String): Boolean =
            "org/jetbrains/kotlin/" in entityName

        private fun areTypesCompatible(entityType: EntityType, segmentType: SegmentType): Boolean =
            when (entityType) {
                EntityType.LINES -> segmentType <= SegmentType.LINES
                EntityType.METHODS -> segmentType <= SegmentType.METHODS
                EntityType.CLASSES -> segmentType <= SegmentType.CLASSES
                EntityType.SOURCE_FILES -> segmentType <= SegmentType.CLASSES
            }

        private fun getLineCounter(line: ILine, segmentType: SegmentType): ICounter =
            when (segmentType) {
                SegmentType.INSTRUCTIONS -> line.instructionCounter
                SegmentType.BRANCHES -> line.branchCounter
                SegmentType.LINES -> when (line.status) {
                    ICounter.EMPTY -> CounterImpl.COUNTER_0_0
                    ICounter.NOT_COVERED -> CounterImpl.COUNTER_1_0
                    else -> CounterImpl.COUNTER_0_1
                }
                else -> throw IllegalArgumentException("Only instruction, branch and line counters are available for lines.")
            }

        private fun getCoverageNodeCounter(coverageNode: ICoverageNode, segmentType: SegmentType): ICounter =
            when (segmentType) {
                SegmentType.INSTRUCTIONS -> coverageNode.instructionCounter
                SegmentType.BRANCHES -> coverageNode.branchCounter
                SegmentType.LINES -> coverageNode.lineCounter
                SegmentType.PATHS -> coverageNode.complexityCounter
                SegmentType.METHODS -> coverageNode.methodCounter
                SegmentType.CLASSES -> coverageNode.classCounter
            }

    }

    private fun isEntityNoise(name: String, entityType: EntityType): Boolean =
        entityType == noiseType && name in noise

    private fun getCounters(entityType: EntityType, segmentType: SegmentType): Map<String, ICounter> {
        val map = mutableMapOf<String, ICounter>()
        when (entityType) {
            EntityType.LINES -> {
                for (classCoverage in coverageBuilder.classes) {
                    for (methodCoverage in classCoverage.methods) {
                        for (index in methodCoverage.firstLine..methodCoverage.lastLine) {
                            val line = methodCoverage.getLine(index)
                            val name = "${classCoverage.name}/${methodCoverage.name}${methodCoverage.desc}/$index"
                            map[name] = getLineCounter(line, segmentType)
                        }
                    }
                }
            }
            EntityType.METHODS -> {
                for (classCoverage in coverageBuilder.classes) {
                    for (methodCoverage in classCoverage.methods) {
                        val name = "${classCoverage.name}/${methodCoverage.name}${methodCoverage.desc}"
                        map[name] = getCoverageNodeCounter(methodCoverage, segmentType)
                    }
                }
            }
            EntityType.CLASSES -> {
                for (classCoverage in coverageBuilder.classes) {
                    val name = classCoverage.name
                    map[name] = getCoverageNodeCounter(classCoverage, segmentType)
                }
            }
            EntityType.SOURCE_FILES -> {
                for (sourceFileCoverage in coverageBuilder.sourceFiles) {
                    val name = "${sourceFileCoverage.packageName}/${sourceFileCoverage.name}"
                    map[name] = getCoverageNodeCounter(sourceFileCoverage, segmentType)
                }
            }
        }
        return map
    }

    fun compose(entityType: EntityType, segmentType: SegmentType, areEmptyEntriesNeeded: Boolean = true): Coverage {
        if (!areTypesCompatible(entityType, segmentType)) {
            throw IllegalArgumentException("$segmentType info is not available for $entityType entities.")
        }

        val coverage = Coverage()
        coverage.entityType = entityType
        coverage.segmentType = segmentType
        for ((name, counter) in getCounters(entityType, segmentType)) {
            if (!isEntityRelevant(name) || isEntityNoise(name, entityType)) continue

            val coveredCount = counter.coveredCount
            val totalCount = counter.totalCount

            if (totalCount == 0) continue
            if (!areEmptyEntriesNeeded && coveredCount == 0) continue

            coverage += EntityCoverage(
                name,
                coveredCount,
                totalCount
            )
        }
        return coverage
    }

}