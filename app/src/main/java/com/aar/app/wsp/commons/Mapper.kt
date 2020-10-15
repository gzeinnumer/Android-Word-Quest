package com.aar.app.wsp.commons

/**
 * Created by abdularis on 08/07/17.
 *
 * Base class for type mapper
 */
abstract class Mapper<From, To> {

    abstract fun map(obj: From): To

    abstract fun revMap(obj: To): From

    fun map(objectList: List<From>?): List<To>? {
        if (objectList == null) return null
        val result: MutableList<To> = ArrayList()
        for (obj in objectList) result.add(map(obj))
        return result
    }

    fun revMap(objectList: List<To>?): List<From>? {
        if (objectList == null) return null
        val result: MutableList<From> = ArrayList()
        for (obj in objectList) result.add(revMap(obj))
        return result
    }
}