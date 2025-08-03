package com.ly.cloud.dingtalk.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.dingtalkim_1_0.models.*;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.models.RuntimeOptions;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiImChatScencegroupInteractivecardCallbackRegisterRequest;
import com.dingtalk.api.response.OapiImChatScencegroupInteractivecardCallbackRegisterResponse;
import com.ly.cloud.dingtalk.constant.UrlConstant;
import com.ly.cloud.dingtalk.util.AccessTokenUtil;
import com.ly.cloud.dingtalk.util.DingTalkUtil;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;

/**
 * Class Name: InteractiveCardBuilder Description:
 *
 * @author: jiangzhongxin
 * @mail: jiangzhongxin@ly-sky.com
 * @date: 2022年02月28日 14:05
 * @copyright: 2018 LIANYI TECHNOLOGY CO.,LTD. All Rights Reserved. 联奕科技有限公司
 * @version: 1.0
 */
@Slf4j
public class InteractiveCardBuilder {

    /**
     * 注册互动卡片回调地址
     *
     * @param callBackUrl 回调URL地址
     * @param forceUpdate 是否强制覆盖更新
     * @throws Exception
     */
    public static void interactiveCardRegisterCallBack(String callBackUrl, Boolean forceUpdate) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(UrlConstant.INTERACTIVE_CARD_REGISTER_CALLBACK_URL);
            OapiImChatScencegroupInteractivecardCallbackRegisterRequest req = new OapiImChatScencegroupInteractivecardCallbackRegisterRequest();
            req.setCallbackUrl(callBackUrl);
            req.setForceUpdate(forceUpdate);
            OapiImChatScencegroupInteractivecardCallbackRegisterResponse registerRespone = client.execute(req, AccessTokenUtil.getAccessToken());
            if (registerRespone.isSuccess()) {
                log.info("interactive card callback registration succeeded！");
            } else {
                log.info("interactive card callback registration failed, reason：{}", registerRespone.getErrmsg());
            }
        } catch (ApiException e) {
            log.info("interactive card callback registration failed, reason：{}", e.getErrMsg());
        }
    }

    /**
     * 机器人推送互动卡片
     *
     * @param conversationId 群id（唯一编号，如：cid######）
     * @param cardId 卡片id（url：https://h5.dingtalk.com/interactive-card-builder/index.html）
     * @param userId 用户id
     * @param phone 手机号（需要开通根据手机号姓名获取成员信息的接口访问权限，位置：应用开发-企业内部开发-机器人-权限管理）
     * @param cardData 数据源（卡片所需要变量对应的值，即卡片数据）
     * @throws Exception
     */
    public static void sendCardMsg(String conversationId, String cardId, String userId, String phone, Map<String, String> cardData) throws Exception {
        userId = null != userId ? userId : AccessTokenUtil.getUserIdByMobile(phone);
        com.aliyun.dingtalkim_1_0.Client client = AccessTokenUtil.createClient(com.aliyun.dingtalkim_1_0.Client.class);
        SendInteractiveCardHeaders sendInteractiveCardHeaders = new SendInteractiveCardHeaders();
        sendInteractiveCardHeaders.xAcsDingtalkAccessToken = AccessTokenUtil.getAccessToken();
        SendInteractiveCardRequest sendInteractiveCardRequest = new SendInteractiveCardRequest()
                .setCardTemplateId(cardId)
                .setReceiverUserIdList(Collections.singletonList(userId))
                .setOutTrackId(DingTalkUtil.createUUID())
                .setCardData(new SendInteractiveCardRequest.SendInteractiveCardRequestCardData().setCardParamMap(cardData))
                .setOpenConversationId(conversationId)
                .setConversationType(1);
        try {
            SendInteractiveCardResponse response = client.sendInteractiveCardWithOptions(sendInteractiveCardRequest, sendInteractiveCardHeaders, new RuntimeOptions());
            log.info("dingtalk-response：{}", JSONObject.toJSONString(response));
        } catch (TeaException err) {
            log.error(JSON.toJSONString(err.getData()));
        } catch (Exception exception) {
            TeaException err = new TeaException(exception.getMessage(), exception);
            log.error(JSON.toJSONString(err.getData()));
        }
    }

    /**
     * 钉钉机器人触发回调事件
     *
     * @param tractId （卡片的唯一标识编码。是由开发者自己生成并作为入参传递给钉钉的，钉钉只在对应使用到outTrackId的场景，帮助开发者对TrackId进行记录。）
     * @param cardData （卡片数据）
     * @throws Exception
     */
    public static void updateCardMsg(String tractId, Map<String, String> cardData) throws Exception {
        com.aliyun.dingtalkim_1_0.Client client = AccessTokenUtil.createClient(com.aliyun.dingtalkim_1_0.Client.class);
        UpdateInteractiveCardHeaders updateInteractiveCardHeaders = new UpdateInteractiveCardHeaders();
        updateInteractiveCardHeaders.xAcsDingtalkAccessToken = AccessTokenUtil.getAccessToken();
        UpdateInteractiveCardRequest updateInteractiveCardRequest = new UpdateInteractiveCardRequest()
                .setOutTrackId(tractId)
                .setCardData(new UpdateInteractiveCardRequest.UpdateInteractiveCardRequestCardData()
                        .setCardParamMap(cardData))
                .setUserIdType(1);
        try {
            UpdateInteractiveCardResponse updateInteractiveCardResponse = client.updateInteractiveCardWithOptions(updateInteractiveCardRequest, updateInteractiveCardHeaders, new RuntimeOptions());
            log.info("callback, card change：{}", JSONObject.toJSONString(updateInteractiveCardResponse));
        } catch (TeaException err) {
            log.error(JSON.toJSONString(err.getData()));
        } catch (Exception exception) {
            TeaException err = new TeaException(exception.getMessage(), exception);
            log.error(JSON.toJSONString(err.getData()));
        }
    }

}
