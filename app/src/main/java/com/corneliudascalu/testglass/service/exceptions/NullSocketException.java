package com.corneliudascalu.testglass.service.exceptions;

/**
 * Thrown if the bluetooth socket is null when it should already have been created
 *
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class NullSocketException extends Throwable {

    public NullSocketException(Throwable e) {
        super(e);
    }
}
