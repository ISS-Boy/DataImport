package org.mhealth.open.data.consumer;

import org.mhealth.open.data.record.SRecord;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by 11245 on 2017/10/26.
 */
public class SFileProducer implements SProducer {
    private BufferedWriter writer;

    public SFileProducer(String partName) {
        try {
            writer = new BufferedWriter(new FileWriter("/Users/hu/STest/" + partName + "-output.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void produce2Dest(SRecord record) {
        //封装成指定格式
        try {
            writer.write(record.toString());
            writer.write("\n");
            SConsumer.written.incrementAndGet();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
