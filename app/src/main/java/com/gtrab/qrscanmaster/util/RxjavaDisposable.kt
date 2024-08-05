package com.gtrab.qrscanmaster.util

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

fun Disposable.addTo(compositeDisposable: CompositeDisposable): io.reactivex.rxjava3.disposables.Disposable =
    apply { compositeDisposable.add(this) }