package com.todd.demo.receive;

import com.todd.demo.entity.GoodsEntity;
import com.todd.demo.entity.RecordEntity;
import com.todd.demo.repository.GoodsDao;
import com.todd.demo.repository.RecordDao;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class MQReceive {

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private RecordDao recordDao;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 处理队列中的消息
     *
     * @param message
     */
    @RabbitListener(queues = "mobile-queue")
    public void receiveSeckillMessage(String message) {
        //查看活动是否已结束
        if (redisTemplate.hasKey("seckill:end")) {

            return;
        }

        GoodsEntity goodsEntity = goodsDao.findByName("xs");
        //查看商品库存数量
        Integer count = goodsEntity.getCount();
        if (count > 0) {

            RecordEntity recordEntity = new RecordEntity();

            recordEntity.setMobile(message);

            recordEntity.setTime(new Timestamp(System.currentTimeMillis()));

            recordDao.saveAndFlush(recordEntity);
            count--;
            goodsEntity.setCount(count);

            goodsDao.saveAndFlush(goodsEntity);
            //将秒杀成功的手机号写入redis缓存
            redisTemplate.opsForValue().set("seckill:" + message, "");

            System.out.println("MQReceive-->OK-->" + message);
        } else {
            //向redis写入活动结束标识
            redisTemplate.opsForValue().set("seckill:end", "");
        }
    }

}
