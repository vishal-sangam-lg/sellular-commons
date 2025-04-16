package com.sellular.commons.jpa.constants;

public enum QueryTimeout {
    DEFAULT( 15000 ), // 15 seconds
    BACKGROUND( 60000 ), // 1 minute
    BULK( 1800000 ); // 30 minutes

    private final int timeoutSeconds;

    QueryTimeout ( int timeoutSeconds ) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public int getTimeoutSeconds () {
        return timeoutSeconds;
    }

}
