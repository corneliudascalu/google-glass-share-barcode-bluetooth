package com.corneliudascalu.testglass.service.exceptions;

import java.io.IOException;

/**
 * Can't open socket output stream
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class OpenStreamException extends IOException {

    public OpenStreamException(IOException e) {
        super(e);
    }
}
