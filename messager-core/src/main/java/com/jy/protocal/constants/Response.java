package com.jy.protocal.constants;

public class Response {

    public int code;
    public String message;
    public Object data;

    public Response() {
    }

    public Response(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static Response success() {
        return new Response(200, "success", null);
    }

    public static Response success(Object data) {
        return new Response(200, "success", data);
    }

    public static Response error() {
        return new Response(500, "error", null);
    }

    public static Response error(String message) {
        return new Response(500, message, null);
    }

}
