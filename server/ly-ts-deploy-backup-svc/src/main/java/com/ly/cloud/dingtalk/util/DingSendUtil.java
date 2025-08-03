package com.ly.cloud.dingtalk.util;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.ly.cloud.backup.mapper.PublicMapper;
import com.taobao.api.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Author ljb
 * @Date 2022/7/15
 */

@Component
public class DingSendUtil {

    private static final Logger logger = LoggerFactory.getLogger(DingSendUtil.class);

    /**
     * 消息类型
     */
    private static final String MSG_TYPE_TEXT = "text";
    private static final String MSG_TYPE_LINK = "link";
    private static final String MSG_TYPE_MARKDOWN = "markdown";
    private static final String MSG_TYPE_ACTION_CARD = "actionCard";
    private static final String MSG_TYPE_FEED_CARD = "feedCard";


    private static PublicMapper publicMapper;


    public DingSendUtil(@Autowired PublicMapper publicMapper) {
        DingSendUtil.publicMapper = publicMapper;
    }


    /**
     * 返回系统设置里的机器人url
     * @return
     */
    public static String getUrl(){
        String res = publicMapper.findRobotUrl();
        return res;
    }

    /**
     *  让群聊机器人推送消息
     * @param content 推送内容
     * @param mobiles @的人员手机号,没有则null
     * @param atAll  是否 @所有人
     */
    public static OapiRobotSendResponse sendText(String content, List<String> mobiles, Boolean atAll ) throws ApiException {
        String s_url = getUrl();
        if (StringUtils.isEmpty(s_url)){
            throw new RuntimeException("钉钉机器人url为空！");
        }
//        if (s_url!=null|| "".equals(s_url)){
        DingTalkClient client = new DefaultDingTalkClient(s_url);
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("text");
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        text.setContent(content);
        request.setText(text);
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        if (mobiles != null && mobiles.size() > 0){
            at.setAtMobiles(mobiles);
        }
        at.setIsAtAll(atAll);
        request.setAt(at);

        OapiRobotSendResponse response = client.execute(request);
        return response;

//        }
    }

    /**
     * 发送Markdown 编辑格式的消息
     *
     * @param title        标题
     * @param markdownText 支持markdown 编辑格式的文本信息
     * @param mobileList   消息@ 联系人
     * @param atAll      是否@ 全部
     * @return OapiRobotSendResponse
     */
    public static void sendMessageByMarkdown(String title, String markdownText, List<String> mobileList, boolean atAll) throws ApiException {

        String s_url = getUrl();

        if (StringUtils.isEmpty(s_url)){
            throw new RuntimeException("钉钉机器人url为空！");
        }
        DingTalkClient client = new DefaultDingTalkClient(s_url);

        //参数	类型	必选	说明
        //msgtype	String	是	此消息类型为固定markdown
        //title	String	是	首屏会话透出的展示内容
        //text	String	是	markdown格式的消息
        //atMobiles	Array	否	被@人的手机号(在text内容里要有@手机号)
        //isAtAll	bool	否	@所有人时：true，否则为：false
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle(title);
        markdown.setText(markdownText);

        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype(DingSendUtil.MSG_TYPE_MARKDOWN);
        request.setMarkdown(markdown);
        if (!CollectionUtils.isEmpty(mobileList)) {
            OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
            at.setIsAtAll(atAll);
            at.setAtMobiles(mobileList);
            request.setAt(at);
        }
        OapiRobotSendResponse response = client.execute(request);
    }

}
