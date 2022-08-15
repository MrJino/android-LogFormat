@file:Suppress("unused")

package noh.jinil.android.utils.log

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

object LogFormat {
    private const val MAIN_TAG = "LogFormat_MAIN"
    private const val BODY_TAG = "LogFormat_BODY"
    private const val HEAD_TAG = "LogFormat_HEAD"

    private var enable: Boolean = true

    fun initialize(debuggable: Boolean) {
        enable = debuggable
    }

    fun w(tag: String?, log: String?) {
        if (!enable || log == null) {
            return
        }
        Log.w(MAIN_TAG, "[$tag] $log")
    }

    fun i(tag: String?, log: String?) {
        if (!enable || log == null) {
            return
        }
        Log.i(MAIN_TAG, "[$tag] $log")
    }

    fun d(tag: String?, log: String?) {
        if (!enable || log == null) {
            return
        }
        Log.d(MAIN_TAG, "[$tag] $log")
    }

    fun v(log: String?) {
        if (!enable || log == null) {
            return
        }
        Log.v(MAIN_TAG, "  -> $log")
    }

    fun l(tag: String?, log: String?) {
        if (!enable || log == null) {
            return
        }
        Log.v(MAIN_TAG, " => $log in [$tag]")
    }

    fun e(log: String?) {
        if (!enable || log == null) {
            return
        }
        Log.e(MAIN_TAG, log)
    }

    fun send(data: String) {
        json("Send", data)
    }

    fun receive(data: String) {
        json("Receive", data)
    }

    fun httpResponse(message: String) {
        try {
            message.toByteArray(Charsets.UTF_8)

            when {
                message.startsWith("-->") || message.startsWith("<--") -> {
                    Log.d(BODY_TAG, message)
                }
                message.startsWith("{") || message.startsWith("[") -> {
                    json("HTTP", message)
                }
                else ->
                    Log.d(HEAD_TAG, message)
            }
        } catch (e: Exception) {
            Log.d(MAIN_TAG, "binary data!!")
        }
    }

    private fun json(title: String, data: String) {
        if (!enable) {
            return
        }

        val json = try {
            JSONObject(data)
        } catch (e: Exception) {
            JSONArray(data)
        }

        val delimiter1 = " -------| $title |-----------------------------------------------------------"
        val delimiter2 = delimiter1.replace("[^-]".toRegex(), "-")

        Log.v(BODY_TAG, delimiter1)
        when (json) {
            is JSONObject -> handleObject(json)
            is JSONArray -> handleArray(json)
        }
        Log.v(BODY_TAG, delimiter2)
    }

    private fun handleObject(obj: JSONObject?, blank: String = "") {
        obj ?: return

        obj.keys().forEach { key ->
            obj[key].let { value->
                when (value) {
                    is JSONObject -> {
                        Log.v(BODY_TAG, "|$blank $key: {")
                        handleObject(value, "$blank  ")
                        Log.v(BODY_TAG, "|$blank }")
                    }
                    is JSONArray -> {
                        if (value.length() == 0) {
                            Log.v(BODY_TAG, "|$blank \"$key\": []")
                        } else {
                            Log.v(BODY_TAG, "|$blank \"$key\":")
                            handleArray(value, blank)
                        }
                    }
                    else -> {
                        Log.v(BODY_TAG, "|$blank \"$key\": \"$value\"")
                    }
                }
            }
        }

    }

    private fun handleArray(array: JSONArray, blank: String = "") {
        Log.v(BODY_TAG, "| $blank[")
        repeat(array.length()) { i ->
            when (val item = array.get(i)) {
                is JSONObject -> {
                    Log.v(BODY_TAG, "|  $blank{")
                    handleObject(item, "$blank   ")
                    Log.v(BODY_TAG, "|  $blank}")
                }
                else -> {
                    Log.v(BODY_TAG, "|  $blank \"$item\"")
                }
            }
        }
        Log.v(BODY_TAG, "| $blank]")
    }
}