package kr.co.minzero.divvyup.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

@JsonInclude(Include.NON_NULL)
public class DivvyupResponse {
    private String code;

    private String message;

    @JsonUnwrapped
    private Object body;

    public DivvyupResponse() {
        super();
    }

    public DivvyupResponse(String code) {
        super();
        this.code = code;
    }

    public DivvyupResponse(String code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public DivvyupResponse(String code, Object body) {
        super();
        this.code = code;
        this.body = body;
    }

    public DivvyupResponse(String code, String message, Object body) {
        super();
        this.code = code;
        this.message = message;
        this.body = body;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}