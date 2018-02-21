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
    int port = 50050;
    try {
      Server server = ServerBuilder.forPort(port)
          .addService(new ReceiverImpl())
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
    else if (host.equals("172.28.143.136"))
      hostName = "master";

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

  static class ReceiverImpl extends OffloadingGrpc.OffloadingImplBase {
    @Override
    public void startService(OffloadingRequest req, StreamObserver<OffloadingReply> responseObserver) {
      String reqMessage = req.getMessage();
      System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      System.out.println(reqMessage);
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
          filteredRate = 0.7 * prevRate + 0.3 * rawRte;
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
      }
      long time = System.currentTimeMillis();
      String hostName = hostTranslation(host);
      System.out.println("RuiLog : " + time + " : " + hostName + " : " + appType + " : " + filteredRate);
      //System.out.println("rawRte: " + rawRte + ", filteredRate: " + filteredRate);
      OffloadingReply reply = OffloadingReply.newBuilder()
          .setMessage("I am your father! \\\\(* W *)//")
          .build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }
  }
}
