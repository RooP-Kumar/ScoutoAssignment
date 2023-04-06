package com.scouto.assignment.data.model.makes

data class Makes(
    val Count: Int,
    val Message: String,
    val Results: List<AllMakesResult>,
    val SearchCriteria: String?
)