package com.jamgu.krouterapi.method

/**
 * Created by jamgu on 2021/08/21
 */
interface IMethodCallback<T> {

    fun onInvokeFinish(t: T)

}