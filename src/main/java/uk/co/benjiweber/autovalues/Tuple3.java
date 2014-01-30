package uk.co.benjiweber.autovalues;

public interface Tuple3<T,U,V> extends HasTuple3<T,U,V> {
    default T first() { return values().first(); };
    default U second() { return values().second(); };
    default V third() { return values().third(); };

    public static <T,U,V> Tuple3<T,U,V> create(T t, U u, V v) {
        return () -> new Tuple3<T,U,V>() {
            @Override
            public Tuple3<T, U, V> values() {
                return this;
            }

            @Override
            public T first() {
                return t;
            }

            @Override
            public U second() {
                return u;
            }

            @Override
            public V third() {
                return v;
            }
        };
    }
}
