//package com.ayt.mqdemo;/**
// * @Title: ${file_name}
// * @Package ${package_name}
// * @Description: ${todo}
// * @author ayt
// * @date 2018/8/422:40
// */
//
//import com.dianwoba.hawkeye.domain.message.RemindCreateMessageBody;
//import org.apache.rocketmq.client.producer.TransactionMQProducer;
//import org.apache.rocketmq.common.message.Message;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * @author ayt  on 20180804
// */
//public class PushProductor {
//    private static final Logger logger = LoggerFactory.getLogger(PushClient.class);
//
///**
// * 发送消息
// */
//    public void sendMessage(String topic,String tag,RemindCreateMessageBody body){
//        Message msg=new Message(topic,tag,body.toString().getBytes());
//        TransactionMQProducer transactionMQProducer=new TransactionMQProducer();
//        transactionMQProducer.send();
//
//    }
//
//
//
//}
//
