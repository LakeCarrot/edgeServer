/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package edgeServer;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.logging.Logger;
import java.lang.Runtime;
import java.lang.Process;
import java.lang.Thread;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import edgeOffloading.OffloadingGrpc;
import edgeOffloading.OffloadingOuterClass.OffloadingRequest;
import edgeOffloading.OffloadingOuterClass.OffloadingReply;

public class EdgeServer {
  private static final Logger logger = Logger.getLogger(EdgeServer.class.getName());

  private Server server;
  private void start() throws IOException {
    /* The port on which the server should run */
    int port = 50051;
    server = ServerBuilder.forPort(port)
        .addService(new OffloadingImpl())
        .build()
        .start();
    logger.info("Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        EdgeServer.this.stop();
        System.err.println("*** server shut down");
      }
    });
  }

  private void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon threads.
   */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  /**
   * Main launches the server from the command line.
   */
  public static void main(String[] args) throws IOException, InterruptedException {
    final EdgeServer server = new EdgeServer();
    server.start();
    server.blockUntilShutdown();
  }

	static class OffloadingImpl extends OffloadingGrpc.OffloadingImplBase {

		@Override 
		public void startService(OffloadingRequest req, StreamObserver<OffloadingReply> responseObserver) {
			EdgeServer s = new EdgeServer();
			OffloadingReply reply = OffloadingReply.newBuilder()
				.setMessage("I am your father! \\\\(* W *)//")
				.build();
			Runtime rt = Runtime.getRuntime();
			try {
				// pull the container image
				String command = "docker pull bhu2017/facerec:1.0";
				System.out.println("pull the container");
				Process pr = rt.exec(command);
			} catch (IOException e) {
				Thread.currentThread().interrupt();
			}
			Thread t = s.new faceThread();
			t.start();
			responseObserver.onNext(reply);
			responseObserver.onCompleted();
		}
	}

	public class faceThread extends Thread {
		public void run() {
			Runtime rt = Runtime.getRuntime();
			try {
				// start to run the container 
				String command = "docker run -p 50052:50052 bhu2017/facerec:1.0";
				System.out.println("start the container");
				Process pr = rt.exec(command);
			} catch (IOException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}
