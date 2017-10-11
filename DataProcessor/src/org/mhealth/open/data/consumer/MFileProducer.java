package org.mhealth.open.data.consumer;

import org.mhealth.open.data.reader.MRecord;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * for DataImport
 *
 * @author just on 2017/10/11.
 */
public class MFileProducer implements MProducer {
    private BufferedWriter writer;
    public MFileProducer(String measureName) {
        try {
            writer = new BufferedWriter(new FileWriter("./"+measureName+"-output.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void produce2Dest(MRecord record) {
        try{
            writer.write(record.toString());
            writer.write("\n");
            MConsumer.written.incrementAndGet();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
