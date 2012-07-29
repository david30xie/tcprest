package net.bluedash.tcprest.invoker;

import net.bluedash.tcprest.server.Context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @author Weinan Li
 * Jul 29 2012
 */
public class DefaultInvoker implements Invoker {
    public String invoke(Context context) throws InstantiationException, IllegalAccessException {
        // get requested class
        Class clazz = context.getTargetClass();
        // get method to invoke
        Method method = context.getTargetMethod();
        try {
            return (String) method.invoke(clazz.newInstance());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
