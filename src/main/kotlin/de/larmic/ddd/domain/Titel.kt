package de.neusta.larmic.ddd.domain

// value object
enum class Titel(val label: String) {

    DR("Dr.");

    companion object Factory {
        fun create(label: String?) = if (label.isNullOrBlank()) {
            null
        } else {
            label.mapToAddition() ?: throw IllegalArgumentException("Person title '$label' is not supported")
        }

        private fun String.mapToAddition() = values().firstOrNull { it.label.lowercase() == this.trimAndLowercase() }
        private fun String.trimAndLowercase() = this.trim { it <= ' ' }.lowercase()
    }
}