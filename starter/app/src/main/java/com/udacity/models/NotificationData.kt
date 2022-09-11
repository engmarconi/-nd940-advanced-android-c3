package com.udacity.models

data class NotificationData(
    val title: String,
    val message: String,
    val repositoryName: String,
    val isSuccess: Boolean
)
