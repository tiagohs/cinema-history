@file:OptIn(kotlin.native.runtime.NativeRuntimeApi::class)

package com.tiagohs.cinemahistory.shared.spike

actual object Memoria {
    actual fun coletar() = kotlin.native.runtime.GC.collect()
}
