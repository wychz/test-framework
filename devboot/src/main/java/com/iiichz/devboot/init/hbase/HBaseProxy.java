package com.iiichz.devboot.init.hbase;

import net.sf.cglib.proxy.*;
import org.easymock.internal.ClassInstantiatorFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HBaseProxy<T> implements MethodInterceptor{

    private T mTarget;

    public HBaseProxy(T var1) {
        this.mTarget = var1;
    }

    public static final <T, U> T create(Class<T> klass, U handler) throws InstantiationException {
        return createProxy(klass, new HBaseProxy(handler));
    }


    public Object intercept(Object self, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        try {
            return this.mTarget.getClass().getMethod(method.getName(), method.getParameterTypes()).invoke(this.mTarget, args);
        } catch (InvocationTargetException ite) {
            throw ite.getCause();
        }
    }

    public static <T> T createProxy(Class<?> klass, MethodInterceptor handler) throws InstantiationException {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(klass);
        enhancer.setInterceptDuringConstruction(true);
        enhancer.setCallbackType(handler.getClass());
        Class proxyClass = enhancer.createClass();
        Factory proxy = (Factory) ClassInstantiatorFactory.getInstantiator().newInstance(proxyClass);
        proxy.setCallbacks(new Callback[]{handler});
        return (T)proxy;
    }

}