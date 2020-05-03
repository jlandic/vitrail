package com.wilgig.vitrail.extensions

import kotlin.random.Random

/**
 * @param list the list from which a random element is picked
 *
 * @return a random element from `list`, or `null` if `list` is empty
 * @author @jlandic
 */
fun <T> Random.elementFromList(list: List<T>): T? = if (list.isEmpty()) null else list[nextInt(list.size)]
