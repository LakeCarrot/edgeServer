package edgeServer;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
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
      for (Map.Entry<String, Double> entry : rateMeta.entrySet()) {
        if (entry.getValue() > maxRate) {
          maxRate = entry.getValue();
          destination = entry.getKey();
        }
      }
    } else {
      destination = InetAddress.getLocalHost().toString().split("/")[1];
    }
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
  }

  static class SyncupImpl extends OffloadingGrpc.OffloadingImplBase {
    @Override
    public void startService(OffloadingRequest req, StreamObserver<OffloadingReply> responseObserver) {
      String reqMessage = req.getMessage();
      String appType = reqMessage.split(":")[0];
      String host = reqMessage.split(":")[1];
      Double rate = Double.parseDouble(reqMessage.split(":")[2]);
      System.out.println(appType + " reports to " + host + " with rate " + rate);
      System.out.println("appRate: " + appRate);
      Map<String, Double> rateMeta = appRate.get(appType);
      System.out.println("*****************************************");
      System.out.println("*****************************************");
      System.out.println("*****************************************");
      System.out.println("rateMeta: " + rateMeta);
      if (rateMeta.isEmpty()) {
        rateMeta = new HashMap<>();
        rateMeta.put(host, rate);
        appRate.put(appType, rateMeta);
      } else {
        rateMeta.put(host, rate);
      }
      System.out.println("appRate: " + appRate);
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
      double filteredRate = 0;
      double prevRate = 0;
      Map<String, Double> rateMeta = appRate.get(appType);
      if (rateMeta != null) {
        if (rateMeta.containsKey(host)) {
          //System.out.println("old " + appType + " on " + host);
          prevRate = rateMeta.get(host);
          filteredRate = 0.8 * prevRate + 0.2 * rawRte;
          rateMeta.put(host, filteredRate);
        } else {
          // first app on this host
          //System.out.println("first " + appType + " on " + host);
          filteredRate = rawRte;
          rateMeta.put(host, rawRte);
        }
      } else {
        // first app in the system
        //System.out.println("first " + appType + " in the system");
        filteredRate = rawRte;
        rateMeta = new HashMap<>();
        rateMeta.put(host, rawRte);
        appRate.put(appType, rateMeta);
        //sender.sync(appType, host, rawRte);
      }
      Sender sender = new Sender();
      System.out.println("Start to sync 1111111");
      sender.sync(appType, host, rawRte);
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
