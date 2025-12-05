package com.example.myspamfilterapp


object SpamLogger {
    private val logList = mutableListOf<String>()

    init {
        System.loadLibrary("spam_logger")
    }


    fun logNumber(entry: String, path: String){
        logList.add(entry)

        try {
            logNumberRust(entry, path)
        } catch (e: Exception){
            e.printStackTrace()
        }
    }


    // Get a copy of current entries (for Compose)
    fun getLog(): List<String> = logList.toList()
    private external fun logNumberRust(entry: String, path: String)
}

