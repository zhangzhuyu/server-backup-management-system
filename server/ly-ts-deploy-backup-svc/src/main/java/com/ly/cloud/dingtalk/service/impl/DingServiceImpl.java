package com.ly.cloud.dingtalk.service.impl;

import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.google.common.collect.Maps;
import com.ly.cloud.backup.common.annotation.DingSendMessage;
import com.ly.cloud.dingtalk.component.DingButton;
import com.ly.cloud.dingtalk.component.DingCommonEnums;
import com.ly.cloud.dingtalk.component.DingFeedCardContent;
import com.ly.cloud.dingtalk.dto.ReceivedMessage;
import com.ly.cloud.dingtalk.message.*;
import com.ly.cloud.dingtalk.service.DingService;
import com.taobao.api.ApiException;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Class Name: DingServiceImpl Description:
 *
 * @author: jiangzhongxin
 * @mail: jiangzhongxin@ly-sky.com
 * @date: 2022年03月07日 15:02
 * @copyright: 2018 LIANYI TECHNOLOGY CO.,LTD. All Rights Reserved. 联奕科技有限公司
 * @version: 1.0
 */
@Service
public class DingServiceImpl implements DingService {

    private static final Logger logger = LoggerFactory.getLogger(DingServiceImpl.class);

    /**
     * MethodAnnotation注解的方法集合数据
     */
    private Map<String, Method> dingMsgProviders = Maps.newConcurrentMap();

    /**
     * @title: init
     * @description: 将获取MethodAnnotation注解的方法添加至providers
     * @author: jiangzhongxin
     * @date: 2022/3/7 15:45
     */
    @PostConstruct
    public void init() {
        try {
            // 获取dingMsgProviders注解的value值以及声明类、方法名
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .setUrls(ClasspathHelper.forPackage("com.ly.cloud"))
                    .setScanners(new MethodAnnotationsScanner())
            );
            // 获取被添加了@dingMsgProviders注解的方法的实例集合
            Set<Method> methodsAnnotatedWith = reflections.getMethodsAnnotatedWith(DingSendMessage.class);
            for (Method method : methodsAnnotatedWith) {
                String name = method.getName();
                Class<?>[] parameterTypes = method.getParameterTypes();
                // 将包含【ReceivedMessage】类对象参数条件的方法讲加入providers集合
                if (parameterTypes.length > 0 && Arrays.asList(parameterTypes).contains(ReceivedMessage.class)) {
                    DingSendMessage dingSendMessage = method.getAnnotation(DingSendMessage.class);
                    dingMsgProviders.put(dingSendMessage.keyword(), method);
                    logger.info("method added: {}", name);
                } else {
                    logger.error("unqualified methods: {}", name);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * @param keyword         : 消息关键字
     * @param receivedMessage : 接受的消息数据
     * @title: sendMessage
     * @description: 根据关键字发送消息
     * @author: jiangzhongxin
     * @date: 2022/3/7 15:42
     */
    @Override
    public void sendMessage(String keyword, ReceivedMessage receivedMessage) {
        try {
            logger.info("keyword: {}", keyword);
            Method method = dingMsgProviders.get(keyword);
            if (Optional.ofNullable(method).isPresent()) {
                // 获取声明了该方法的实例的字节码对象
                Class<?> aClass = method.getDeclaringClass();
                boolean isStatic = Modifier.isStatic(method.getModifiers());
                // 执行该方法
                if (isStatic) {
                    method.invoke(aClass, receivedMessage);
                } else {
                    method.invoke(aClass.newInstance(), receivedMessage);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 直接回复文本消息
     *
     * @param receivedMessage
     */
    @DingSendMessage
    public static void learning(ReceivedMessage receivedMessage) {
        try {
            DingTalkClient client = receivedMessage.getClient();
            String userId = receivedMessage.getSenderStaffId();
            DingTextMessageBuilder textMessageBuilder = new DingTextMessageBuilder();
            textMessageBuilder.setContent("你好啊，你没发任何信息哇！").uat(Collections.singletonList(userId));
            OapiRobotSendResponse response = client.execute(textMessageBuilder.build());
            logger.info("dingtalk-response：{}", response.getBody());
        } catch (ApiException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 直接回复文本消息
     *
     * @param receivedMessage
     */
    @DingSendMessage(keyword = "1")
    public void learning1(ReceivedMessage receivedMessage) {
        try {
            DingTalkClient client = receivedMessage.getClient();
            String userId = receivedMessage.getSenderStaffId();
            DingTextMessageBuilder textMessageBuilder = new DingTextMessageBuilder();
            textMessageBuilder.setContent("非静态方法").uat(Collections.singletonList(userId));
            OapiRobotSendResponse response = client.execute(textMessageBuilder.build());
            logger.info("dingtalk-response：{}", response.getBody());
        } catch (ApiException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * link@人员效果
     *
     * @param receivedMessage
     */
    @DingSendMessage(keyword = "link")
    public static void link(ReceivedMessage receivedMessage) {
        try {
            DingTalkClient client = receivedMessage.getClient();
            String userId = receivedMessage.getSenderStaffId();
            DingLinkMessageBuilder linkMessageBuilder = new DingLinkMessageBuilder();
            linkMessageBuilder.setTitle("文件下载-支持单聊、群聊")
                    .setText("模拟链接 : http://jzxc.vaiwan.com/netraffic/download")
                    .setLink("http://jzxc.vaiwan.com/netraffic/download")
                    .uat(Collections.singletonList(userId));
            OapiRobotSendResponse response = client.execute(linkMessageBuilder.build());
            logger.info("dingtalk-response：{}", response.getBody());
        } catch (ApiException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * markdown@人员效果
     *
     * @param receivedMessage
     */
    @DingSendMessage(keyword = "markdown")
    public static void markdown(ReceivedMessage receivedMessage) {
        try {
            DingTalkClient client = receivedMessage.getClient();
            String userId = receivedMessage.getSenderStaffId();
            DingMarkDownMessageBuilder markDownMessageBuilder = new DingMarkDownMessageBuilder();
            markDownMessageBuilder.setMarkdownTitle("Markdown@人员效果")
                    .setMarkdownContent(" @" + userId + "  \n  " +
                            "**Markdown@人员效果**  \n  " +
                            "*Markdown@人员效果*")
                    .uat(Collections.singletonList(userId));
            OapiRobotSendResponse response = client.execute(markDownMessageBuilder.build());
            logger.info("dingtalk-response：{}", response.getBody());
        } catch (ApiException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * feedCard@人员效果
     *
     * @param receivedMessage
     */
    @DingSendMessage(keyword = "feedCard")
    public static void feedCard(ReceivedMessage receivedMessage) {
        try {
            DingTalkClient client = receivedMessage.getClient();
            String userId = receivedMessage.getSenderStaffId();
            List<DingFeedCardContent> contentList = new ArrayList<>();
            contentList.add(new DingFeedCardContent("时代的火车向前开1", "https://www.baidu.com/", "https://img.alicdn.com/tfs/TB1NwmBEL9TBuNjy1zbXXXpepXa-2400-1218.png"));
            contentList.add(new DingFeedCardContent("时代的火车向前开2", "https://www.baidu.com/", "https://img.alicdn.com/tfs/TB1NwmBEL9TBuNjy1zbXXXpepXa-2400-1218.png"));
            DingFeedCardMessageBuilder feedCardMessageBuilder = new DingFeedCardMessageBuilder();
            feedCardMessageBuilder.setFeedCardContent(contentList).uat(Collections.singletonList(userId));
            OapiRobotSendResponse response = client.execute(feedCardMessageBuilder.build());
            logger.info("dingtalk-response：{}", response.getBody());
        } catch (ApiException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * actionCard@人员效果
     *
     * @param receivedMessage
     */
    @DingSendMessage(keyword = "actionCard")
    public static void actionCard(ReceivedMessage receivedMessage) {
        try {
            DingTalkClient client = receivedMessage.getClient();
            String userId = receivedMessage.getSenderStaffId();
            List<DingButton> dingButtonArrayList = new ArrayList<>();
            dingButtonArrayList.add(new DingButton("督促吃饭", "http://jzxc.vaiwan.com/welcome"));
            dingButtonArrayList.add(new DingButton("督促上班", "http://jzxc.vaiwan.com/welcome"));
            DingActionCardMessageBuilder dingActionCardMessageBuilder = new DingActionCardMessageBuilder();
            dingActionCardMessageBuilder.setTitle("我 20 年前想打造一间苹果咖啡厅，而它正是 Apple Store 的前身")
                    .setContent("> ![screenshot](https://img.alicdn.com/tfs/TB1NwmBEL9TBuNjy1zbXXXpepXa-2400-1218.png)\n" +
                            "> #### 乔布斯 20 年前想打造的苹果咖啡厅\n" +
                            "Apple Store 的设计正从原来满满的科技感走向生活化，而其生活化的走向其实可以追溯到 20 年前苹果一个建立咖啡馆的计划")
                    .setBtnOrientation(DingCommonEnums.BUTTON_ORIENTATION_HORIZONAL)
                    .setHideAvatar(true)
                    .setBtns(dingButtonArrayList)
                    .uat(Collections.singletonList(userId))
                    .build();
            OapiRobotSendResponse response = client.execute(dingActionCardMessageBuilder.build());
            logger.info("dingtalk-response：{}", response.getBody());
        } catch (ApiException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @DingSendMessage(keyword = "卡片1")
    public static void interactiveCardOne(ReceivedMessage receivedMessage) {
        try {
            String conversationId = receivedMessage.getConversationId();
            String userId = receivedMessage.getSenderStaffId();
            Map<String, String> cardData = new HashMap<>(3);
            cardData.put("title", "设计中心周会------服务端接收");
            cardData.put("date", "3月24日 周五 18:00-17:00");
            cardData.put("location", "湖畔 大梅沙");
            InteractiveCardBuilder.sendCardMsg(conversationId, "323a7c96-bc7a-4496-905d-d0436a0cae52", userId, null, cardData);
            InteractiveCardBuilder.interactiveCardRegisterCallBack("http://jzxc.vaiwan.com/callback", true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @DingSendMessage(keyword = "卡片2")
    public static void interactiveCardTwo(ReceivedMessage receivedMessage) {
        try {
            String conversationId = receivedMessage.getConversationId();
            String userId = receivedMessage.getSenderStaffId();
            Map<String, String> cardData = new HashMap<>(10);
            cardData.put("chart", "https://img.alicdn.com/imgextra/i1/O1CN014LQ8RO2ALhG7EvAtF_!!6000000008187-0-tps-595-413.jpg");
            cardData.put("pv", "12393");
            cardData.put("users", "4452");
            cardData.put("comments", "5943");
            cardData.put("likes", "34534");
            cardData.put("url", "https://www.baidu.com/");
            InteractiveCardBuilder.sendCardMsg(conversationId, "25507952-3b43-43aa-b5d5-15ee00eae30d", userId, null, cardData);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
