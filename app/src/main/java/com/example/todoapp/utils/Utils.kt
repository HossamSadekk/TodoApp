package com.example.todoapp.utils

/**
 * This property has a custom getter which returns the object itself,
 * so if we use it on a when block, it’s treated as an expression and the compiler will force us to specify all cases.
 * **/
val <T> T.exhaustive: T
    get() = this