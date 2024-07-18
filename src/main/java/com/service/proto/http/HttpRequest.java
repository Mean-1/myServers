package main.java.com.service.proto.http;

/**
 * @ClassName: HttpRequest
 * @Description:
 * @author: liuchen
 * @date: 2024/7/16 0:07
 */


public class HttpRequest {

    /**
     * 协议编码
     */
    private byte encode;

    /**
     * 命令
     */
    private String command;

    /**
     * 命令长度
     */
    private int commandLength;

    public HttpRequest() {
    }

    public HttpRequest(byte encode, String command, int commandLength) {
        this.encode = encode;
        this.command = command;
        this.commandLength = commandLength;
    }

    public byte getEncode() {
        return encode;
    }

    public void setEncode(byte encode) {
        this.encode = encode;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getCommandLength() {
        return commandLength;
    }

    public void setCommandLength(int commandLength) {
        this.commandLength = commandLength;
    }

    @Override
    public String toString() {
        return "Request [encode=" + encode + ", command=" + command
                + ", commandLength=" + commandLength + "]";
    }
}
