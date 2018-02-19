package edgeServer;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edgeOffloading.OffloadingGrpc;
import edgeOffloading.OffloadingOuterClass.OffloadingRequest;
import edgeOffloading.OffloadingOuterClass.OffloadingReply;

public class Receiver implements Runnable {
  static Map<Integer, Double> appFilteredRate = new HashMap<>();
  public void run() {
    int port = 50050;
    try {
      Server server = ServerBuilder.forPort(port)
          .addService(new ReceiverImpl())
          .build()
          .start();
      System.out.println("receiver started, listening on " + port);
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

  static class ReceiverImpl extends OffloadingGrpc.OffloadingImplBase {
    @Override
    public void startService(OffloadingRequest req, StreamObserver<OffloadingReply> responseObserver) {
      String reqMessage = req.getMessage();
      int appId = Integer.parseInt(reqMessage.split(":")[0]);
      double rawRte = Double.parseDouble(reqMessage.split(":")[1]);
      double filteredRate = 0;
      if (appFilteredRate.containsKey(appId)) {
        filteredRate = 0.8 * appFilteredRate.get(appId) + 0.2 * rawRte;
        appFilteredRate.put(appId, filteredRate);
      } else {
        filteredRate = rawRte;
        appFilteredRate.put(appId, rawRte);
      }
      //double rawRate = Double.parseDouble(reqMessage.split(":")[2]);
      long time = System.currentTimeMillis();
      System.out.println(time + " : " + appId + " : " + filteredRate);
      OffloadingReply reply = OffloadingReply.newBuilder()
          .setMessage("I am your father! \\\\(* W *)//")
          .build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }
  }
}
