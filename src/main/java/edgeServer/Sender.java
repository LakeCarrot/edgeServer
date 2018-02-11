package edgeServer;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.HashSet;
import java.util.Set;

import edgeOffloading.OffloadingGrpc;
import edgeOffloading.OffloadingOuterClass.OffloadingRequest;
import edgeOffloading.OffloadingOuterClass.OffloadingReply;

public class Sender implements Runnable {
  public void run() {
    Set<String> neighbours = new HashSet<>();
    neighbours.add("172.28.142.176");
    int hostPort = 50050;
    ManagedChannel mChannel;

    while (true) {
      mChannel = ManagedChannelBuilder.forAddress("localhost", hostPort)
          .usePlaintext(true)
          .build();
      OffloadingGrpc.OffloadingBlockingStub stub = OffloadingGrpc.newBlockingStub(mChannel);
      OffloadingRequest message = OffloadingRequest.newBuilder().setMessage("test sender").build();
      OffloadingReply reply = stub.startService(message);
      System.out.println("reply: " + reply);
      try {
        Thread.sleep(10000);
      } catch (Exception e) {
        System.out.println("Exception: " + e);
      }
    }






    /*
    while (true) {
      for (String neighbour : neighbours) {
        System.out.println("try to connect " + neighbour + " at " + hostPort);
        OffloadingGrpc.OffloadingBlockingStub stub = OffloadingGrpc.newBlockingStub(mChannel);
        OffloadingRequest message = OffloadingRequest.newBuilder().setMessage("hi2").build();
        //OffloadingReply reply = stub.startService(message);
        //System.out.println("reply: " + reply);
      }
      try {
        Thread.sleep(10000);
      } catch (Exception e) {
        System.out.println("Exception: " + e);
      }
    }
    */
  }
}
