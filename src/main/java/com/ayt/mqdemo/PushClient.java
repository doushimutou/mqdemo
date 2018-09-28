//package com.ayt.mqdemo;
//
//import com.alibaba.fastjson.JSONObject;
//import com.dianwoba.contract.rider.dto.result.RecruitRecordDTO;
//import com.dianwoba.core.exception.BusinessException;
//import com.dianwoba.hawkeye.common.enums.RemindTopicEn;
//import com.dianwoba.hawkeye.common.enums.RemindTypeEn;
//import com.dianwoba.hawkeye.domain.message.RemindCreateMessageBody;
//import com.dianwoba.hawkeye.domain.message.RemindCreateMessageBodyForExpressCapacity;
//import com.dianwoba.hawkeye.domain.message.RemindPushMessage;
//import com.dianwoba.redcliff.duty.domain.dto.StaffDutyDTO;
//import com.dianwoba.redcliff.duty.enums.DutyTypeEn;
//import com.dianwoba.redcliff.duty.provider.StaffDutyProvider;
//import com.dianwoba.redcliff.region.common.RegionStatus;
//import com.dianwoba.redcliff.region.dto.PointDTO;
//import com.dianwoba.redcliff.region.dto.RegionDutyDTO;
//import com.dianwoba.redcliff.region.provider.RegionProviderV2;
//import com.dianwoba.redcliff.user.dto.result.ShopDTO;
//import com.dianwoba.redcliff.user.dto.result.StationDTO;
//import com.dianwoba.redcliff.user.provider.ExpressManagerProvider;
//import com.dianwoba.redcliff.user.provider.ToBUserProvider;
//import com.dinawoba.platform.dto.PlatformDTO;
//import com.dinawoba.platform.enums.BusinessPlateEnum;
//import com.dinawoba.platform.provider.PlatformSearchProvider;
//import com.google.common.base.Joiner;
//import com.google.common.collect.Lists;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//import javax.annotation.Resource;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.rocketmq.client.producer.TransactionMQProducer;
//import org.apache.rocketmq.common.message.Message;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
///**
// * 快递运力push提醒
// * @author zhangyongjun
// * @date 2018/4/18
// */
//@Component
//public class PushClient {
//
//    private static final Logger logger = LoggerFactory.getLogger(PushClient.class);
//
//    @Resource
//    private ExpressManagerProvider expressManagerProvider;
//
//    @Resource
//    private TransactionMQProducer transactionMQProducer;
//
//    @Resource
//    private StaffDutyProvider staffDutyProvider;
//
//    @Autowired
//    private PlatformClient platformClient;
//
//    @Resource
//    private PlatformSearchProvider platformSearchProvider;
//
//    @Resource
//    private RegionProviderV2 regionProviderV2;
//
//    @Resource
//    private ToBUserProvider toBUserProvider;
//
//    /**
//     * push消息发送
//     * @Param:
//     * @return:
//     */
//    public void sendMessage(String topic, String tag, RemindCreateMessageBody body){
//        logger.info("--- sendMessage start topic=[{}],tag=[{}],body=[{}] ---",topic,tag,body);
//
//        Message msg = new Message(topic, tag, JSONObject.toJSONString(body).getBytes());
//        try {
//            logger.info("send msg " + body.toString());
//            transactionMQProducer.send(msg);
//        } catch (Exception e) {
//            logger.error("sendMessage error, messageBody:{}, exception:{}", body.toString(), e);
//        }
//
//        logger.info("--- sendMessage end ---");
//    }
//
//    /**
//    * 运力发布提醒
//    * @Param:
//    * @return:
//    */
//    public void notifyRecruit(RecruitRecordDTO recruitRecordDTO){
//        logger.info("--- notifyRecruit start,recordId=[{}] ---",recruitRecordDTO.getId());
//        //获得处理人
//        String code = getStaffCode(recruitRecordDTO);
//        logger.info("--- notifyRecruit 的 getcode 为 [{}] ---",code);
//
//        //提醒创建消息body
//        RemindCreateMessageBodyForExpressCapacity body
//                = new RemindCreateMessageBodyForExpressCapacity();
//
//        String displayname = getDisplayName(recruitRecordDTO.getShopTitle(),recruitRecordDTO.getPlatformId());
//        logger.info("--- notifyRecruit 的 getDisplayName 为 [{}] ---",displayname);
//        String pushcontent = String.format("%s发布运力需求了，点击查看详情",displayname);
//
//        //提醒推送信息封装
//        RemindPushMessage remindPushMessage = new RemindPushMessage();
//        //push的内容
//        remindPushMessage.setContent(pushcontent);
//        //push的处理人工号
//        remindPushMessage.setPushToStaff(code);
//
//        //对与调用方来说的唯一性标志，比如订单号
//        body.setCode(recruitRecordDTO.getId().toString());
//        //提醒标题
//        body.setTitle(displayname);
//        //提醒人数
//        body.setNumber(recruitRecordDTO.getRequireRiderNum());
//        //推送信息，如果有的话
//        body.setPush(remindPushMessage);
//        //时间
//        body.setDeliverTime(recruitRecordDTO.getDeliverTime());
//        body.setSignupDeadline(recruitRecordDTO.getSignupDeadline());
//        body.setStartTime(recruitRecordDTO.getStartTime());
//        body.setEndTime(recruitRecordDTO.getEndTime());
//        Map<String,String> map = new HashMap<String,String>();
//        map.put("userId",String.valueOf(recruitRecordDTO.getUserId()));
//        body.setUrlParam(map);
//        //处理人工号，推送给AM
//        body.setStaff(code);
//
//        String topic = RemindTopicEn.HAWKEYE_REMIND_CREATE_TOPIC.getTopicName();
//        String tag = RemindTypeEn.CAPACITY_RELEASE_REMIND.getTagName();
//        sendMessage(topic, tag, body);
//        logger.info("--- notifyRecruit end ---");
//    }
//
//    /**
//    * 报名不足提醒
//    * @Param:
//    * @return:
//    */
//    public void signupNotEnoughNotifyRcruit(RecruitRecordDTO recruitRecordDTO){
//        logger.info("--- signupNotEnoughNotifyRcruit start,recordId=[{}]---",recruitRecordDTO.getId());
//
//        //获得处理人
//        String code = getStaffCode(recruitRecordDTO);
//        logger.info("--- signupNotEnoughNotifyRcruit 的 getcode 为 [{}] ---",code);
//
//        //提醒创建消息body
//        RemindCreateMessageBodyForExpressCapacity body
//                = new RemindCreateMessageBodyForExpressCapacity();
//
//        int lackSignupNum = recruitRecordDTO.getRequireRiderNum() - recruitRecordDTO.getRealRiderNum();
//        String displayname = getDisplayName(recruitRecordDTO.getShopTitle(),recruitRecordDTO.getPlatformId());
//        logger.info("--- signupNotEnoughNotifyRcruit 的 getDisplayName 为 [{}] ---",displayname);
//        String pushcontent = String.format("%s缺%s人报名，点击查看详情",displayname,lackSignupNum);
//
//        //提醒推送信息封装
//        RemindPushMessage remindPushMessage = new RemindPushMessage();
//        //push的内容
//        remindPushMessage.setContent(pushcontent);
//        //push的处理人工号
//        remindPushMessage.setPushToStaff(code);
//
//        //对与调用方来说的唯一性标志，比如订单号
//        body.setCode(recruitRecordDTO.getId().toString());
//        //提醒标题
//        body.setTitle(displayname);
//        //提醒人数
//        body.setNumber(lackSignupNum);
//        //推送信息，如果有的话
//        body.setPush(remindPushMessage);
//        //时间
//        body.setDeliverTime(recruitRecordDTO.getDeliverTime());
//        body.setSignupDeadline(recruitRecordDTO.getSignupDeadline());
//        body.setStartTime(recruitRecordDTO.getStartTime());
//        body.setEndTime(recruitRecordDTO.getEndTime());
//        Map<String,String> map = new HashMap<String,String>();
//        map.put("recordId",String.valueOf(recruitRecordDTO.getId()));
//        body.setUrlParam(map);
//        //处理人工号，推送给AM
//        body.setStaff(code);
//
//        String topic = RemindTopicEn.HAWKEYE_REMIND_CREATE_TOPIC.getTopicName();
//        String tag = RemindTypeEn.SIGN_UP_NOT_ENOUGH_REMIND.getTagName();
//        sendMessage(topic, tag, body);
//        logger.info("--- signupNotEnoughNotifyRcruit end ---");
//    }
//
//    /**
//    * 未签到提醒
//    * @Param:
//    * @return:
//    */
//    public void noCheckInNotifyRecruit(RecruitRecordDTO recruitRecordDTO){
//        logger.info("--- NoCheckInNotifyRcruit start,recordId=[{}] ---",recruitRecordDTO.getId());
//
//        //获得处理人
//        String code = getStaffCode(recruitRecordDTO);
//        logger.info("--- NoCheckInNotifyRcruit 的 getcode 为 [{}] ---",code);
//
//        //提醒创建消息body
//        RemindCreateMessageBodyForExpressCapacity body
//                = new RemindCreateMessageBodyForExpressCapacity();
//
//        int lackCheckInNum = recruitRecordDTO.getRealRiderNum() - recruitRecordDTO.getCheckInNum();
//        String displayname = getDisplayName(recruitRecordDTO.getShopTitle(),recruitRecordDTO.getPlatformId());
//        logger.info("--- noCheckInNotifyRcruit 的 getDisplayName 为 [{}] ---",displayname);
//        String pushcontent = String.format("%s已报名未签到%s人，点击查看详情",displayname,lackCheckInNum);
//
//        //提醒推送信息封装
//        RemindPushMessage remindPushMessage = new RemindPushMessage();
//        //push的内容
//        remindPushMessage.setContent(pushcontent);
//        //push的处理人工号
//        remindPushMessage.setPushToStaff(code);
//
//        //对与调用方来说的唯一性标志，比如订单号
//        body.setCode(recruitRecordDTO.getId().toString());
//        //提醒标题
//        body.setTitle(displayname);
//        //提醒人数
//        body.setNumber(lackCheckInNum);
//        //推送信息，如果有的话
//        body.setPush(remindPushMessage);
//        //时间
//        body.setDeliverTime(recruitRecordDTO.getDeliverTime());
//        body.setSignupDeadline(recruitRecordDTO.getSignupDeadline());
//        body.setStartTime(recruitRecordDTO.getStartTime());
//        body.setEndTime(recruitRecordDTO.getEndTime());
//        Map<String,String> map = new HashMap<String,String>();
//        map.put("recordId",String.valueOf(recruitRecordDTO.getId()));
//        body.setUrlParam(map);
//        //处理人工号，推送给AM
//        body.setStaff(code);
//
//        String topic = RemindTopicEn.HAWKEYE_REMIND_CREATE_TOPIC.getTopicName();
//        String tag = RemindTypeEn.NO_SIGN_IN_REMIND.getTagName();
//        sendMessage(topic, tag, body);
//        logger.info("--- NoCheckInNotifyRcruit end ---");
//    }
//
//    /**
//     * 未签退提醒
//     * @Param:
//     * @return:
//     */
//    public void noCheckOutNotifyRecruit(RecruitRecordDTO recruitRecordDTO){
//        logger.info("--- NoCheckOutNotifyRecruit start,recordId=[{}] ---",recruitRecordDTO.getId());
//
//        //获得处理人
//        String code = getStaffCode(recruitRecordDTO);
//        logger.info("--- NoCheckOutNotifyRecruit 的 getcode 为 [{}] ---",code);
//
//        //提醒创建消息body
//        RemindCreateMessageBodyForExpressCapacity body
//                = new RemindCreateMessageBodyForExpressCapacity();
//
//        int lackCheckOutNum = recruitRecordDTO.getCheckInNum() - recruitRecordDTO.getCheckOutNum();
//        String displayname = getDisplayName(recruitRecordDTO.getShopTitle(),recruitRecordDTO.getPlatformId());
//        logger.info("--- noCheckOutNotifyRecruit 的 getDisplayName 为 [{}] ---",displayname);
//        String pushcontent = String.format("%s已签到未签退%s人，点击查看详情",displayname,lackCheckOutNum);
//
//        //提醒推送信息封装
//        RemindPushMessage remindPushMessage = new RemindPushMessage();
//        //push的内容
//        remindPushMessage.setContent(pushcontent);
//        //push的处理人工号
//        remindPushMessage.setPushToStaff(code);
//
//        //对与调用方来说的唯一性标志，比如订单号
//        body.setCode(recruitRecordDTO.getId().toString());
//        //提醒标题
//        body.setTitle(displayname);
//        //提醒人数
//        body.setNumber(lackCheckOutNum);
//        //推送信息，如果有的话
//        body.setPush(remindPushMessage);
//        //时间
//        body.setDeliverTime(recruitRecordDTO.getDeliverTime());
//        body.setSignupDeadline(recruitRecordDTO.getSignupDeadline());
//        body.setStartTime(recruitRecordDTO.getStartTime());
//        body.setEndTime(recruitRecordDTO.getEndTime());
//        Map<String,String> map = new HashMap<String,String>();
//        map.put("recordId",String.valueOf(recruitRecordDTO.getId()));
//        body.setUrlParam(map);
//        //处理人工号，推送给AM
//        body.setStaff(code);
//
//        String topic = RemindTopicEn.HAWKEYE_REMIND_CREATE_TOPIC.getTopicName();
//        String tag = RemindTypeEn.NOT_SIGN_OUT_REMIND.getTagName();
//        sendMessage(topic, tag, body);
//        logger.info("--- NoCheckOutNotifyRecruit end ---");
//    }
//
//    /**
//    * 获取处理人
//    * @Param:
//    * @return:
//    */
//    private String getStaffCode(RecruitRecordDTO recruitRecordDTO) {
//        logger.info("--- getStaffCode start, cityId=[{}],userId=[{}] ---",
//                recruitRecordDTO.getCityId(), recruitRecordDTO.getUserId());
//
//        ShopDTO shopDTO;
//        //根据userId获取商家经纬度
//        try {
//            shopDTO =toBUserProvider.findUserById(recruitRecordDTO.getUserId());
//        } catch (Exception e){
//            logger.error("--- userId=[{}]获取经纬度时出现异常 ---", recruitRecordDTO.getUserId());
//            return null;
//        }
//        List<String> codeList = Lists.newArrayList();
//
//        PointDTO pointDTO = new PointDTO();
//        pointDTO.setX(shopDTO.getLng());
//        pointDTO.setY(shopDTO.getLat());
//
//        //根据商家经纬度查amList
//        RegionDutyDTO regionDutyDTO = regionProviderV2
//                .findBelongRegionByBizType(recruitRecordDTO.getCityId(), pointDTO,
//                        Integer.valueOf(BusinessPlateEnum.EXPRESS.getCode()));
//
//        if (regionDutyDTO != null && CollectionUtils.isNotEmpty(regionDutyDTO.getAmList())) {
//            codeList = regionDutyDTO.getAmList().stream().map(StaffDutyDTO::getStaffCode).collect(
//                    Collectors.toList());
//        } else if (regionDutyDTO != null && regionDutyDTO.getParentId() != null) {
//            //amList为空时，查bmList
//            RegionDutyDTO regionDutyDTO1 = regionProviderV2
//                    .findRegionDuty(recruitRecordDTO.getCityId(), regionDutyDTO.getParentId(),
//                            RegionStatus.DEPLOYED.getCode(),
//                            Integer.valueOf(BusinessPlateEnum.EXPRESS.getCode()));
//            if (regionDutyDTO1 != null && CollectionUtils.isNotEmpty(regionDutyDTO1.getBmList())) {
//                codeList = regionDutyDTO1.getBmList().stream().map(StaffDutyDTO::getStaffCode)
//                        .collect(Collectors.toList());
//            }
//        }
//        if (CollectionUtils.isNotEmpty(codeList)) {
//            String code = Joiner.on(",").join(codeList);
//            logger.info("--- getStaffCode end ---");
//            return code;
//        } else {
//            logger.warn("--- getStaffCode cityId=[{}],userId=[{}]没有AM、BM、CM ---",
//                    recruitRecordDTO.getCityId(), recruitRecordDTO.getUserId());
//            return null;
//        }
//    }
//
//    /**
//    * 获取渠道名称-商家名称
//    * @Param:
//    * @return:
//    */
//    private String getDisplayName(String shopTitle, Integer platformId){
//        logger.info("--- getDisplayName start [{}] ---",platformId);
//
//        PlatformDTO platformDTO = null;
//        try {
//            platformDTO = platformSearchProvider.findById(platformId);
//        } catch (Exception e){
//            logger.info("--- getDisplayName#findById error, platformId[{}] cause:{}", platformId, e);
//            return shopTitle;
//        }
//
//        if (platformDTO == null || StringUtils.isBlank(platformDTO.getSupNm())){
//            logger.info("--- getDisplayName#findById({})没取到渠道名称", platformId);
//            return shopTitle;
//        }
//
//        String displayName = platformDTO.getSupNm() + "-" + shopTitle;
//        return displayName;
//    }
//}
