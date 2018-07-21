package no.jstien.bikeapi.tsdb.read

class TSDBException : RuntimeException {
    var statusCode: Int = 0
        private set

    constructor(message: String) : super(message) {
        statusCode = -1
    }

    constructor(message: String, statusCode: Int) : super(message) {
        this.statusCode = statusCode
    }
}
