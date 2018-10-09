package au.com.beba.runninggoal.https

class HttpHeaders {
    private val items: MutableList<HttpHeader> = mutableListOf()

    fun addHeader(key: String, vararg values: String): HttpHeaders {
        val existingHeader = getHeaderByKey(key)

        if (existingHeader == null) {
            items.add(HttpHeader(key, values.toMutableList()))
        } else {
            values.forEach {
                if (!existingHeader.values.contains(it)) {
                    existingHeader.values.add(it)
                }
            }

            //TODO: ADD existingHeader BACK INTO ITEMS IF NEEDED
        }

        return this
    }

    val headers: List<HttpHeader>
    get() {
        return items.toList()
    }

    fun getHeaderByKey(key: String): HttpHeader? {
        return items.find { it.key == key }
    }
}

data class HttpHeader(val key: String, val values: MutableList<String>)