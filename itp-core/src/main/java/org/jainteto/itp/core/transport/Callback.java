package org.jainteto.itp.core.transport;

public interface Callback<R, T, E extends Exception> {

    R call(T value) throws E;

}
