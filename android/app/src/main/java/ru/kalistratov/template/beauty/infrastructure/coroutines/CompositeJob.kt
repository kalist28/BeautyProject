package ru.kalistratov.template.beauty.infrastructure.coroutines

import kotlinx.coroutines.Job

class CompositeJob {
    private val jobs = arrayListOf<Job>()

    fun add(job: Job) = jobs.add(job)

    fun cancel() = jobs.forEach { it.cancel() }
}

fun Job.addTo(composite: CompositeJob) = composite.add(this)
