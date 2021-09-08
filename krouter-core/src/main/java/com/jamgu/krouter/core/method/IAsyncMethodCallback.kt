package com.jamgu.krouter.core.method

/**
 * Created by jamgu on 2021/08/21
 *
 * a callback interface that will be called after method invoked.
 *
 * T: result's type you want.
 */
interface IAsyncMethodCallback<T> {

    fun onInvokeFinish(data: T)

}