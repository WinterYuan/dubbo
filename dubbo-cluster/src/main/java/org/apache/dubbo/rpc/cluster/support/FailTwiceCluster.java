package org.apache.dubbo.rpc.cluster.support;

import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.Cluster;
import org.apache.dubbo.rpc.cluster.Directory;

public class FailTwiceCluster implements Cluster {
    public final static String NAME = "failtwice";

    @Override
    public <T> Invoker<T> join(Directory<T> directory) throws RpcException {
        return new FailTwiceClusterInvoker<T>(directory);
    }
}
