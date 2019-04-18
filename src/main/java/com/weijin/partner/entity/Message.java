package com.weijin.partner.entity;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author ydw
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "t_message")
public class Message implements Serializable {

    private static final long serialVersionUID = 4432256278957890870L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 发送者
     */
    private Long fromId;
    /**
     * 发送者名称
     */
    private String fromName;
    /**
     * 接收者
     */
    private Long toId;
    /**
     * 发送的文本
     */
    @Lob
    @Column(name = "text", columnDefinition = "TEXT")
    private String text;
    /**
     * 发送日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date date;
    @Transient
    private Long statusId;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
