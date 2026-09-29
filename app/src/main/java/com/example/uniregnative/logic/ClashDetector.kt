package com.example.uniregnative.logic

import com.example.uniregnative.data.Course
import com.example.uniregnative.data.TimeSlot

/**
 * A single detected clash between two courses, with the specific
 * overlapping time slots so the UI can highlight exactly which meeting
 * times conflict (matches the "Clash Detection" screen in the Figma spec).
 */
data class Clash(
    val courseA: Course,
    val courseB: Course,
    val slotA: TimeSlot,
    val slotB: TimeSlot,
)

/** Result of running clash detection over a set of courses. */
data class ClashResult(
    val hasClash: Boolean,
    val clashes: List<Clash>,
) {
    /** Courses involved in at least one clash, in no particular order. */
    val affectedCourses: Set<Course>
        get() = clashes.flatMap { listOf(it.courseA, it.courseB) }.toSet()

    companion object {
        val NONE = ClashResult(hasClash = false, clashes = emptyList())
    }
}

/**
 * Core clash-detection algorithm.
 *
 * Compares every pair of courses in [courses] and flags any pair whose
 * weekly time slots overlap. O(n^2) over courses (and their slot counts),
 * which is fine for a student's course list (typically < 10 courses).
 */
object ClashDetector {

    fun detect(courses: List<Course>): ClashResult {
        val clashes = mutableListOf<Clash>()

        for (i in courses.indices) {
            for (j in i + 1 until courses.size) {
                val courseA = courses[i]
                val courseB = courses[j]

                for (slotA in courseA.slots) {
                    for (slotB in courseB.slots) {
                        if (slotA.overlaps(slotB)) {
                            clashes += Clash(courseA, courseB, slotA, slotB)
                        }
                    }
                }
            }
        }

        return if (clashes.isEmpty()) ClashResult.NONE else ClashResult(true, clashes)
    }

    /**
     * Checks whether adding [candidate] to [existing] would introduce any
     * new clash — used for the "add course" flow so the UI can warn before
     * the student commits to adding it.
     */
    fun wouldClash(existing: List<Course>, candidate: Course): ClashResult {
        val clashes = mutableListOf<Clash>()
        for (course in existing) {
            for (slotA in candidate.slots) {
                for (slotB in course.slots) {
                    if (slotA.overlaps(slotB)) {
                        clashes += Clash(candidate, course, slotA, slotB)
                    }
                }
            }
        }
        return if (clashes.isEmpty()) ClashResult.NONE else ClashResult(true, clashes)
    }

    /**
     * Given a course that clashes with the student's current selection,
     * finds alternative course offerings (from [allOptions]) that would
     * NOT clash — powers the "Compare Options / alternative time" flow.
     */
    fun findNonClashingAlternatives(
        existing: List<Course>,
        allOptions: List<Course>,
        excluding: Course,
    ): List<Course> {
        return allOptions
            .filter { it.id != excluding.id }
            .filter { !wouldClash(existing, it).hasClash }
    }
}