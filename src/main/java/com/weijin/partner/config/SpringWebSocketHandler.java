package com.weijin.partner.config;

import com.alibaba.fastjson.JSONObject;
import com.weijin.partner.entity.Message;
import com.weijin.partner.repository.MessageRepository;
import com.weijin.partner.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


@Component
public class SpringWebSocketHandler extends TextWebSocketHandler {
    public static final Map<Long, WebSocketSession> USER_SOCKET_SESSION_MAP;
    private static Logger logger = LoggerFactory.getLogger(SpringWebSocketHandler.class);

    @Resource
    private IUserService userService;
    @Resource
    private MessageRepository messageRepository;
    @Resource
    private RedisTemplate<String, Message> redisTemplate;

    static {
        USER_SOCKET_SESSION_MAP = new ConcurrentHashMap<>();
    }

    public SpringWebSocketHandler() {
        // TODO Auto-generated constructor stub
    }

    /**
     * 连接成功时候，会触发页面上onopen方法
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // TODO Auto-generated method stub
        Long uid = (Long) session.getAttributes().get("uid");
        System.out.println(USER_SOCKET_SESSION_MAP);
        USER_SOCKET_SESSION_MAP.put(uid, session);
        ListOperations<String, Message> stringTextMessageListOperations = redisTemplate.opsForList();
        logger.info("传入的redis解析串:{}", uid);
        Set<String> keys = redisTemplate.keys("*&" + uid);
        if (keys != null) {
            keys.forEach(key -> {
                long size = 0;
                try {
                    size = stringTextMessageListOperations.size(key);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < size; i++) {
                    sendMessageToUser(new TextMessage(JSONObject.toJSONBytes(stringTextMessageListOperations.rightPop(key))));
                }
            });
        }
        String username = userService.getNameById(uid);
        Message msg = new Message();
        //0表示上线消息
        msg.setStatusId(0L).
                setText(username).
                setDate(Calendar.getInstance().getTime());
        this.broadcast(new TextMessage(JSONObject.toJSONBytes(msg)));
//        }
    }

    /**
     * 关闭连接时触发
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        System.out.println("Websocket:" + session.getId() + "已经关闭");
        // 移除当前用户的Socket会话
        kickSession(session);
    }

    /**
     * js调用websocket.send时候，会调用该方法
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        if (message.getPayloadLength() == 0) {
            return;
        }
        Message msg = JSONObject.parseObject(message.getPayload(), Message.class);
        msg.setStatusId(msg.getFromId());
        msg.setDate(Calendar.getInstance().getTime());
        try {
//            messageRepository.save(msg);
        } catch (Exception e) {

        }
        sendMessageToUser(new TextMessage(JSONObject.toJSONBytes(msg)));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        /*if (session.isOpen()) {
            session.close();
        }*/
        // 移除当前抛出异常用户的Socket会话
//        kickSession(session);
    }

    private void kickSession(WebSocketSession session) throws IOException {
        for (Map.Entry<Long, WebSocketSession> entry : USER_SOCKET_SESSION_MAP.entrySet()) {
            if (entry.getValue().getId().equals(session.getId())) {
                USER_SOCKET_SESSION_MAP.remove(entry.getKey());
                System.out.println("Socket会话已经移除:用户ID" + entry.getKey());
                String username = userService.getNameById(entry.getKey());
                Message msg = new Message();
                msg.setFromId(-2L);
                msg.setStatusId(-2L);
                msg.setText(username).setDate(Calendar.getInstance().getTime());
                this.broadcast(new TextMessage(JSONObject.toJSONBytes(msg)));
                break;
            }
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 给所有在线用户发送消息
     *
     * @param message
     * @throws IOException
     */
    public void broadcast(final TextMessage message) throws IOException {
        Iterator<Map.Entry<Long, WebSocketSession>> it = USER_SOCKET_SESSION_MAP.entrySet().iterator();

        //多线程群发
        while (it.hasNext()) {

            final Map.Entry<Long, WebSocketSession> entry = it.next();

            if (entry.getValue().isOpen()) {
                // entry.getValue().sendMessage(message);
                new Thread(() -> {
                    try {
                        if (entry.getValue().isOpen()) {
                            entry.getValue().sendMessage(message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }

        }
    }

    /**
     * 给某个用户发送消息
     *
     * @param message
     * @throws IOException
     */
    private void sendMessageToUser(TextMessage message) {
        Message msg = JSONObject.parseObject(message.getPayload(), Message.class);
        Long uid = msg.getFromId();
        Long toId = msg.getToId();
        WebSocketSession session = USER_SOCKET_SESSION_MAP.get(toId);
//        if (session != null && session.isOpen()) {
        try {
            session.sendMessage(message);
        } catch (Exception e) {
            String chatId = uid.toString() + "&" + toId.toString();
            ListOperations<String, Message> longTextMessageListOperations = redisTemplate.opsForList();
            longTextMessageListOperations.getOperations().expire(chatId, 1, TimeUnit.DAYS);
            longTextMessageListOperations.leftPush(chatId, msg);
        }
//        }
    }


}