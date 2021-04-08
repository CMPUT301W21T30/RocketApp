package com.example.rocketapp.controller.callbacks;

/**
 * @param <Type> object returned on callback
 */
public interface ObjectCallback<Type> {
    void callBack(Type object);
}
