package org.mhealth.open.data;

import com.alibaba.fastjson.JSON;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mhealth.open.data.configuration.ConfigurationSetting;
import org.mhealth.open.data.reader.MDataReader;
import org.mhealth.open.data.reader.MDataReaderFactory;
import org.mhealth.open.data.reader.SFileReader;
import org.mhealth.open.data.record.Patients;
import org.mhealth.open.data.record.SRecord;
import org.mhealth.open.data.util.ClockService;

import java.io.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;

import static java.time.temporal.ChronoUnit.*;

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
//        Application app = new Application();
        MDataReaderFactory factory = new MDataReaderFactory();
        MDataReader syntheaReader = factory.getReader(ConfigurationSetting.SYNTHEA_READER_CLASS);
        syntheaReader.readDataInQueue();
        SFileReader reader = (SFileReader)syntheaReader;
        reader.waitForThreadsStartup();
        reader.waitForThreadsShutdown();


    }

    @Test
    public void litterTest() throws Exception{
        String[] line = "the-user-0,2018-01-01,1994-06-24,999-25-4765,S99910734,false,Mrs.,Alina272,Gutmann808,,Bartoletti476,M,hispanic,mexican,F,Lawrence MA US,434 Assunta Valleys Apt. 183 Andover MA 05544 US".split(",");
        Patients s = new Patients(line);
        long l = ConfigurationSetting.SYNTHEA_CLOCK.instant().until(s.getBirthdate(), DAYS);
        System.out.println(l);

    }

    @Test
    public void patientTest() throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader("/home/just/Documents/csv/patients.csv"));
        reader.lines().map(line -> line.split(",")).forEach(array -> System.out.println(array[0]) );
    }
} 
