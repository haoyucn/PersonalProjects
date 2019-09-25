package com.obfuscation.server;

import com.google.gson.Gson;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;

import communication.ICommand;
import communication.Result;

/**
 * Created by jalton on 10/1/18.
 */

public class GenericCommand implements ICommand {

    private static final String TAG = "GenCom";

    public String className;
    public String methodName;
    public String[] parameterType;
    public Object[] parameterValue;

    public GenericCommand(String className, String methodName, String[] parameterType, Object[] parameterValue) {
        this.className = className;
        this.methodName = methodName;
        this.parameterType = parameterType;
        this.parameterValue = parameterValue;
    }

    @Override
    public Result execute() {
//        System.out.println(this.toString());
        try {
            Class<?> retClass = Class.forName(className);

            //gets singleton
            Method getInstance = retClass.getMethod("getInstance");
            Object serverFacadeInstance = getInstance.invoke(null, null);

            Class<?>[] paramTypeClass = new Class<?>[parameterType.length];

            for (int i = 0; i < parameterType.length; i++) {
                try {
                    paramTypeClass[i] = Class.forName(parameterType[i]);
                   // System.out.println("A----------------");
                   // System.out.println(parameterValue[i].toString());
                   // System.out.println("B-----------------");
                    parameterValue[i] = new Gson().fromJson(parameterValue[i].toString(), paramTypeClass[i]);

                } catch (Exception e) {
                    e.printStackTrace();
                    return new Result(false, null, "Error: "+e.getMessage());
                }

            }

            Method method = retClass.getMethod(methodName, paramTypeClass);
//            System.out.println(parameterType);
//            System.out.println(method.getGenericParameterTypes().getClass().toString());

//            for (String s : parameterType) {
//                System.out.println("D : " + s);
//            }
//            for (Object o : parameterValue) {
//                System.out.println("D : " + o.getClass());
//            }

            Object results = method.invoke(serverFacadeInstance, parameterValue);

            return (Result)results;
        }
        catch (InvocationTargetException e) {
            //System.out.println(TAG + ": execute: " + e.getCause().getStackTrace());
            e.getCause().printStackTrace();
            return new Result(false, null, e.getCause().getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, null, e.getMessage());
        }
    }

    public String getMethodName() {
        return methodName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GenericCommand that = (GenericCommand) o;

        if (className != null ? !className.equals(that.className) : that.className != null)
            return false;
        if (methodName != null ? !methodName.equals(that.methodName) : that.methodName != null)
            return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(parameterType, that.parameterType)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(parameterValue, that.parameterValue);
    }

    @Override
    public int hashCode() {
        int result = className != null ? className.hashCode() : 0;
        result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(parameterType);
        result = 31 * result + Arrays.hashCode(parameterValue);
        return result;
    }
}
