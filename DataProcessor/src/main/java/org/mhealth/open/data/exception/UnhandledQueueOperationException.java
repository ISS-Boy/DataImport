package org.mhealth.open.data.exception;

/**
 * Created by dujijun on 2017/10/6.
 */
public class UnhandledQueueOperationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnhandledQueueOperationException() {
        super();
    }

    public UnhandledQueueOperationException(String s) {
        super(s);
    }
}
