package edgeServer;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edgeOffloading.OffloadingGrpc;
import edgeOffloading.OffloadingOuterClass.OffloadingRequest;
import edgeOffloading.OffloadingOuterClass.OffloadingReply;

public class Receiver implements Runnable {
  static Map<String, Map<String, Double>> appRate = new HashMap<>(); // appType, hostId, filteredRate
  static double rate1 = 0;
  static double rate2 = 0;

  public void run() {
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
  }

  public static String hostTranslation(String host) {
    String hostName = "unknown";
    if (host.equals("172.28.142.176"))
      hostName = "slave1";
    else if (host.equals("172.28.140.65"))
      hostName = "slave2";
    else if (host.equals("172.28.142.226"))
      hostName = "slave3";
    else if (host.equals("172.28.136.3"))
      hostName = "master";
    else
      hostName = host;

    return hostName;
  }

  public String getAppDest(String appType) throws Exception {
    Map<String, Double> rateMeta = appRate.get(appType);
    String destination = null;
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
      /*
      for (Map.Entry<String, Double> entry : rateMeta.entrySet()) {
        curRate = entry.getValue();
        machineRates.add(curRate);
        totalRates = totalRates + curRate;
      }

      for (double machineRate : machineRates) {
        prob = machineRate / totalRates;
      }
      */
    } else {
      destination = InetAddress.getLocalHost().toString().split("/")[1];
    }
    return destination;
    /*
    //destination = "172.28.142.176";  // always use slave1
    System.out.println("*****************************************");
    System.out.println("*****************************************");
    System.out.println("*****************************************");
    System.out.println("[RuiSchedule] appType: " + appType + ", hostName: " + hostTranslation(destination));
    System.out.println("[RuiSchedule] appRate: " + appRate);
    System.out.println("*****************************************");
    System.out.println("*****************************************");
    System.out.println("*****************************************");

    return destination;
    */
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

  static class AppReportImpl extends OffloadingGrpc.OffloadingImplBase {
    @Override
    public void startService(OffloadingRequest req, StreamObserver<OffloadingReply> responseObserver) {
      String reqMessage = req.getMessage();
      String host = reqMessage.split(":")[0];
      String appType = reqMessage.split(":")[1];
      double rawRte = Double.parseDouble(reqMessage.split(":")[2]);
      try {
        String localIP = InetAddress.getLocalHost().toString().split("/")[1];
        if (!localIP.equals(host)) {
          System.out.println("WRONG!! Should not report to central scheduler!");
          System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
          System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
          System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
      } catch (Exception e) {
        System.out.println(e);
      }
      double filteredRate = 0;
      double prevRate = 0;
      double contentionThres = 0.9;
      Map<String, Double> rateMeta = appRate.get(appType);
      Sender sender = new Sender();
      if (rateMeta != null) {
        if (rateMeta.containsKey(host)) {
          prevRate = rateMeta.get(host);
          if (contentionThres * prevRate >= rawRte) {
            sender.sync(appType, host, rawRte);
          }
          filteredRate = 0.8 * prevRate + 0.2 * rawRte;
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
        sender.sync(appType, host, rawRte);
      }
      long time = System.currentTimeMillis();
      String hostName = hostTranslation(host);
      System.out.println("RuiLog : " + time + " : " + hostName + " : " + appType + " : " + filteredRate);
      OffloadingReply reply = OffloadingReply.newBuilder()
          .setMessage("I am your father! \\\\(* W *)//")
          .build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }
  }
}
