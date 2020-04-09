package com.wilgig.vitrail.extensions

import kotlin.random.Random

fun <T> Random.elementFromList(list: List<T>): T? = if (list.isEmpty()) null else list[nextInt(list.size)]
