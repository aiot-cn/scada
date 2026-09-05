
package org.aiot.infc;

@FunctionalInterface
public interface CallBack<T1,T2,R> {

    R apply(T1 t,T2 t2);

}
