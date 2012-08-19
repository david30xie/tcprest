package io.tcprest.conveter;

import io.tcprest.exception.MapperNotFoundException;
import io.tcprest.logger.Logger;
import io.tcprest.logger.SystemOutLogger;
import io.tcprest.mapper.Mapper;
import io.tcprest.protocol.NullObj;
import io.tcprest.protocol.TcpRestProtocol;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TcpRest Protocol:
 * Class.method(arg1, arg2) should transform to: "Class/method({{arg1}}arg1ClassName,{{arg2}}arg2ClassName)"
 * <p/>
 * For example:
 * HelloWorldResource.sayHelloFromTo("Jack", "Lucy") should transform to:
 * "HelloWorldResource/sayHelloFromTo({{Jack}}java.lang.String,{{Lucy}}java.lang.String)"
 *
 * @author Weinan Li
 * @date 07 31 2012
 */
public class DefaultConverter implements Converter {
    Logger logger = new SystemOutLogger();

    /**
     * Convert a method call into a string according to TcpRest protocol that can be transmitted across network.
     *
     * @param clazz   Calling class
     * @param method  Calling method
     * @param params  parameters of calling method
     * @param mappers mapper for each parameter
     * @return
     * @throws MapperNotFoundException
     */
    public String encode(Class clazz, Method method, Object[] params, Map<String, Mapper> mappers) throws MapperNotFoundException {
        StringBuilder paramTokenBuffer = new StringBuilder();
        if (params != null) {
            for (Object param : params) {
                if (param == null)
                    param = new NullObj();
                logger.log("***DefaultConverter: " + param.getClass());
                Mapper mapper = mappers.get(param.getClass().getCanonicalName());
                if (mapper == null) {
                    throw new MapperNotFoundException("***DefaultConverter - Cannot find mapper for: " + param.getClass().getCanonicalName());
                }

                paramTokenBuffer.append("{{").append(mapper.objectToString(param))
                        .append("}}").append(TcpRestProtocol.PATH_SEPERATOR);
            }

            return clazz.getCanonicalName() + "/" + method.getName() + "(" + paramTokenBuffer.
                    substring(0, paramTokenBuffer.length() - TcpRestProtocol.PATH_SEPERATOR.length()) + ")";
        } else {
            return clazz.getCanonicalName() + "/" + method.getName() + "()";
        }


    }

    public Object[] decode(Method targetMethod, String paramsToken, Map<String, Mapper> mappers) throws MapperNotFoundException {
        if (paramsToken == null || paramsToken.trim().length() < 1) {
            return null;
        } else {
            String rawParams[] = paramsToken.trim().split(TcpRestProtocol.PATH_SEPERATOR);
            List<Object> paramsHolder = new ArrayList<Object>();
            int i = 0;
            for (String rawParam : rawParams) {
                String thisParam = decodeParam(rawParam);
                String classType = targetMethod.getParameterTypes()[i].getCanonicalName();
                Mapper mapper = mappers.get(classType.trim());
                if (mapper == null) {
                    throw new MapperNotFoundException("***DefaultConverter - cannot find mapper for: " + classType);
                }

                Object param = mapper.stringToObject(thisParam);

                if (thisParam.equals(TcpRestProtocol.NULL)) {
                    param = null;
                }

                paramsHolder.add(param);
                i++;
            }
            return paramsHolder.toArray();
        }
    }

    /**
     * Encode a single parameter.
     * For example: "abc" will transform into "{{abc}}java.lang.String"
     *
     * @param message
     * @return
     */
    public String encodeParam(String message) {
        return "{{" + message + "}}";
    }


    /**
     * Decode a single parameter.
     *
     * @param message
     * @return
     */
    public String decodeParam(String message) {
        return message.substring(message.indexOf("{{") + 2, message.lastIndexOf("}}"));
//        String type = message.substring(message.indexOf("}}") + 2, message.length());
    }
}
