package com.example.uniregnative.data

/**
 * Mock course catalog for development/testing before a real backend or
 * database is wired in. Includes a deliberate clash (CS3060 vs CS3021,
 * both Mon 09:00-10:30) so you can exercise the clash-detection flow
 * immediately, matching the scenario used in your usability testing.
 */
object SampleData {

    val catalog: List<Course> = listOf(
        Course(
            id = "CS3060",
            title = "Human-Computer Interaction",
            instructor = "Dr. A. Perera",
            credits = 3,
            slots = listOf(
                TimeSlot.of(DayOfWeek.MON, "09:00", "10:30"),
                TimeSlot.of(DayOfWeek.WED, "09:00", "10:30"),
            ),
            capacity = 60,
            enrolled = 45,
        ),
        Course(
            id = "CS3021",
            title = "Database Systems",
            instructor = "Dr. N. Silva",
            credits = 3,
            slots = listOf(
                TimeSlot.of(DayOfWeek.MON, "09:00", "10:30"), // clashes with CS3060
                TimeSlot.of(DayOfWeek.THU, "13:00", "14:30"),
            ),
            capacity = 55,
            enrolled = 30, //
        ),
        Course(
            id = "CS3021-ALT",
            title = "Database Systems (Alt. Section)",
            instructor = "Dr. N. Silva",
            credits = 3,
            slots = listOf(
                TimeSlot.of(DayOfWeek.TUE, "11:00", "12:30"), // no clash with CS3060
                TimeSlot.of(DayOfWeek.THU, "13:00", "14:30"),
            ),
            capacity = 50,
            enrolled = 20,
        ),
        Course(
            id = "CS3042",
            title = "Software Engineering",
            instructor = "Dr. R. Fernando",
            credits = 3,
            slots = listOf(
                TimeSlot.of(DayOfWeek.TUE, "14:00", "15:30"),
                TimeSlot.of(DayOfWeek.FRI, "14:00", "15:30"),
            ),
            capacity = 55,
            enrolled = 30,
        ),
        Course(
            id = "CS3015",
            title = "Mobile Application Development",
            instructor = "Dr. K. Jayasuriya",
            credits = 3,
            slots = listOf(
                TimeSlot.of(DayOfWeek.WED, "11:00", "12:30"),
            ),
            capacity = 40,
            enrolled = 18,
        ),
    )
}