package com3011.assignment1.model;

public record ErrorResponse(String timestamp, int status, String error, String path, String message) {

}
