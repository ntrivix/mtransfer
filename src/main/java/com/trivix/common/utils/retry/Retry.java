package com.trivix.common.utils.retry;

import java.util.Arrays;
import java.util.function.Supplier;

public class Retry<T> {

    /**
     * Retry to run a function a few times, retry if specific exceptions occur.
     *
     * @param retryOnExceptions what exceptions should lead to retry. Default: any exception
     */
    public static <T> T execute(Supplier<T> function, int maxRetries, long sleep, Class<? extends Exception>... retryOnExceptions) {
        retryOnExceptions = retryOnExceptions.length == 0 ? new Class[]{Exception.class} : retryOnExceptions;
        int retryCounter = 0;
        Exception lastException = null;
        while (retryCounter < maxRetries) {
            try {
                return function.get();
            } catch (Exception e) {
                lastException = e;
                if (Arrays.stream(retryOnExceptions).noneMatch(tClass ->
                        tClass.isAssignableFrom(e.getClass())
                ))
                    throw lastException instanceof RuntimeException ?
                            ((RuntimeException) lastException) :
                            new RuntimeException(lastException);
                else {
                    retryCounter++;
                    if (retryCounter >= maxRetries) {
                        break;
                    }
                }
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        throw lastException instanceof RuntimeException ?
                ((RuntimeException) lastException) :
                new RuntimeException(lastException);
    }
}
