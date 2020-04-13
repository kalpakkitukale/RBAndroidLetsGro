package com.ramanbyte.data_layer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object CoroutineUtils {

    fun main(work : suspend ( () -> Unit )) =
        CoroutineScope(Dispatchers.Unconfined).launch {
            work()
        }
}