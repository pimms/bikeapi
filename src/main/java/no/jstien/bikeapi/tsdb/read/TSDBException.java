package no.jstien.bikeapi.tsdb.read;

public class TSDBException extends RuntimeException {
    private int statusCode;

    public TSDBException(String message) {
        super(message);
        statusCode = -1;
    }

    public TSDBException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
