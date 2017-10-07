package org.mhealth.open.data.processor.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by dujijun on 2017/9/23.
 */
public class ProcessorDemo {


    private static class Record {
        String userId;
        Date creationTime;
        String content;

        Record(String userId, Date creationTime, String content) {
            this.userId = userId;
            this.creationTime = creationTime;
            this.content = content;
        }

        @Override
        public String toString() {
            return "Record{" +
                    "userId='" + userId + '\'' +
                    ", creationTime=" + creationTime +
                    '}';
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, ParseException {
        BufferedReader br = new BufferedReader(new FileReader(new File("output_yeah.json")));
        Map<String, List<Record>> recorders = new HashMap<>();
        String str = "";

        while ((str = br.readLine()) != null) {
            JSONObject root = JSON.parseObject(str);
            JSONObject header = root.getJSONObject("header");

            // 获取userId
            String userId = root.getString("id");

            // 产生数据记录的时间
            String creation_date_time = header.getString("creation_date_time");
            creation_date_time = creation_date_time.replace("Z", " CST");//注意是空格+CST
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss Z");//注意格式化的表达式
            Date date = format.parse(creation_date_time);

            // 获取整个json内容
            String content = root.toString();

            // 按类别进行分类
            String catalog = header.getJSONObject("schema_id").getString("name");
            if (!recorders.containsKey(catalog))
                recorders.put(catalog, new ArrayList<>());
            // 将同类记录追加
            recorders.get(catalog).add(new Record(userId, date, content));

            System.out.println("这条记录的时间是：" + date.getTime());
            System.out.println("这条记录的日期是：" + date);
            System.out.println("实际的时间是：" + creation_date_time);
            System.out.println("现在的时间是" + System.currentTimeMillis());
            System.out.println("现在的时间超过了生产的时间: " + (System.currentTimeMillis() > date.getTime()));
        }

        System.out.println("文件读取完成！");
        ExecutorService service = Executors.newFixedThreadPool(recorders.size());
        recorders.forEach((catalog, records) -> {
            service.execute(() -> {
                records.forEach((record) -> {
                    while (record.creationTime.getTime() > System.currentTimeMillis()) ;

                    // 这里对应要有生产者进行生产操作
                    System.out.println("这里的类别是：" + catalog + ", 要进行数据生产了！");
                    System.out.println("生产的时间为: " + record.creationTime + ", 用户的id为: " + record.userId);
                });
            });
        });
        // 关闭启动线程
        service.shutdown();
        service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        System.out.println("全部执行结束");

    }
}
