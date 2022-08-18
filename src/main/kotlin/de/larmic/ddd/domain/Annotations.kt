package de.larmic.ddd.domain

/**
 * Marks given class as DDD building block 'aggregate root'.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class AggregateRoot(val id: String)

/**
 * Marks given class as DDD building block 'entity'.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class Entity

/**
 * Marks given class as DDD building block 'value object'.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class ValueObject
