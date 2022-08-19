package de.neusta.larmic.ddd.domain

// value object
enum class Namenszusatz(val label: String) {

    VON("von"),
    VAN("van"),
    DE("de");

    companion object Factory {
        fun create(label: String?) = if (label.isNullOrBlank()) {
            null
        } else {
            label.mapToAddition() ?: throw IllegalArgumentException("Person addition '$label' is not supported")
        }

        private fun String.mapToAddition() = values().firstOrNull { it.label == this.trimAndLowercase() }
        private fun String.trimAndLowercase() = this.trim { it <= ' ' }.lowercase()
    }
}