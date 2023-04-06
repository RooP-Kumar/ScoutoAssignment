package com.scouto.assignment.data.model.singlemake

data class SingleMake(
    val Count: Int,
    val Message: String,
    val Results: List<SingleMakeResult>,
    val SearchCriteria: String
)