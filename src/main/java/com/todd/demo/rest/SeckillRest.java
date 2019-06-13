package com.todd.demo.rest;

import com.todd.demo.repository.GoodsDao;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SeckillRest {


    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping(value = "/seckill")
    public String seckill(String mobile) {
        //处理同一手机号重复请求
        if (redisTemplate.hasKey("seckill:" + mobile)) {

            return "已抢到!请勿重复提交！";
        }
        //从redis缓存中判断活动是否结束
        if (redisTemplate.hasKey("seckill:end")) {

            return "活动已结束!";
        }
        //向队列中发送消息
        rabbitTemplate.convertAndSend("mobile-queue", mobile);
        //等待MQ处理消息
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "系统繁忙!";
        }

        //查看是否秒杀成功
        if (redisTemplate.hasKey("seckill:" + mobile)) {

            return "已抢到!";
        }

        //查看活动是否结束
        if (redisTemplate.hasKey("seckill:end")) {

            return "没抢到!";
        }

        //MQ未处理完消息
        return "系统繁忙!";

    }


}
