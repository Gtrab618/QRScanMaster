package com.example.qrscanmaster.extension
fun <T> unsafeLazy(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE,initializer)
