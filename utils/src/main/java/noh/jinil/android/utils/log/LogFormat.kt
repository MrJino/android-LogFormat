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

    @JvmStatic
    fun initialize(debuggable: Boolean) {
        enable = debuggable
    }

    @JvmStatic
    fun w(tag: String?, log: String?) {
        if (!enable || log == null) {
            return
        }
        Log.w(MAIN_TAG, "[$tag] $log")
    }

    @JvmStatic
    fun i(tag: String?, log: String?) {
        if (!enable || log == null) {
            return
        }
        Log.i(MAIN_TAG, "[$tag] $log")
    }

    @JvmStatic
    fun d(tag: String?, log: String?) {
        if (!enable || log == null) {
            return
        }
        Log.d(MAIN_TAG, "[$tag] $log")
    }

    @JvmStatic
    fun v(log: String?) {
        if (!enable || log == null) {
            return
        }
        Log.v(MAIN_TAG, "  -> $log")
    }

    @JvmStatic
    fun l(tag: String?, log: String?) {
        if (!enable || log == null) {
            return
        }
        Log.v(MAIN_TAG, " => $log in [$tag]")
    }

    @JvmStatic
    fun e(log: String?) {
        if (!enable || log == null) {
            return
        }
        Log.e(MAIN_TAG, log)
    }

    @JvmStatic
    fun send(data: String) {
        printJson("Send", data)
    }

    /**
     * Json 데이터 파싱
     */
    @JvmStatic
    fun receive(data: String) {
        printJson("Receive", data)
    }

    @JvmStatic
    fun httpResponse(message: String) {
        try {
            message.toByteArray(Charsets.UTF_8)

            when {
                message.startsWith("-->") || message.startsWith("<--") -> {
                    Log.d(BODY_TAG, message)
                }
                message.startsWith("{") || message.startsWith("[") -> {
                    printJson("HTTP", message)
                }
                else ->
                    Log.d(HEAD_TAG, message)
            }
        } catch (e: Exception) {
            Log.d(MAIN_TAG, "binary data!!")
        }
    }

    private fun printJson(title: String, data: String) {
        val builder = StringBuilder()
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

        builder.appendLine()
        builder.appendLine(delimiter1)
        when (json) {
            is JSONObject -> handleObject(json)
            is JSONArray -> handleArray(json)
            else -> null
        }?.let {
            builder.append(it)
        }
        builder.appendLine(delimiter2)
        Log.v(BODY_TAG, builder.toString())
    }

    private fun handleObject(obj: JSONObject?, blank: String = ""): String? {
        obj ?: return null
        val builder = StringBuilder()

        obj.keys().forEach { key ->
            obj[key].let { value->
                when (value) {
                    is JSONObject -> {
                        builder.appendLine("|$blank $key: {")
                        builder.append(handleObject(value, "$blank  "))
                        builder.appendLine("|$blank }")
                    }
                    is JSONArray -> {
                        if (value.length() == 0) {
                            builder.appendLine("|$blank \"$key\": []")
                        } else {
                            builder.appendLine("|$blank \"$key\":")
                            builder.append(handleArray(value, blank))
                        }
                    }
                    else -> {
                        builder.appendLine("|$blank \"$key\": \"$value\"")
                    }
                }
            }
        }
        return builder.toString()
    }

    private fun handleArray(array: JSONArray, blank: String = ""): String {
        val builder = StringBuilder()

        fun handleOneLine() {
            builder.append("|$blank - [")
            repeat(array.length()) { i ->
                builder.append("\"${array.get(i)}\"")
                if (i != array.length() - 1) {
                    builder.append(", ")
                }
            }
            builder.appendLine("]")
        }

        fun handleSeparate() {
            builder.appendLine("|  $blank[")
            repeat(array.length()) { i ->
                when (val item = array.get(i)) {
                    is JSONObject -> {
                        builder.appendLine("|  $blank{")
                        builder.append(handleObject(item, "$blank   "))
                        builder.appendLine("|  $blank}")
                    }
                    else -> {
                        builder.appendLine("|  $blank \"$item\"")
                    }
                }
            }
            builder.appendLine("|  $blank]")
        }

        when (val sample = array.get(0)) {
            is Int, is Float -> {
                handleOneLine()
            }
            is String -> {
                if (sample.length < 20) {
                    handleOneLine()
                } else {
                    handleSeparate()
                }
            }
            else -> {
                handleSeparate()
            }
        }
        return builder.toString()
    }
}