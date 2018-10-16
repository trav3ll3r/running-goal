package au.com.beba.runninggoal.https

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.zip.GZIPInputStream

data class HttpResponse(
        val headers: HttpHeaders,
        val statusCode: String = "0",
        val body: InputStream? = null
) {
    val hasBody: Boolean = body != null
}

/**
 * Builder for [HttpResponse]
 *
 * Only visible withing the package (not meant for external use)
 */
internal class HttpResponseBuilder {
    private var headers: HttpHeaders = HttpHeaders()
    private var responseCode: String = "0"
    private var tempBody: InputStream? = null

    fun withHeaders(headers: HttpHeaders): HttpResponseBuilder {
        this.headers = headers
        return this
    }

    fun withResponseCode(responseCode: String): HttpResponseBuilder {
        this.responseCode = responseCode
        return this
    }

    fun withBody(body: InputStream?): HttpResponseBuilder {
        if (body != null) {
            this.tempBody = copyInputStreamToOfflineStream(body)
        }
        return this
    }

//    fun withBody(body: String): HttpResponseBuilder {
//        this.tempBody = ByteArrayInputStream(body.toByteArray())
//        return this
//    }

    fun build(): HttpResponse {
        val encodingHeader: HttpHeader? = headers.getHeaderByKey("Content-Encoding")

        val body: InputStream? = if (tempBody != null && encodingHeader?.values?.contains("gzip") == true) {
            copyInputStreamToOfflineStream(GZIPInputStream(tempBody))
        } else {
            tempBody
        }

        return HttpResponse(headers, responseCode, body)
    }

    private fun copyInputStreamToOfflineStream(inputStream: InputStream): InputStream {
        val outputBuffer = ByteArrayOutputStream()

        inputStream.use {
            val data = ByteArray(DEFAULT_BUFFER_SIZE)
            var bytesRead = it.read(data, 0, data.size)
            while (bytesRead > 0) {
                outputBuffer.write(data, 0, bytesRead)
                bytesRead = it.read(data, 0, data.size)
            }
            outputBuffer.flush()
            outputBuffer.close()
        }

        return ByteArrayInputStream(outputBuffer.toByteArray())
    }
}