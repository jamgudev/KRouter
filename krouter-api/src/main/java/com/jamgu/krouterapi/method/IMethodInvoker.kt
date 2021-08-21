package com.jamgu.krouterapi.method

/**
 * Created by jamgu on 2021/08/21
 */
interface IMethodInvoker {

    fun<T> invoke(map: HashMap<Any, Any>, callback: IMethodCallback<*>): T

}