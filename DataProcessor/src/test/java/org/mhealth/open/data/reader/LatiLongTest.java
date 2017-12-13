package org.mhealth.open.data.reader;

import org.junit.Test;
import org.mhealth.open.data.avro.LatitudeAndLongitude;
import org.mhealth.open.data.reader.LatitudeAndLongitudeReader;

import java.io.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by dujijun on 2017/12/12.
 */
public class LatiLongTest {

    @Test
    public void sendLaloToKafkaTest() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        LatitudeAndLongitudeReader.readInitialPos();
    }

    @Test
    public void testRandomLatiLong() throws IOException {
        List<LatitudeAndLongitude> lls = LatitudeAndLongitudeReader.getLatiLongListData();
        LatitudeAndLongitude ll = lls.get(0);
        for(int i = 0; i < 40; i++){
            ll.nextRandomValue();
            System.out.println(ll);
        }
    }

    // 将所有的记录改写为只有一条初始记录
    @Test
    public void preHandleLatiLong() throws IOException {
        File root = new File("/Users/dujijun/Documents/tmp/locus");
        File[] files = root.listFiles(f -> f.getName().startsWith("the"));
        BufferedReader br = null;
        FileWriter fw = null;
        for(File file : files){
            br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            fw = new FileWriter(file, false);
            fw.write(line);
            br.close();
            fw.flush();
            fw.close();
        }
    }

    @Test
    public void testMathRandom(){
        System.out.println(Math.random());
        System.out.println(new Random(47).nextDouble());
    }
}
