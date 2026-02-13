package no.badevann.scraper

import java.text.Normalizer
import java.util.Locale

object SlugUtils {
    fun toSlug(input: String): String {
        val normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
        return normalized
            .replace("æ", "ae").replace("Æ", "ae")
            .replace("ø", "oe").replace("Ø", "oe")
            .replace("å", "aa").replace("Å", "aa")
            .replace(Regex("[\\p{InCombiningDiacriticalMarks}]"), "")
            .lowercase(Locale("no", "NO"))
            .replace(Regex("[^a-z0-9\\s-]"), "")
            .trim()
            .replace(Regex("[\\s]+"), "-")
            .replace(Regex("-+"), "-")
    }
}
