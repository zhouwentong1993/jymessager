package com.jy.messager.protocal.constants;

public class Response {

    public int code;
    public String message;
    public Object data;
    public int type;

    public Response() {
    }

    public Response(int code, String message, Object data, int type) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.type = type;
    }

    public static Response success(int type) {
        return new Response(200, "success", null, type);
    }

    public static Response success(Object data, int type) {
        return new Response(200, "success", data, type);
    }
//
//    public static Response error() {
//        return new Response(500, "error", null);
//    }

    public static Response error(String message, int type) {
        return new Response(500, message, null,type );
    }

}
