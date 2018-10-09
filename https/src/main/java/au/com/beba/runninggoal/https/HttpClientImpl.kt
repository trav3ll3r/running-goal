package au.com.beba.runninggoal.https

import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.coroutines.experimental.CoroutineContext


class HttpClientImpl
constructor(private val networkingContext: CoroutineContext = DefaultDispatcher)
    : HttpClient {

    companion object {
        private val TAG = HttpClientImpl::class.java.simpleName
    }

    private val logger: Logger = LoggerFactory.getLogger(TAG)
    private val client: OkHttpClient = OkHttpClient.Builder().cache(null).build()

    override fun executeRequest(request: HttpRequest): Deferred<HttpResponse> = async(networkingContext) {
        logRequest(request)
        val r: Response? = client.newCall(mapToRequest(request)).execute()
        mapToResponse(r)
    }

    private fun mapToRequest(request: HttpRequest): Request {
        val b = Request.Builder()
                .method(request.method, null)
                .url(request.url)
        request.headers.headers.forEach { b.addHeader(it.key, it.values[0]) }

        return b.build()
    }

    private fun mapToResponse(response: Response?): HttpResponse {
        val httpResponse = if (extractBody(response)) {
            val h = HttpHeaders()
            val r = response!!
            r.headers().toMultimap().asSequence().map { h.addHeader(it.key, *it.value.toTypedArray()) }

            HttpResponseBuilder()
                    .withHeaders(h)
                    .withResponseCode(r.code().toString())
                    .withBody(r.body()?.byteStream())
                    .build()
        } else {
            HttpResponseBuilder()
                    .withBody(null)
                    .withResponseCode(response?.code().toString())
                    .build()
        }

        logResponse(httpResponse)
        return httpResponse
    }

    private fun extractBody(response: Response?): Boolean {
        return (response != null && response.code() == 200)
    }

    private fun logRequest(request: HttpRequest) {
        logger.debug("== REQUEST ==")
        logger.debug("%s %s".format(request.method, request.url))
        request.headers.headers.forEach { logger.debug("header: %s".format("%s %s".format(it.key, it.values))) }
        logger.debug("body: %s".format(request.body ?: "NULL"))
    }

    private fun logResponse(response: HttpResponse) {
        logger.debug("== RESPONSE ==")
        logger.debug("code: %s".format(response.statusCode))
        logger.debug("body: %s".format(IOUtils.toString(response.body)))
    }
}