//package org.mhealth.open.data;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import org.junit.Test;
//import org.mhealth.open.data.avro.MEvent;
//import org.mhealth.open.data.avro.Measure;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by dujijun on 2017/10/28.
// */
//public class AvroTest {
//
//
//    @Test
//    public void avroTest(){
////        String json = "{\"header\":{\"id\":\"ed15c7dc-f6bc-493d-96ba-0c85ab7721ae\",\"schema_id\":{\"namespace\":\"omh\",\"name\":\"blood-pressure\",\"version\":\"1.0\"},\"user_id\":\"the-user-0\"},\"body\":{\"effective_time_frame\":{\"date_time\":\"2017-01-01T12:01:05Z\"},\"systolic_blood_pressure\":{\"unit\":\"mmHg\",\"value\":115.03603711565032},\"diastolic_blood_pressure\":{\"unit\":\"mmHg\",\"value\":64.16587592597477}}}\n";
//        String json = "{\"header\":{\"id\":\"02efa364-b2ec-4d40-b3e5-4cf016c67213\",\"creation_date_time\":\"2017-01-01T19:45:56Z\",\"acquisition_provenance\":{\"source_name\":\"generator\",\"source_creation_date_time\":\"2017-01-01T19:44:56Z\",\"modality\":\"sensed\"},\"user_id\":\"the-user-26\",\"schema_id\":{\"namespace\":\"omh\",\"name\":\"sleep-duration\",\"version\":\"1.0\"}},\"body\":{\"effective_time_frame\":{\"time_interval\":{\"start_date_time\":\"2017-01-01T12:44:56Z\",\"duration\":{\"unit\":\"h\",\"value\":7.8837229257297405}}},\"sleep_duration\":{\"unit\":\"h\",\"value\":7.8837229257297405}},\"id\":\"02efa364-b2ec-4d40-b3e5-4cf016c67213\"}";
//        JSONObject root = JSON.parseObject(json);
//        JSONObject header = root.getJSONObject("header");
//        JSONObject body = root.getJSONObject("body");
//        JSONObject effectiveTimeFrame = body.getJSONObject("effective_time_frame");
//        Map<String, Measure> measureMap = new HashMap<>();
//
//
//        String name = header.getJSONObject("schema_id").getString("name");
//        String userId = header.getString("user_id");
//        long timestamp = 0L;
//        if(effectiveTimeFrame.containsKey("date_time"))
//            timestamp = effectiveTimeFrame
//                    .getDate("date_time")
//                    .toInstant()
//                    .getEpochSecond();
//        else{
//            JSONObject timeInterval = effectiveTimeFrame.getJSONObject("time_interval");
//            timestamp = timeInterval
//                    .getDate("start_date_time")
//                    .toInstant()
//                    .getEpochSecond();
//            JSONObject duration = timeInterval.getJSONObject("duration");
//            measureMap.put("duration", JSON.parseObject(duration.toString(), Measure.class));
//        }
//
//        body.forEach((k, v) -> {
//            if (!k.equals("effective_time_frame")) {
//                String value = v.toString();
//                Measure measure = null;
//                if(value.matches("\\d+")) {
//                    measure = new Measure();
//                    measure.setUnit("");
//                    measure.setValue(Float.valueOf(value));
//                }else{
//                    measure = JSON.parseObject(v.toString(), Measure.class);
//                }
//                measureMap.put(k, measure);
//            }
//        });
//
//        MEvent mEvent = new MEvent(userId, timestamp, measureMap);
//        System.out.println(mEvent);
//
//
//    }
//}
