package no.jstien.bikeapi.utils

object VarUtils {
    fun getEnv(key: String): String? {
        var tmp: String? = null
        tmp = System.getProperty(key)
        if (tmp != null) {
            return tmp
        }

        tmp = System.getenv(key)
        return tmp
    }
}
