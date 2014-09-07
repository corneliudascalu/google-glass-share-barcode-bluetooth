package com.corneliudascalu.glass.app2.interactor;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public interface RequestResult<T> {

    T getResult();

    Exception getError();

    boolean isSuccessful();
}
