package com.weijin.partner.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author yindwe@yonyu.com
 * @Date 2019/4/18 14:36
 * @Description 合伙人对应用户
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "t_user_partner")
public class User implements Serializable {
    private static final long serialVersionUID = 5795527482118319346L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    private String password;
    @Column(unique = true)
    private String name;
    private Boolean ifEnable;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date creationTime;
}
