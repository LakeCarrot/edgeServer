package edgeOffloading;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.8.0)",
    comments = "Source: offloading.proto")
public final class OffloadingGrpc {

  private OffloadingGrpc() {}

  public static final String SERVICE_NAME = "edgeOffloading.Offloading";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getStartServiceMethod()} instead. 
  public static final io.grpc.MethodDescriptor<edgeOffloading.OffloadingOuterClass.OffloadingRequest,
      edgeOffloading.OffloadingOuterClass.OffloadingReply> METHOD_START_SERVICE = getStartServiceMethod();

  private static volatile io.grpc.MethodDescriptor<edgeOffloading.OffloadingOuterClass.OffloadingRequest,
      edgeOffloading.OffloadingOuterClass.OffloadingReply> getStartServiceMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<edgeOffloading.OffloadingOuterClass.OffloadingRequest,
      edgeOffloading.OffloadingOuterClass.OffloadingReply> getStartServiceMethod() {
    io.grpc.MethodDescriptor<edgeOffloading.OffloadingOuterClass.OffloadingRequest, edgeOffloading.OffloadingOuterClass.OffloadingReply> getStartServiceMethod;
    if ((getStartServiceMethod = OffloadingGrpc.getStartServiceMethod) == null) {
      synchronized (OffloadingGrpc.class) {
        if ((getStartServiceMethod = OffloadingGrpc.getStartServiceMethod) == null) {
          OffloadingGrpc.getStartServiceMethod = getStartServiceMethod = 
              io.grpc.MethodDescriptor.<edgeOffloading.OffloadingOuterClass.OffloadingRequest, edgeOffloading.OffloadingOuterClass.OffloadingReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "edgeOffloading.Offloading", "startService"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  edgeOffloading.OffloadingOuterClass.OffloadingRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  edgeOffloading.OffloadingOuterClass.OffloadingReply.getDefaultInstance()))
                  .build();
          }
        }
     }
     return getStartServiceMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static OffloadingStub newStub(io.grpc.Channel channel) {
    return new OffloadingStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static OffloadingBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new OffloadingBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static OffloadingFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new OffloadingFutureStub(channel);
  }

  /**
   */
  public static abstract class OffloadingImplBase implements io.grpc.BindableService {

    /**
     */
    public void startService(edgeOffloading.OffloadingOuterClass.OffloadingRequest request,
        io.grpc.stub.StreamObserver<edgeOffloading.OffloadingOuterClass.OffloadingReply> responseObserver) {
      asyncUnimplementedUnaryCall(getStartServiceMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getStartServiceMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                edgeOffloading.OffloadingOuterClass.OffloadingRequest,
                edgeOffloading.OffloadingOuterClass.OffloadingReply>(
                  this, METHODID_START_SERVICE)))
          .build();
    }
  }

  /**
   */
  public static final class OffloadingStub extends io.grpc.stub.AbstractStub<OffloadingStub> {
    private OffloadingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private OffloadingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OffloadingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new OffloadingStub(channel, callOptions);
    }

    /**
     */
    public void startService(edgeOffloading.OffloadingOuterClass.OffloadingRequest request,
        io.grpc.stub.StreamObserver<edgeOffloading.OffloadingOuterClass.OffloadingReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getStartServiceMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class OffloadingBlockingStub extends io.grpc.stub.AbstractStub<OffloadingBlockingStub> {
    private OffloadingBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private OffloadingBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OffloadingBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new OffloadingBlockingStub(channel, callOptions);
    }

    /**
     */
    public edgeOffloading.OffloadingOuterClass.OffloadingReply startService(edgeOffloading.OffloadingOuterClass.OffloadingRequest request) {
      return blockingUnaryCall(
          getChannel(), getStartServiceMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class OffloadingFutureStub extends io.grpc.stub.AbstractStub<OffloadingFutureStub> {
    private OffloadingFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private OffloadingFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OffloadingFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new OffloadingFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<edgeOffloading.OffloadingOuterClass.OffloadingReply> startService(
        edgeOffloading.OffloadingOuterClass.OffloadingRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getStartServiceMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_START_SERVICE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final OffloadingImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(OffloadingImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_START_SERVICE:
          serviceImpl.startService((edgeOffloading.OffloadingOuterClass.OffloadingRequest) request,
              (io.grpc.stub.StreamObserver<edgeOffloading.OffloadingOuterClass.OffloadingReply>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (OffloadingGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .addMethod(getStartServiceMethod())
              .build();
        }
      }
    }
    return result;
  }
}
