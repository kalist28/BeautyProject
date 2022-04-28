package ru.kalistratov.template.beauty.infrastructure.coroutines

import kotlinx.coroutines.Job
import ru.kalistratov.template.beauty.infrastructure.extensions.loge

class CompositeJob {
    private val jobs = mutableListOf<Job>()

    fun add(job: Job) = jobs.add(job)

    fun cancel() = jobs.forEach { loge("CANCLE")
        it.cancel() }
}

fun Job.addTo(composite: CompositeJob) = composite.add(this)
