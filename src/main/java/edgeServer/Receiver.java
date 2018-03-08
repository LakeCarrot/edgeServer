package edgeServer;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import edgeOffloading.OffloadingGrpc;
import edgeOffloading.OffloadingOuterClass.OffloadingRequest;
import edgeOffloading.OffloadingOuterClass.OffloadingReply;

public class Receiver implements Runnable {
  static Map<String, Map<String, Double>> appRate = new HashMap<>(); // appType, hostId, filteredRate
  static Map<String, Set<Integer>> serverCache = new HashMap<>();
  static double rate1 = 0;
  static double rate2 = 0;
  final static Map<String, String> schedulerTrans = new HashMap<>();
  static List<Integer> machineList = new ArrayList<>();
  static Map<String, List<String>> neighborList = new HashMap<>();
  static Map<String, Map<String, Integer>> activeSession = new HashMap<>(); // hostname, apptype, #activeSession
  static ReentrantLock lock = new ReentrantLock();
  Queue<Node> sessionQueue = new LinkedList<>();

  class Node {
    String appType;
    String hostName;
    public Node(String appType, String hostName) {
      this.appType = appType;
      this.hostName = hostName;
    }
  }

  String findIpFromHostName(String hostName) {
    for (Map.Entry<String, String> entry : schedulerTrans.entrySet()) {
      if (hostName.equals(entry.getValue())) {
        return entry.getKey();
      }
    }

    return null;
  }

  private void initActiveSession(String hostName) {
    Map<String, Integer> tmp = new HashMap<>();
    tmp.put("face", 0);
    tmp.put("speech", 0);
    tmp.put("plate", 0);
    tmp.put("ocr", 0);
    activeSession.put(hostName, tmp);
  }

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
    // initialize the active session list
    initActiveSession("34.218.97.178");
    initActiveSession("52.32.37.78");
    initActiveSession("34.210.236.180");
    initActiveSession("35.162.89.207");
    initActiveSession("34.215.123.179");
    initActiveSession("34.218.85.62");
    initActiveSession("34.218.40.221");
    initActiveSession("54.70.118.25");
    initActiveSession("34.215.4.4");
    initActiveSession("34.212.255.112");
    initActiveSession("34.218.34.145");
    initActiveSession("34.212.158.200");
    initActiveSession("35.165.231.66");
    initActiveSession("35.160.178.233");
    initActiveSession("35.162.173.174");
    initActiveSession("52.89.98.213");
    initActiveSession("52.32.48.185");
    initActiveSession("52.39.84.224");
    initActiveSession("34.218.107.169");
    initActiveSession("54.187.129.27");
    for (int cnt = 1; cnt <= 20; cnt++) {
      machineList.add(cnt);
    }
    // initialize the neighborList
    Random r = new Random();
    int numNeighbors = 12;
    List<String> neighb;
    String hostName;
    Set<Integer> selectedMachines;
    for (int iter = 1; iter <= 20; iter++) {
      neighb = new ArrayList<>();
      selectedMachines = new HashSet<>();
      hostName = "m" + Integer.toString(iter);
      neighb.add(findIpFromHostName(hostName));
      int rand;
      while (selectedMachines.size() < numNeighbors) {
        rand = r.nextInt(20) + 1;
        if (rand != iter)
          selectedMachines.add(rand);
      }
      System.out.println("[RuiNeighbor] machine " + iter + "'s neighbors: " + selectedMachines);
      for (Integer machineId : selectedMachines) {
        hostName = "m" + Integer.toString(machineId);
        neighb.add(findIpFromHostName(hostName));
      }
      neighborList.put("m" + Integer.toString(iter), neighb);
    }
    System.out.println("[RuiReal] neighborList: " + neighborList);

    // assign initial warm-up speed to MAX_VALUE
		for(String j : Arrays.asList("face", "speech", "plate", "ocr")) {
			appRate.put(j, new HashMap<>());
			for(String i : schedulerTrans.keySet()) {
				appRate.get(j).put(i, 100000.0);
			}
    }
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

  public String getAppDest(String appType, String serverID) throws Exception {
    Map<String, Double> rateMeta_ori = appRate.get(appType);
    String destination = null;
    double pRatio = 0.8;

    // poll the head if the queue is larger than 80
    if(sessionQueue.size() == 80) {
      Node outdated = sessionQueue.poll();
      Map<String, Integer> tmp = activeSession.get(outdated.hostName);
      tmp.put(outdated.appType, tmp.get(outdated.appType) - 1);
      activeSession.put(outdated.hostName, tmp);
      System.out.println("[Bo Active] Delete Old Session : " + outdated.appType + " : " + outdated.hostName);
    }

    /*
      Ours (start)
     */
    Map<String, Double> rateMeta = new HashMap<>();
    List<String> idleMachine = new LinkedList<>();
    System.out.println("[RuiReal] server " + serverID + " has " + neighborList.get(serverID).size() + " neighbors.");
    for(String neighborIP : neighborList.get(serverID)) {
      if(activeSession.get(neighborIP).get(appType) != 0 || rateMeta_ori.get(neighborIP) == 100000) {
        rateMeta.put(neighborIP, rateMeta_ori.get(neighborIP));
      } else {
        System.out.println("[DEBUG] idleMachine: " + idleMachine);
        idleMachine.add(neighborIP);
      }
    }
    /*
      Ours (stop)
     */

    double rRatio = new Random().nextDouble();
    int idleSize = idleMachine.size();

    if (idleSize == 0) {
      if(rateMeta != null) {
        double maxRate = 0;
        List<String> dstList = new ArrayList<>();
        double curRate = 0;
        double totalRates = 0;
        List<Double> machineRates = new ArrayList<>();
        double prob = 0;
        for (Map.Entry<String, Double> entry : rateMeta.entrySet()) {
          if (maxRate < entry.getValue()) {
            dstList = new ArrayList<>();
            dstList.add(entry.getKey());
            maxRate = entry.getValue();
          } else if (maxRate == entry.getValue()) {
            dstList.add(entry.getKey());
          }
        }
        Random r = new Random();
        destination = dstList.get(r.nextInt(dstList.size()));
      }
      else {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("WRONG! No machine process " + appType + " yet!");
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        //destination = InetAddress.getLocalHost().toString().split("/")[1];
      }
    } else {
      if(rateMeta == null) {
        destination = idleMachine.get(new Random().nextInt(idleSize));
        System.out.println("[Bo Active] P offloading triggered1" + " : " + appType + " : " + destination);
      } else if(rRatio >= pRatio) {
        destination = idleMachine.get(new Random().nextInt(idleSize));
        System.out.println("[Bo Active] P offloading triggered2" + " : " + appType + " : " + destination);
      } else {
        double maxRate = 0;
        List<String> dstList = new ArrayList<>();
        double curRate = 0;
        double totalRates = 0;
        List<Double> machineRates = new ArrayList<>();
        double prob = 0;
        for (Map.Entry<String, Double> entry : rateMeta.entrySet()) {
          if (maxRate < entry.getValue()) {
            dstList = new ArrayList<>();
            dstList.add(entry.getKey());
            maxRate = entry.getValue();
          } else if (maxRate == entry.getValue()) {
            dstList.add(entry.getKey());
          }
        }
        Random r = new Random();
        destination = dstList.get(r.nextInt(dstList.size()));
      }
    }

    System.out.println("*************************************************");
    System.out.println("*************************************************");
    System.out.println("*************************************************");
    System.out.println("[RuiSchedule] appType: " + appType + ", hostName: " + hostTranslation(destination) + ", time: " + System.currentTimeMillis());

    // Record the new request information
    sessionQueue.add(new Node(appType, destination));
    // update the new request to active session list
    Map<String, Integer> tmp = activeSession.get(destination);
    tmp.put(appType, tmp.get(appType) + 1);
    activeSession.put(destination, tmp);
    System.out.println("[Bo Active] AddNewSession : " + appType + " : " + destination);

    System.out.println("[RuiSchedule][AppRate] appRate at " + System.currentTimeMillis());
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

  static class AppReportImpl extends OffloadingGrpc.OffloadingImplBase {
    @Override
    public void startService(OffloadingRequest req, StreamObserver<OffloadingReply> responseObserver) {
      //System.out.println("[RuiLog] " + req.getMessage());
      String reqMessage = req.getMessage();
      String host = reqMessage.split(":")[0];
      String sessionID = reqMessage.split(":")[1];
      //int downloadTme = lruCache(host);
      String appType = reqMessage.split(":")[2];
      double rawRte = Double.parseDouble(reqMessage.split(":")[3]);
      double filteredRate = 0;
      double prevRate = 0;
      double contentionThres = 0.9;
      lock.lock();
      Map<String, Double> rateMeta = appRate.get(appType);
      //Sender sender = new Sender();
      if (rateMeta != null) {
        if (rateMeta.containsKey(host)) {
          prevRate = rateMeta.get(host);
					if(prevRate != 100000) {
          	filteredRate = 0.8 * prevRate + 0.2 * rawRte;
					} else {
						filteredRate = rawRte;
					}
          rateMeta.put(host, filteredRate);
        } else {
          System.out.println("WRONG!!!!");
          filteredRate = rawRte;
          rateMeta.put(host, rawRte);
        }
        appRate.put(appType, rateMeta);
      } else {
        System.out.println("WRONG!!!!");
        filteredRate = rawRte;
        rateMeta = new HashMap<>();
        rateMeta.put(host, rawRte);
        appRate.put(appType, rateMeta);
        //sender.sync(appType, host, rawRte);
      }
      lock.unlock();
      long time = System.currentTimeMillis();
      String hostName = hostTranslation(host);
      System.out.println("[RuiSchedule] RuiLog : " + time + " : " + hostName + " : " + appType + " : " + filteredRate + " : " + rawRte + " : " + sessionID);
      System.out.println("appRate: " + appRate);
      OffloadingReply reply = OffloadingReply.newBuilder()
          .setMessage("I am your father! \\\\(* W *)//")
          .build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }
  }
}
