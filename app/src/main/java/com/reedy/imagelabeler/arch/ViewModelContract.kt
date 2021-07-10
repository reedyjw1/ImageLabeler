package com.reedy.imagelabeler.arch

interface ViewModelContract<T: ViewStateEvent> {
    fun process(event: T)
}