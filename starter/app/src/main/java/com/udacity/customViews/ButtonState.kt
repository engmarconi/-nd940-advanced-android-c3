package com.udacity.customViews


sealed class ButtonState {
    object Clicked : ButtonState()
    object Loading : ButtonState()
    object Completed : ButtonState()
}