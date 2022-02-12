package com.example.mybabelreader.utils.dataclass

import nl.siegmann.epublib.domain.Author
import nl.siegmann.epublib.domain.Resource

data class ManifestData(
    val language: String?,
    val author: List<Author?>?,
    val titles: List<String?>?,
    val image: Resource?
)