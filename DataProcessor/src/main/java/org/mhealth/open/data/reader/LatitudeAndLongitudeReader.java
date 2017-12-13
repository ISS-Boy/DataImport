package org.mhealth.open.data.reader;

import org.mhealth.open.data.avro.LatitudeAndLongitude;
import org.mhealth.open.data.configuration.ConfigurationSetting;
import org.mhealth.open.data.consumer.LatiLongSender;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by dujijun on 2017/12/12.
 */
public class LatitudeAndLongitudeReader {

    private static String rootDir = ConfigurationSetting.LATILONG_DATA_PATH;
    public static void readInitialPos() throws IOException, InterruptedException, ExecutionException, TimeoutException {

        List<LatitudeAndLongitude> lls = getLatiLongListData();

        ExecutorService singleThread = Executors.newSingleThreadExecutor();
        Future<?> future = singleThread.submit(new LatiLongSender(lls));
        future.get(5, TimeUnit.MINUTES);
    }

    /**
     * 从文件中获取所有用的初始经纬度信息, 可用于单元测试
     */
    static List<LatitudeAndLongitude> getLatiLongListData() throws IOException {
        File root = new File(rootDir);
        File[] files = root.listFiles(f -> f.getName().startsWith("the-user-"));
        BufferedReader br = null;
        List<LatitudeAndLongitude> lls = new LinkedList<>();
        for (File f : files) {
            br = new BufferedReader(new FileReader(f));
            String userId = f.getName();
            String[] latiAndLong = br.readLine().split(",");
            double latitude = Double.valueOf(latiAndLong[0]);
            double longitude = Double.valueOf(latiAndLong[1]);
            lls.add(new LatitudeAndLongitude(userId, latitude, longitude, 0l));
            br.close();
        }
        return lls;
    }


}
