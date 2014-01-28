package uk.co.benjiweber.autovalues;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ValueType<T extends ValueType> {

    default T toValueType() {
        EqualsHashCodeInvocationHandler handler = new EqualsHashCodeInvocationHandler(this);
        return (T) Proxy.newProxyInstance(getClass().getClassLoader(), array(parameterType(), Wrapper.class, ValueType.class), handler);
    }

    default boolean valueEquals(Object other) {
        if (other == null) return false;

        List<Object> ourValues = Stream.of(getClass().getDeclaredMethods())
                .map(method -> unchecked(() -> method.invoke(this)))
                .collect(Collectors.toList());
        List<Object> theirValues = Stream.of(other.getClass().getDeclaredMethods())
                .map(method -> unchecked(() -> method.invoke(other)))
                .collect(Collectors.toList());

        if (ourValues.size() != ourValues.size()) return false;
        for (int i = 0; i < ourValues.size(); i++) {
            Object ourValue = ourValues.get(i);
            Object theirValue = theirValues.get(i);
            if (ourValue == null && theirValue != null) return false;
            if (theirValue == null && ourValue != null) return false;
            if (!ourValue.equals(theirValue)) return false;
        }

        return true;
    }

    default int valueHashCode() {
        return Stream.of(getClass().getDeclaredMethods())
            .map(method -> unchecked(() -> method.invoke(this)))
            .map(value -> Optional.of(value))
            .map(optional -> optional.map(value -> value.hashCode()))
            .reduce(Optional.of(0), (val1, val2) -> val1.map(val -> val * 31 + val2.orElse(0)))
            .orElse(0);
    }

    default String valueToString() {
        return
            "{" +
                Stream.of(getClass().getDeclaredMethods())
                    .map(method -> unchecked(() -> "[" + method.getName() + "=" + method.invoke(this) + "]"))
                    .reduce((one, two) -> one + "," + two)
                    .orElse("") +
            "}" ;
    }

    interface Wrapper {
        static Object unwrap(Object val) {
            return ((EqualsHashCodeInvocationHandler)Proxy.getInvocationHandler(val)).proxee;
        }
    }

    static class EqualsHashCodeInvocationHandler implements InvocationHandler {
        private final ValueType proxee;

        public EqualsHashCodeInvocationHandler(ValueType proxee) {
            this.proxee = proxee;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("hashCode".equals(method.getName()) && (args == null || args.length == 0)) {
                return proxee.valueHashCode();
            }
            if ("toString".equals(method.getName()) && (args == null || args.length == 0)) {
                return proxee.valueToString();
            }
            if ("equals".equals(method.getName()) && (args != null && args.length == 1)) {
                Object arg = args[0] instanceof Wrapper ? Wrapper.unwrap(args[0]) : args[0];
                return proxee.valueEquals(arg);
            }

            return method.invoke(proxee, args);
        }

    }


    default Class<T> parameterType() {
        Type type = ((ParameterizedType) getClass().getInterfaces()[0].getGenericInterfaces()[0]).getActualTypeArguments()[0];
        Class<T> rawType = type instanceof Class<?>
                ? (Class<T>) type
                : (Class<T>) ((ParameterizedType) type).getRawType();
        return rawType;
    }

    static <U> U[] array(U... us) {
        return us;
    }

    interface ExceptionalSupplier<U> {
        U supply() throws Exception;
    }
    static <U> U unchecked(ExceptionalSupplier<U> supplier) {
        try {
            return supplier.supply();
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
