package com.tiagohs.cinemahistory.shared.spike

actual object Memoria {
    actual fun coletar() = System.gc()
}
