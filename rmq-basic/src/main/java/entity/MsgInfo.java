package entity;

import java.io.Serializable;

/**
 * ClassName: MsgInfo
 * Description:
 *
 * @author zxw
 * @date 2020/12/1 8:15 下午
 * @since JDK 1.8
 */
public class MsgInfo implements Serializable {

    private static final long serialVersionUID = -5550237134170969714L;

    /**
     * 消息体
     */
    private String body;
    /**
     * ID
     */
    private String id;
    /**
     * 消息ID
     */
    private String msgId;

    public MsgInfo(String body, String id, String msgId) {
        this.body = body;
        this.id = id;
        this.msgId = msgId;
    }


    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    @Override
    public String toString() {
        return "MsgInfo{" +
                "body='" + body + '\'' +
                ", id='" + id + '\'' +
                ", msgId='" + msgId + '\'' +
                '}';
    }
}
