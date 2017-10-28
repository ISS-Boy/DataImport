package org.mhealth.open.data;

import com.alibaba.fastjson.JSON;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mhealth.open.data.configuration.ConfigurationSetting;
import org.mhealth.open.data.reader.MDataReader;
import org.mhealth.open.data.reader.MDataReaderFactory;
import org.mhealth.open.data.reader.SFileReader;
import org.mhealth.open.data.util.ClockService;

import java.io.*;
import java.time.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
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
public class SFileReaderTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: readDataInQueue(Map<String, Queue> queueMaps)
     */
    @Test
    public void testReadDataInQueue() throws Exception {

        SFileReader reader = new SFileReader();
        reader.readDataInQueue();
        reader.waitForThreadsStartup();
        reader.waitForThreadsShutdown();

    }

    @Test
    public void testReadDataInQueue2() throws Exception {

//        Map<String, BlockingQueue> squeueMaps = ConfigurationSetting.initSyntheaContainer();
        Application app = new Application();
        MDataReaderFactory factory = new MDataReaderFactory();
        MDataReader syntheaReader = factory.getReader(ConfigurationSetting.SYNTHEA_READER_CLASS);
        syntheaReader.readDataInQueue();
        SFileReader reader = (SFileReader)syntheaReader;
        reader.waitForThreadsStartup();
        reader.waitForThreadsShutdown();
        Application.squeueMaps.get("observations").toString();

    }

} 
