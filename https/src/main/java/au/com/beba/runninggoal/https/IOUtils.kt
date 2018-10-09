package au.com.beba.runninggoal.https

import java.io.ByteArrayOutputStream
import java.io.InputStream


internal class IOUtils {

    companion object {
        fun toString(inputStream: InputStream?): String {
            val outputBuffer = ByteArrayOutputStream()

            return inputStream?.use {
                val data = ByteArray(DEFAULT_BUFFER_SIZE)
                var bytesRead = it.read(data, 0, data.size)
                while (bytesRead > 0) {
                    outputBuffer.write(data, 0, bytesRead)
                    bytesRead = it.read(data, 0, data.size)
                }
                outputBuffer.flush()
                outputBuffer.close()

                // RESET THE InputStream's CURSOR IN CASE IT NEEDS TO BE USED ELSEWHERE
                it.reset()

                // CONVERT OUTPUT BUFFER TO BYTE ARRAY AND CREATE STRING FROM IT
                String(outputBuffer.toByteArray(), Charsets.UTF_8)
            } ?: ""
        }
    }
}