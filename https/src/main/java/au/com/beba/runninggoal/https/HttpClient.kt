package au.com.beba.runninggoal.https

import kotlinx.coroutines.experimental.Deferred


interface HttpClient {

    fun executeRequest(request: HttpRequest): Deferred<HttpResponse>
}