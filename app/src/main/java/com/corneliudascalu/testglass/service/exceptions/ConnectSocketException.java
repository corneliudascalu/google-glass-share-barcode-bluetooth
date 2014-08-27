package com.corneliudascalu.testglass.service.exceptions;

import java.io.IOException;

/**
 * Failed to connect the socket. One possible cause is the connection being interrupted or too weak.
 * @author Corneliu Dascalu <corneliu.dascalu@gmail.com>
 */
public class ConnectSocketException extends IOException{

    public ConnectSocketException(IOException e) {
        super(e);
    }
}
