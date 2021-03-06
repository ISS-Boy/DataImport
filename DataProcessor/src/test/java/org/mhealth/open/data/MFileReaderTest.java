package org.mhealth.open.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.avro.data.Json;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mhealth.open.data.avro.MEvent;
import org.mhealth.open.data.avro.Measure;
import org.mhealth.open.data.util.ClockService;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * MFileReader Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>十月 4, 2017</pre>
 */
public class MFileReaderTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: readDataInQueue(Map<String, Queue> queueMaps)
     */
//    @Test
//    public void testReadDataInQueue() throws Exception {
//        String path = "/Users/dujijun/Documents/大数据相关/数据生成/data/UserGroup-0";
//        MDataReader reader = new MFileReadRer(new File(path));
//        Map<String, Queue> queueMap = new HashMap<>();
//        queueMap.put("blood-pressure-output", new LinkedList<>());
//        queueMap.put("body-fat-percentage-output", new LinkedList<>());
//        queueMap.put("body-weight-output", new LinkedList<>());
//        queueMap.put("heart-rate-output", new LinkedList<>());
//        queueMap.put("step-count-output", new LinkedList<>());
//
//        while (!((MFileReader) reader).isEnd())
//            reader.readDataInQueue(queueMap);
//
//        queueMap.forEach((name, queue) -> {
//            System.out.println("==================measure name is " + name);
//            queue.forEach(System.out::println);
//        });
//
//    }
    @Test
    public void testFile() {
        String path = "/Users/dujijun/Documents/大数据相关/数据生成/data/UserGroup-0";
        File file = new File(path);
        System.out.println(file.getName());
        File[] files = file.listFiles(File::isDirectory);

        Arrays.stream(files).forEach(f -> System.out.println(f.getName()));
    }

    @Test
    public void testRandomFileAccess() {
        String path = "/Users/dujijun/Documents/大数据相关/数据生成/data/UserGroup-0/the-user-5/blood-pressure-output.json";
        File file = new File(path);
        long offset = 0;
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            while (true) {
                raf.seek(offset);
                String line = raf.readLine();
                if (line == null)
                    break;
                System.out.println(line);
                offset += line.length() + 1;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testGetDate() throws IOException {
//        String record = "{\"header\":{\"id\":\"4d8328b8-342b-4fb7-bc48-3b133bbeb7c1\",\"schema_id\":{\"namespace\":\"omh\",\"name\":\"blood-pressure\",\"version\":\"1.0\"},\"user_id\":\"the-user-0\"},\"body\":{\"effective_time_frame\":{\"date_time\":\"2017-01-01T12:01:42Z\"},\"systolic_blood_pressure\":{\"unit\":\"mmHg\",\"value\":111.95501693146828},\"diastolic_blood_pressure\":{\"unit\":\"mmHg\",\"value\":73.34199409000631}}}\n";
        BufferedReader reader = new BufferedReader(new FileReader("./src/test/resources/test.json"));
        String line;


        while ((line = reader.readLine()) != null) {
            Map<String, Measure> measures = new HashMap<>();

            JSON.parseObject(line)
                    .getJSONObject("body")
                    .forEach((key, values) -> {
                        Date date = new Date();
                        if (key.equals("effective_time_frame")) {
                            if (((JSONObject) values).containsKey("date_time")) {
                                date = ((JSONObject) values).getDate("date_time");
                                System.out.println(date);
                            } else {
                                date = ((JSONObject) values).getJSONObject("time_interval").getDate("start_date_time");
                                System.out.println(date);
                                measures.put("duration", construct(((JSONObject) values).getJSONObject("time_interval").getJSONObject("duration")));
                            }
                        } else if(key.equals("sleep_duration")){
                            measures.putIfAbsent("duration", construct(values));
                        }else{
                            measures.putIfAbsent(key, construct(values));
                        }
                    });
            measures.forEach((name,measure)->{
                System.out.println(name+":"+measure);
            });

        }
        reader.close();

    }
    public Measure construct(Object obj){
        Measure measure =new Measure("",0F);
        if(obj instanceof JSONObject){
            measure = JSON.parseObject(obj.toString(), Measure.class);
        }else if (obj instanceof String){
            measure.setUnit(obj.toString());
        }else{
            measure.setValue(Float.valueOf(obj.toString()));
        }
        return measure;
    }
    @Test
    public void avroTest(){
       // String json = "{\"header\":{\"id\":\"ed15c7dc-f6bc-493d-96ba-0c85ab7721ae\",\"schema_id\":{\"namespace\":\"omh\",\"name\":\"blood-pressure\",\"version\":\"1.0\"},\"user_id\":\"the-user-0\"},\"body\":{\"effective_time_frame\":{\"date_time\":\"2017-01-01T12:01:05Z\"},\"systolic_blood_pressure\":{\"unit\":\"mmHg\",\"value\":115.03603711565032},\"diastolic_blood_pressure\":{\"unit\":\"mmHg\",\"value\":64.16587592597477}}}\n";
        String json = "{\"header\":{\"id\":\"02efa364-b2ec-4d40-b3e5-4cf016c67213\",\"creation_date_time\":\"2017-01-01T19:45:56Z\",\"acquisition_provenance\":{\"source_name\":\"generator\",\"source_creation_date_time\":\"2017-01-01T19:44:56Z\",\"modality\":\"sensed\"},\"user_id\":\"the-user-26\",\"schema_id\":{\"namespace\":\"omh\",\"name\":\"sleep-duration\",\"version\":\"1.0\"}},\"body\":{\"effective_time_frame\":{\"time_interval\":{\"start_date_time\":\"2017-01-01T12:44:56Z\",\"duration\":{\"unit\":\"h\",\"value\":7.8837229257297405}}},\"sleep_duration\":{\"unit\":\"h\",\"value\":7.8837229257297405}},\"id\":\"02efa364-b2ec-4d40-b3e5-4cf016c67213\"}";
//        String json ="{\"header\":{\"id\":\"1f63739c-f8e0-4b67-94e8-ecb19a5966a8\",\"creation_date_time\":\"2017-01-01T12:01:17Z\",\"acquisition_provenance\":{\"source_name\":\"generator\",\"source_creation_date_time\":\"2017-01-01T12:00:17Z\",\"modality\":\"sensed\"},\"user_id\":\"the-user-0\",\"schema_id\":{\"namespace\":\"omh\",\"name\":\"body-temperature\",\"version\":\"1.0\"}},\"body\":{\"effective_time_frame\":{\"date_time\":\"2017-01-01T12:00:17Z\"},\"body_temperature\":{\"unit\":\"C\",\"value\":36.59967727271914},\"measurement_location\":\"oral\"},\"id\":\"1f63739c-f8e0-4b67-94e8-ecb19a5966a8\"}" ;

        JSONObject root = JSON.parseObject(json);
        JSONObject header = root.getJSONObject("header");
        JSONObject body = root.getJSONObject("body");
        JSONObject effectiveTimeFrame = body.getJSONObject("effective_time_frame");
        Map<String, Measure> measureMap = new HashMap<>();


        String name = header.getJSONObject("schema_id").getString("name");
        String userId = header.getString("user_id");
        long timestamp = 0L;
        if(effectiveTimeFrame.containsKey("date_time"))
            timestamp = effectiveTimeFrame
                    .getDate("date_time")
                    .toInstant()
                    .getEpochSecond();
        else{
            JSONObject timeInterval = effectiveTimeFrame.getJSONObject("time_interval");
            timestamp = timeInterval
                    .getDate("start_date_time")
                    .toInstant()
                    .getEpochSecond();
            JSONObject duration = timeInterval.getJSONObject("duration");
            measureMap.put("duration", JSON.parseObject(duration.toString(), Measure.class));
        }

        body.forEach((k, v) -> {
            if (!k.equals("effective_time_frame")) {
                String value = v.toString();
                Measure measure = null;
                if(value.matches("\\d+")) {
                    measure = new Measure();
                    measure.setUnit("");
                    measure.setValue(Float.valueOf(value));
                }else{
                    measure = JSON.parseObject(v.toString(), Measure.class);
                }
                measureMap.put(k, measure);
            }
        });

        MEvent mEvent = new MEvent(userId, timestamp, measureMap);
        System.out.println(mEvent);


    }
    @Test
    public void testProperties() {
        ClassLoader classLoader = MFileReaderTest.class.getClassLoader();
        InputStream resource_in = classLoader.getResourceAsStream("conf.properties");
        Properties prop = new Properties();
        try {
            prop.load(resource_in);
            String dataRootPath = prop.getProperty("DATA_ROOT_PATH");
            long readingIntervalMillis = Long.valueOf(prop.getProperty("BLOCK_WAIT_TIME"));
            Class<?> readerClass = Class.forName(prop.getProperty("MHEALTH_READER_CLASS_NAME"));
            System.out.println(dataRootPath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testThreadShutDown() {
        ExecutorService es = Executors.newCachedThreadPool();
        for (int i = 0; i < 5; i++) {
            final int j = i;
            es.execute(() -> {
                System.out.println(j);
                try {
                    Thread.sleep(1000);
                    System.out.println(j + "over");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

        }
        es.shutdown();
        try {
            es.awaitTermination(10L, TimeUnit.SECONDS);
            System.out.println("over!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testClockMethod() throws InterruptedException {
        Clock current = Clock.system(ZoneId.systemDefault());
        Instant date = Instant.parse("2017-01-01T12:00:00Z");
        Clock clock = Clock.offset(current, Duration.between(current.instant(), date));
        Clock tickClock = Clock.tick(current, Duration.of(500_000, MILLIS));
        System.out.println(current.instant().until(date, SECONDS));
        System.out.println(date.until(current.instant(), SECONDS));
        System.out.println(TimeUnit.NANOSECONDS.convert(current.instant().until(date, SECONDS) / 100, TimeUnit.SECONDS));
        System.out.println(TimeUnit.NANOSECONDS.convert(current.instant().until(date, SECONDS), TimeUnit.SECONDS));
        System.out.println(LocalDateTime.now());
        System.out.println(Instant.ofEpochMilli(-111111));
        System.out.println(clock.instant());
        System.out.println(clock.instant());

        System.out.println(current.instant());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(current.instant());
        Clock self = Clock.offset(current, Duration.ofDays(-1));
        System.out.println(self.instant());
    }

    @Test
    public void testClockService() throws InterruptedException, ParseException {
        System.out.println(Instant.now().minusMillis(Instant.parse("2018-05-08T21:47:04Z").toEpochMilli())
                .truncatedTo(ChronoUnit.MINUTES).toEpochMilli());
        System.out.println(Duration.parse("P1D").toMillis());
        ClockService cs = new ClockService(Instant.parse("2018-05-08T21:47:04Z"), 10);
        System.out.println("当前时间是:" + cs.instant().toEpochMilli());//946728000000
        Thread.sleep(1000);
        System.out.println("过了一秒后:" + cs.instant());
        for (int i = 0; i < 10; i++) {
            System.out.println("当前时间是:" + cs.instant());
            System.out.println("======过了0.1秒=======");
            Thread.sleep(100);
        }
    }
}
