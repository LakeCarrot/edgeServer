package edgeServer;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edgeOffloading.OffloadingGrpc;
import edgeOffloading.OffloadingOuterClass.OffloadingRequest;
import edgeOffloading.OffloadingOuterClass.OffloadingReply;

public class Sender implements Runnable {
  static String appType;
  static String hostId;
  static Double rate;

  Set<String> neighbours = new HashSet<>();

  public void Sender() {
    neighbours.add("172.28.142.176");
    neighbours.add("172.28.140.65");
    neighbours.add("172.28.142.226");
    neighbours.add("172.28.136.3");
  }

  public void sync(String appType, String hostId, Double rate) {
    this.appType = appType;
    this.hostId = hostId;
    this.rate = rate;
  }

  public void run() {
    int hostPort = 50049;
    ManagedChannel mChannel;
    for (String neighbour : neighbours) {
      System.out.println("Connect to neighbor " + neighbour + " for schedule info sync up");
      mChannel = ManagedChannelBuilder.forAddress(neighbour, hostPort)
          .usePlaintext(true)
          .build();
      OffloadingGrpc.OffloadingBlockingStub stub = OffloadingGrpc.newBlockingStub(mChannel);
      String syncMessage = appType + ":" + hostId + ":" + Double.toString(rate);
      OffloadingRequest message = OffloadingRequest.newBuilder().setMessage(syncMessage).build();
      stub.startService(message);
    }
  }
}
