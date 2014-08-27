package com.corneliudascalu.testglass.service.exceptions;

import java.io.IOException;

/**
 * Bluetooth not available, or no permissions
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class CreateSocketException extends IOException{

    public CreateSocketException(IOException e) {
        super(e);
    }
}
