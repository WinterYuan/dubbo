package org.apache.dubbo.rpc.proxy.cglib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibProxyWrapper implements MethodInterceptor {
    private Object proxyObj;

    private Class<?>[] interfaces;

    public CglibProxyWrapper(Object proxyObj, Class<?>[] interfaces) {
        this.proxyObj = proxyObj;
        this.interfaces = interfaces;
    }

    public Object getProxyObj() {
        Enhancer en = new Enhancer();
        if (this.interfaces.length != 0) {
            en.setInterfaces(interfaces);
        } else {
            en.setSuperclass(proxyObj.getClass());
        }
        en.setCallback(this);
        return en.create();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        return method.invoke(proxyObj, args);
    }
}
