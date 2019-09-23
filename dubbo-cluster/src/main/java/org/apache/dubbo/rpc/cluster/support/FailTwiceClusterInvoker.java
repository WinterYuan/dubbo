package org.apache.dubbo.rpc.cluster.support;

import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.AsyncRpcResult;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.Directory;
import org.apache.dubbo.rpc.cluster.LoadBalance;

import java.util.List;

public class FailTwiceClusterInvoker<T> extends AbstractClusterInvoker<T>{

    private static final Logger logger = LoggerFactory.getLogger(FailTwiceClusterInvoker.class);

    public FailTwiceClusterInvoker(Directory<T> directory) {
        super(directory);
    }

    @Override
    public Result doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadbalance) throws RpcException {
        try {
            checkInvokers(invokers, invocation);
            Invoker<T> invoker = select(loadbalance, invocation, invokers, null);
            return invoker.invoke(invocation);
        } catch (Throwable e) {
            try {
                checkInvokers(invokers, invocation);
                Invoker<T> invoker = select(loadbalance, invocation, invokers, null);
                return invoker.invoke(invocation);
            } catch (Throwable e2) {
                logger.error("Fail twice ignore exception: " + e.getMessage(), e);
                return AsyncRpcResult.newDefaultAsyncResult(null, null, invocation); // ignore
            }
        }
    }
}
