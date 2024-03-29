package org.elastos.elastoscarriermultiinstancetest;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.os.Process;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
 
/**
 * Usage:
 *  Sampler.getInstance().init(getApplicationContext(), 100L);
 *  Sampler.getInstance().start();
 */
public class Sampler implements Runnable {

    private volatile static Sampler instance = null;
    private ScheduledExecutorService scheduler;
    private ActivityManager activityManager;
    private long freq;
    private Long lastCpuTime;
    private Long lastAppCpuTime;
    private RandomAccessFile procStatFile;
    private RandomAccessFile appStatFile;

    private final String relativeSavePath = "sourceSummary.txt";
    private String savePath;
    private StringBuilder surveyBuffer;
    private long count;

    private Sampler() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public static Sampler getInstance() {
        if (instance == null) {
            synchronized (Sampler.class) {
                if (instance == null) {
                    instance = new Sampler();
                }
            }
        }
        return instance;
    }

    // freq为采样周期
    public void init(Context context, long freq, String savePath) {
        activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        this.freq = freq;
        this.savePath = savePath + relativeSavePath;
        surveyBuffer = new StringBuilder();
        count = 1;
    }

    public void start() {
        scheduler.scheduleWithFixedDelay(this, 0L, freq, TimeUnit.MILLISECONDS);
    }

    public void writeDown() {
        try {
            File saveFile = new File(savePath);
            saveFile.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile));
            writer.write(surveyBuffer.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        double cpu = sampleCPU();
        double mem = sampleMemory();

        Log.d("Carrier",": CPU: " + cpu + "%" + ", Memory: " + mem + "MB\r\n");
    }

    private double sampleCPU() {
        long cpuTime;
        long appTime;
        double sampleValue = 0.0D;
        try {
            if (procStatFile == null || appStatFile == null) {
                procStatFile = new RandomAccessFile("/proc/stat", "r");
                appStatFile = new RandomAccessFile("/proc/" + Process.myPid() + "/stat", "r");
            } else {
                procStatFile.seek(0L);
                appStatFile.seek(0L);
            }
            String procStatString = procStatFile.readLine();
            String appStatString = appStatFile.readLine();
            String procStats[] = procStatString.split(" ");
            String appStats[] = appStatString.split(" ");
            cpuTime = Long.parseLong(procStats[2]) + Long.parseLong(procStats[3])
                    + Long.parseLong(procStats[4]) + Long.parseLong(procStats[5])
                    + Long.parseLong(procStats[6]) + Long.parseLong(procStats[7])
                    + Long.parseLong(procStats[8]);
            appTime = Long.parseLong(appStats[13]) + Long.parseLong(appStats[14]);
            if (lastCpuTime == null && lastAppCpuTime == null) {
                lastCpuTime = cpuTime;
                lastAppCpuTime = appTime;
                return sampleValue;
            }
            sampleValue = ((double) (appTime - lastAppCpuTime) / (double) (cpuTime - lastCpuTime)) * 100D;
            lastCpuTime = cpuTime;
            lastAppCpuTime = appTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sampleValue;
    }

    private double sampleMemory() {
        double mem = 0.0D;
        try {
            // 统计进程的内存信息 totalPss
            final Debug.MemoryInfo[] memInfo = activityManager.getProcessMemoryInfo(new int[]{Process.myPid()});
            if (memInfo.length > 0) {
                // TotalPss = dalvikPss + nativePss + otherPss, in KB
                final int totalPss = memInfo[0].getTotalPss();
                if (totalPss >= 0) {
                    // Mem in MB
                    mem = totalPss / 1024.0D;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mem;
    }

//    public static float getProcessCpuRate()
//    {
//
//        float totalCpuTime1 = getTotalCpuTime();
//        float processCpuTime1 = getAppCpuTime();
//        try
//        {
//            Thread.sleep(360);  //sleep一段时间
//        }
//        catch (Exception e)
//        {
//        }
//
//        float totalCpuTime2 = getTotalCpuTime();
//        float processCpuTime2 = getAppCpuTime();
//
//        float cpuRate = 100 * (processCpuTime2 - processCpuTime1) / (totalCpuTime2 - totalCpuTime1);//百分比
//
//        return cpuRate;
//    }
//
//    // 获取系统总CPU使用时间
//    public static long getTotalCpuTime()
//    {
//        String[] cpuInfos = null;
//        try
//        {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(
//                    new FileInputStream("/proc/stat")), 1000);
//            String load = reader.readLine();
//            reader.close();
//            cpuInfos = load.split(" ");
//        }
//        catch (IOException ex)
//        {
//            ex.printStackTrace();
//        }
//        long totalCpu = Long.parseLong(cpuInfos[2])
//                + Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
//                + Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
//                + Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
//        return totalCpu;
//    }
//
//    // 获取应用占用的CPU时间
//    public static long getAppCpuTime()
//    {
//        String[] cpuInfos = null;
//        try
//        {
//            int pid = android.os.Process.myPid();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(
//                    new FileInputStream("/proc/" + pid + "/stat")), 1000);
//            String load = reader.readLine();
//            reader.close();
//            cpuInfos = load.split(" ");
//        }
//        catch (IOException ex)
//        {
//            ex.printStackTrace();
//        }
//        long appCpuTime = Long.parseLong(cpuInfos[13])
//                + Long.parseLong(cpuInfos[14]) + Long.parseLong(cpuInfos[15])
//                + Long.parseLong(cpuInfos[16]);
//        return appCpuTime;
//    }
//    // 获取应用占用的内存(单位为KB)
//    public static String getAppMemory()
//    {
//        String info=null;
//        try
//        {
//            int pid = android.os.Process.myPid();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(
//                    new FileInputStream("/proc/" + pid + "/status")), 1000);
//            String load;
//            while((load = reader.readLine())!=null)
//            {
//                load=load.replace(" ","");
//                String[]Info=load.split("[: k K]");
//                if(Info[0].equals("VmRSS"))
//                {
//                    info=Info[1];
//                    break;
//                }
//
//            }
//            reader.close();
//        }
//        catch (IOException ex)
//        {
//            ex.printStackTrace();
//        }
//
//        return info;
//    }
}
