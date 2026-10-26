package com.hundredwidgets.core.model

/** A visual family of designs with its own fonts, colours and wallpaper. */
enum class Theme(val id: String, val displayName: String) {
    HORIZON("horizon", "Horizon"),
    NOIR("noir", "Noir"),
    BAUHAUS("bauhaus", "Bauhaus"),
    COMIC("comic", "Comic"),
    // PAPER and SIGNAL are deferred (README §2).
    ;

    companion object {
        fun fromId(id: String): Theme? = entries.firstOrNull { it.id == id }
    }
}
