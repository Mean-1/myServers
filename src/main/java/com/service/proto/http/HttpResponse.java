package main.java.com.service.proto.http;

/**
 * @ClassName: HttpResponse
 * @Description:
 * @author: liuchen
 * @date: 2024/7/16 0:07
 */

public class HttpResponse {

    /**
     * 编码
     */
    private byte encode;

    /**
     * 响应内容
     */
    private String response;

    /**
     * 响应长度
     */
    private int responseLength;

    public HttpResponse() {
        super();
    }

    public HttpResponse(byte encode, String response, int responseLength) {
        super();
        this.encode = encode;
        this.response = response;
        this.responseLength = responseLength;
    }

    public byte getEncode() {
        return encode;
    }

    public void setEncode(byte encode) {
        this.encode = encode;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public int getResponseLength() {
        return responseLength;
    }

    public void setResponseLength(int responseLength) {
        this.responseLength = responseLength;
    }

    @Override
    public String toString() {
        return "Response [encode=" + encode + ", response=" + response
                + ", responseLength=" + responseLength + "]";
    }
}
