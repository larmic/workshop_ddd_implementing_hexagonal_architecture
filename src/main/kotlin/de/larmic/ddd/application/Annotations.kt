package de.larmic.ddd.application

import org.springframework.stereotype.Component

/**
 * Marks class as 'use case'.
 * In DDD building block this is a 'service'.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Component
annotation class UseCase
