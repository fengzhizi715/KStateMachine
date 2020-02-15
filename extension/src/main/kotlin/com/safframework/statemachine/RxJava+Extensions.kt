package com.safframework.statemachine

import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

/**
 *
 * @FileName:
 *          com.safframework.statemachine.`RxJava+Extensions`
 * @author: Tony Shen
 * @date: 2020-02-15 17:30
 * @version: V1.0 <描述当前版本功能>
 */

@JvmOverloads
@JvmName("toObservable")
fun StateMachine.asObservable(scheduler: Scheduler = Schedulers.io()): Observable<StateMachine> {
    return Observable.create(ObservableOnSubscribe<StateMachine> { emitter -> emitter.onNext(this@asObservable) }).observeOn(scheduler)
}