package org.densebrain.tools.ko

typealias KOLoggerProvider = (msg: String, cause: Throwable?) -> Unit

interface KOLogger {
  operator fun invoke(msg: String, cause: Throwable? = null)

  companion object {

    var provider: KOLoggerProvider? = null

    val logger: KOLogger = object : KOLogger {
      override fun invoke(msg: String, cause: Throwable?) {
        val provider = provider
        if (provider != null) {
          provider(msg, cause)
          return
        }

        System.out.println(msg)
        if (cause != null) {
          cause.printStackTrace()
        }
      }
    }
  }
}

fun KOLogger() = KOLogger.logger

interface KOLoggable

fun KOLoggable.log(msg: String, cause: Throwable? = null) {
  KOLogger.logger(msg,cause)
}
