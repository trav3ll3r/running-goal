package au.com.beba.runninggoal.https

data class HttpRequest(
        val method: String,
        val url: String,
        val headers: HttpHeaders = HttpHeaders(),
        val body: String? = null
)