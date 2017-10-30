package org.mhealth.open.data.reader;

import org.mhealth.open.data.Application;
import org.mhealth.open.data.configuration.ConfigurationSetting;
import org.mhealth.open.data.exception.InValidPathException;
import org.mhealth.open.data.exception.UnhandledQueueOperationException;
import org.mhealth.open.data.record.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dujijun on 2017/10/23.
 */
public class SFileReader extends MThreadController implements MDataReader {

    public static final String[] DESCRIPTIONS = {"Body Height","Body Weight","Body Mass Index","Systolic Blood Pressure",
            "Diastolic Blood Pressure","Hemoglobin A1c/Hemoglobin.total in Blood","Glucose","Urea Nitrogen","Creatinine",
            "Calcium","Sodium","Potassium","Chloride","Carbon Dioxide","Total Cholesterol","Triglycerides","Low Density Lipoprotein Cholesterol",
            "High Density Lipoprotein Cholesterol","Microalbumin Creatinine Ratio","Estimated Glomerular Filtration Rate"};

    private String sdataRootPath = ConfigurationSetting.SDATA_ROOT_PATH;
    private List<SFileReaderThread> readers;
    public final AtomicInteger CURRENT_READER_COUNT = new AtomicInteger(0);


    public SFileReader() {
        readers = new ArrayList<>();
    }

    public List<SFileReaderThread> getReaderThreads() {
        return readers;
    }

    @Override
    public void readDataInQueue() throws InterruptedException {
        // 在这里看是读数据进入队列的逻辑
        File rootDir = new File(sdataRootPath);
        if (!rootDir.isDirectory())
            throw new InValidPathException("数据路径选取不合法，请重新选择路径");
        File[] parts = rootDir.listFiles();
        int threadcount = parts.length;
        // 初始化闭锁
        CountDownLatch startupThreadsLatch = new CountDownLatch(parts.length);
        CountDownLatch readCompleteLatch = new CountDownLatch(parts.length);
        CountDownLatch shutdownCompleteLatch = new CountDownLatch(parts.length);

        // 设置闭锁
        setStartupLatch(startupThreadsLatch);
        setCompleteLatch(readCompleteLatch);
        setShutdownLatch(shutdownCompleteLatch);

        SFileReaderThread reader;
        for(File part:parts){
            switch (part.getName()){
                case "allergies.csv":
                    reader = new SFileReaderThread(startupThreadsLatch,readCompleteLatch,shutdownCompleteLatch,part,SFileReader::allergieReader);
                    break;
                case "observations.csv":
                    reader = new SFileReaderThread(startupThreadsLatch,readCompleteLatch,shutdownCompleteLatch,part,SFileReader::observationReader);
                    break;
                default:
                    reader = new SFileReaderThread(startupThreadsLatch,readCompleteLatch,shutdownCompleteLatch,part,SFileReader::lineReader);
                    break;
            }
//            SFileReaderThread reader = new SFileReaderThread(startupThreadsLatch,readCompleteLatch,shutdownCompleteLatch,parts[8],SFileReader::lineReader);
            Thread readTherad = new Thread(reader);
            readers.add(reader);
            readTherad.start();
        }


    }
    private static void observationReader(File obsfile) {
        String partName = obsfile.getName();
        partName = partName.substring(0, partName.indexOf("."));
        Queue partQueue = Objects.requireNonNull(Application.squeueMaps.get(partName), "队列未创建或文件名有误");

        Set<String> obs = new HashSet<>();
        for(int i= 0;i<DESCRIPTIONS.length;i++)
            obs.add(DESCRIPTIONS[i]);
            String[][] lines = new String[20][];
            int n;
            String[] line;
            String pline;
            BufferedReader bf;
            try{

                bf = new BufferedReader(new FileReader(obsfile));

            //循环读取数据构造Observation对象
            out:while (true){
                for(n=0;n<DESCRIPTIONS.length;){
                    if((pline= bf.readLine())==null) {
                        break out;
                    }else {
                        line=pline.split(",");
                        if(obs.contains(line[4])){
                            lines[n] = line;
                            n++;
                        }
                    }
                }
                Observations ob = new Observations(lines);
                //之后改为进队操作
//                System.out.println(ob);
                if (!partQueue.offer(ob)) {
                    throw new UnhandledQueueOperationException("无法进入队列，请检查队列容量是否出现异常");
                }
            }

            }catch (IOException e){
                e.printStackTrace();
            }
        }


    private static void allergieReader(File allerfile){
        String partName = allerfile.getName();
        partName = partName.substring(0, partName.indexOf("."));
        Queue partQueue = Objects.requireNonNull(Application.squeueMaps.get(partName), "队列未创建或文件名有误");

        String pline;
        String[] line;
        ArrayList<String[]> lines;
        BufferedReader bf;
        String userId;
        try {
            bf = new BufferedReader(new FileReader(allerfile));
            //读一行跳过表头
            bf.readLine();
            line = bf.readLine().split(",");
            while(true){
                lines =  new ArrayList<>();
                lines.add(line);
                userId = lines.get(0)[2];
                while((pline = bf.readLine())!=null){
                    line = pline.split(",");
                    if(line[2].equals(userId)){
                        lines.add(line);
                    }else{
                        Allergies al = new Allergies(lines);
                        //之后改为进队操作
//                        System.out.println(al);
                        if (!partQueue.offer(al)) {
                            throw new UnhandledQueueOperationException("无法进入队列，请检查队列容量是否出现异常");
                        }
                        break;
                    }
                }
                if(pline==null){
                    Allergies al = new Allergies(lines);
                    //之后改为进队操作
//                    System.out.println(al);
                    if (!partQueue.offer(al)) {
                        throw new UnhandledQueueOperationException("无法进入队列，请检查队列容量是否出现异常");
                    }
                    break;
                }

            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void lineReader(File linefile){
        String partName = linefile.getName();
        partName = partName.substring(0, partName.indexOf("."));
        Queue partQueue = Objects.requireNonNull(Application.squeueMaps.get(partName), "队列未创建或文件名有误");

        String pline;
        String[] line;
        BufferedReader bf;
        String filename = linefile.getName();
        try {
            bf = new BufferedReader(new FileReader(linefile));
            //读一行跳过表头
            bf.readLine();
            //根据不同的文件创建不同的对象
            switch (filename){
                case "careplans.csv":
                    while ((pline = bf.readLine())!=null){
                        line = pline.split(",");
                        CarePlans care = new CarePlans(line);
                        //之后改为进队操作
//                        System.out.println(care);
                        if (!partQueue.offer(care)) {
                            throw new UnhandledQueueOperationException("无法进入队列，请检查队列容量是否出现异常");
                        }
                    }
                    break;
                case "conditions.csv":
                    while ((pline = bf.readLine())!=null){
                        line = pline.split(",");
                        Conditions cond = new Conditions(line);
                        //之后改为进队操作
//                        System.out.println(cond);
                        if (!partQueue.offer(cond)) {
                            throw new UnhandledQueueOperationException("无法进入队列，请检查队列容量是否出现异常");
                        }
                    }
                    break;
                case "encounters.csv":
                    while ((pline = bf.readLine())!=null){
                        line = pline.split(",");
                        Encounters enc = new Encounters(line);
                        //之后改为进队操作
//                        System.out.println(enc);
                        if (!partQueue.offer(enc)) {
                            throw new UnhandledQueueOperationException("无法进入队列，请检查队列容量是否出现异常");
                        }
                    }
                    break;
                case "immunizations.csv":
                    while ((pline = bf.readLine())!=null){
                        line = pline.split(",");
                        Immunizations imm = new Immunizations(line);
                        //之后改为进队操作
//                        System.out.println(imm);
                        if (!partQueue.offer(imm)) {
                            throw new UnhandledQueueOperationException("无法进入队列，请检查队列容量是否出现异常");
                        }
                    }
                    break;
                case "medications.csv":
                    while ((pline = bf.readLine())!=null){
                        line = pline.split(",");
                        Medications med = new Medications(line);
                        //之后改为进队操作
//                        System.out.println(med);
                        if (!partQueue.offer(med)) {
                            throw new UnhandledQueueOperationException("无法进入队列，请检查队列容量是否出现异常");
                        }
                    }
                    break;
                case "patients.csv":
                    while ((pline = bf.readLine())!=null){
                        line = pline.split(",");
                        Patients pat = new Patients(line);
                        //之后改为进队操作
//                        System.out.println(pat);
                        if (!partQueue.offer(pat)) {
                            throw new UnhandledQueueOperationException("无法进入队列，请检查队列容量是否出现异常");
                        }
                    }
                    break;
                case "procedures.csv":
                    while ((pline = bf.readLine())!=null){
                        line = pline.split(",");
                         procedures pro = new procedures(line);
                        //之后改为进队操作
//                        System.out.println(pro);
                        if (!partQueue.offer(pro)) {
                            throw new UnhandledQueueOperationException("无法进入队列，请检查队列容量是否出现异常");
                        }
                    }
                    break;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
