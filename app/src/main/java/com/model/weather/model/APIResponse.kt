package com.model.weather.model

data class APIResponse<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?): APIResponse<T> {
            //Log.e("data", data.toString())
            return APIResponse(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): APIResponse<T> {
            return APIResponse(Status.ERROR, data, msg)
        }

        fun <T> loading(data: T?): APIResponse<T> {
            return APIResponse(Status.LOADING, data, null)
        }
    }

    enum class Status {
        SUCCESS, ERROR, LOADING
    }
}