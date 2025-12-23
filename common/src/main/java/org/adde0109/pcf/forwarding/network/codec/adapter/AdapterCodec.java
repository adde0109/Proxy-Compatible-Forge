package org.adde0109.pcf.forwarding.network.codec.adapter;

import java.lang.reflect.ParameterizedType;

@SuppressWarnings("unchecked")
public interface AdapterCodec<M, V> extends AdapterDecoder<M, V>, AdapterEncoder<M, V> {
    default Class<M> mcClass() {
        return (Class<M>)
                ((ParameterizedType) getClass().getGenericInterfaces()[0])
                        .getActualTypeArguments()[0];
    }

    default Class<V> objClass() {
        return (Class<V>)
                ((ParameterizedType) getClass().getGenericInterfaces()[0])
                        .getActualTypeArguments()[1];
    }
}
