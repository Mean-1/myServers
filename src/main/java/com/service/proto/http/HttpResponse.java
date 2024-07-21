package main.java.com.service.proto.http;

import java.util.Map;

/**
 * @ClassName: HttpResponse
 * @Description:
 * @author: liuchen
 * @date: 2024/7/16 0:07
 */

public class HttpResponse {

    /**
     * 响应版本
     */
    private String version;

    /**
     * 响应状态码
     */

    private int code;

    /**
     * 状态信息
     */
    private String status;

    /**
     *响应头
     */
    private Map<String, String> headers;

    /**
     * 响应正文
     */
    private String message;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
