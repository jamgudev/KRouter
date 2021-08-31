package com.jamgu.krouter.core.method

/**
 * Created by jamgu on 2021/08/21
 *
 * interface to invoke method
 */
interface IMethodInvoker {

    fun<T> invoke(map: HashMap<Any, Any>?, callback: IMethodCallback<Any>?): T

}