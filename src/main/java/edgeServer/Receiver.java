package edgeServer;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

import edgeOffloading.OffloadingGrpc;
import edgeOffloading.OffloadingOuterClass.OffloadingRequest;
import edgeOffloading.OffloadingOuterClass.OffloadingReply;

public class Receiver implements Runnable {
  static Map<String, Map<String, Double>> appRate = new HashMap<>(); // appType, hostId, filteredRate
  static Map<String, Set<Integer>> serverCache = new HashMap<>();
  static double rate1 = 0;
  static double rate2 = 0;
  final static Map<String, String> schedulerTrans = new HashMap<>();

  public void run() {
    schedulerTrans.put("34.218.97.178", "m1");
    schedulerTrans.put("52.32.37.78", "m2");
    schedulerTrans.put("34.210.236.180", "m3");
    schedulerTrans.put("35.162.89.207", "m4");
    schedulerTrans.put("34.215.123.179", "m5");
    schedulerTrans.put("34.218.85.62", "m6");
    schedulerTrans.put("34.218.40.221", "m7");
    schedulerTrans.put("54.70.118.25", "m8");
    schedulerTrans.put("34.215.4.4", "m9");
    schedulerTrans.put("34.212.255.112", "m10");
    schedulerTrans.put("34.218.34.145", "m11");
    schedulerTrans.put("34.212.158.200", "m12");
    schedulerTrans.put("35.165.231.66", "m13");
    schedulerTrans.put("35.160.178.233", "m14");
    schedulerTrans.put("35.162.173.174", "m15");
    schedulerTrans.put("52.89.98.213", "m16");
    schedulerTrans.put("52.32.48.185", "m17");
    schedulerTrans.put("52.39.84.224", "m18");
    schedulerTrans.put("34.218.107.169", "m19");
    schedulerTrans.put("54.187.129.27", "m20");
    // receive app report
    int port = 50050;
    try {
      Server server = ServerBuilder.forPort(port)
          .addService(new AppReportImpl())
          .build()
          .start();
      //System.out.println("receiver started, listening on " + port);
      Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override
        public void run() {
          // Use stderr here since the logger may have been reset by its JVM shutdown hook.
          System.err.println("*** shutting down gRPC server since JVM is shutting down");
          System.err.println("*** server shut down");
        }
      });
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }

    /*
    // receive neighbor scheduler sync-up
    port = 50049;
    try {
      Server server = ServerBuilder.forPort(port)
          .addService(new SyncupImpl())
          .build()
          .start();
      //System.out.println("receiver started, listening on " + port);
      Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override
        public void run() {
          // Use stderr here since the logger may have been reset by its JVM shutdown hook.
          System.err.println("*** shutting down gRPC server since JVM is shutting down");
          System.err.println("*** server shut down");
        }
      });
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }
    */
  }

  public static String hostTranslation(String host) {
    String hostName = schedulerTrans.get(host);
    if (hostName == null)
      hostName = host;

    return hostName;
  }

  public String getAppDest(String appType) throws Exception {
    System.out.println("appRate: " + appRate);
    Map<String, Double> rateMeta = appRate.get(appType);
    String destination = null;
    System.out.println("rateMeta: " + rateMeta);
    if (rateMeta != null) {
      double maxRate = 0;
      double curRate = 0;
      double totalRates = 0;
      List<Double> machineRates = new ArrayList<>();
      double prob = 0;
      for (Map.Entry<String, Double> entry : rateMeta.entrySet()) {
        if (maxRate < entry.getValue()) {
          destination = entry.getKey();
          maxRate = entry.getValue();
        }
      }
    } else {
      System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      System.out.println("WRONG! No machine process " + appType + " yet!");
      System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      //destination = InetAddress.getLocalHost().toString().split("/")[1];
    }

    System.out.println("*************************************************");
    System.out.println("*************************************************");
    System.out.println("*************************************************");
    System.out.println("[RuiSchedule] appType: " + appType + ", hostName: " + hostTranslation(destination) + ", time: " + System.currentTimeMillis());
    System.out.println("[RuiSchedule][AppRate] appRate: ");
    for (Map.Entry<String, Map<String, Double>> entry1 : appRate.entrySet()) {
      System.out.println("[RuiSchedule][AppRate] appType: " + entry1.getKey());
      StringBuffer sb = new StringBuffer();
      sb.append("[RuiSchedule][AppRate] ");
      for (Map.Entry<String, Double> entry2 : entry1.getValue().entrySet()) {
        sb.append(schedulerTrans.get(entry2.getKey()) + ":" + entry2.getValue() + ",  ");
      }
      sb.append("\n");
      System.out.println(sb.toString());
      System.out.println("*************************************************");
      System.out.println("*************************************************");
      System.out.println("*************************************************");
    }

    return destination;
  }

  static class SyncupImpl extends OffloadingGrpc.OffloadingImplBase {
    @Override
    public void startService(OffloadingRequest req, StreamObserver<OffloadingReply> responseObserver) {
      String reqMessage = req.getMessage();
      String appType = reqMessage.split(":")[0];
      String host = reqMessage.split(":")[1];
      Double rate = Double.parseDouble(reqMessage.split(":")[2]);
      Map<String, Double> rateMeta = appRate.get(appType);
      if (rateMeta == null) {
        rateMeta = new HashMap<>();
        rateMeta.put(host, rate);
        appRate.put(appType, rateMeta);
      } else {
        rateMeta.put(host, rate);
      }
      OffloadingReply reply = OffloadingReply.newBuilder()
          .setMessage("I am your father! \\\\(* W *)//")
          .build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }
  }

  /*
  static int lruCache(String host) {
    // 400 types of apps in total
    // preloading 70% (280) of the app types
    // cache entry: 9
    Random rand = new Random();
    int  n = rand.nextInt(400);
    System.out.println("n: " + n);
    int downloadTime = 0;
    if (n > 280) {
      // docker image is not preloaded
      Set<Integer> cachedItems = serverCache.get(host);
      if (cachedItems != null) {
        if (!cachedItems.contains(n)) {
          // n is not cached
          downloadTime = 100;
          if (cachedItems.size() < 9)
            cachedItems.add(n);
          else {
            // cache replacement algo
          }
        }
      } else {
        // empty cache, so add n to it
        cachedItems = new HashSet<>();
        cachedItems.add(n);
      }
    }

    return downloadTime;
  }
  */

  static class AppReportImpl extends OffloadingGrpc.OffloadingImplBase {
    @Override
    public void startService(OffloadingRequest req, StreamObserver<OffloadingReply> responseObserver) {
      String reqMessage = req.getMessage();
      String host = reqMessage.split(":")[0];
      //int downloadTme = lruCache(host);
      String appType = reqMessage.split(":")[1];
      double rawRte = Double.parseDouble(reqMessage.split(":")[2]);
      double filteredRate = 0;
      double prevRate = 0;
      double contentionThres = 0.9;
      Map<String, Double> rateMeta = appRate.get(appType);
      //Sender sender = new Sender();
      if (rateMeta != null) {
        if (rateMeta.containsKey(host)) {
          prevRate = rateMeta.get(host);
          /*
          if (contentionThres * prevRate >= rawRte) {
            //sender.sync(appType, host, rawRte);
          }
          */
          filteredRate = 0.2 * prevRate + 0.8 * rawRte;
          rateMeta.put(host, filteredRate);
        } else {
          filteredRate = rawRte;
          rateMeta.put(host, rawRte);
        }
      } else {
        filteredRate = rawRte;
        rateMeta = new HashMap<>();
        rateMeta.put(host, rawRte);
        appRate.put(appType, rateMeta);
        //sender.sync(appType, host, rawRte);
      }
      long time = System.currentTimeMillis();
      String hostName = hostTranslation(host);
      System.out.println("RuiLog : " + time + " : " + hostName + " : " + appType + " : " + filteredRate + " : " + rawRte);
      OffloadingReply reply = OffloadingReply.newBuilder()
          .setMessage("I am your father! \\\\(* W *)//")
          .build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }
  }
}
