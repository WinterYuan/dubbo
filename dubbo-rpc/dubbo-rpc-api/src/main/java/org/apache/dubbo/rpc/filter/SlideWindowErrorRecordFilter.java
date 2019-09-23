package org.apache.dubbo.rpc.filter;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.ListenableFilter;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.RpcStatus;

@Activate(group = CommonConstants.PROVIDER)
public class SlideWindowErrorRecordFilter extends ListenableFilter {

    private static final Logger logger = LoggerFactory.getLogger(SlideWindowErrorRecordFilter.class);

    private static final String ERROR_RECORD_FILTER_START_TIME = "error_record_filter_start_time";

    private static final int  METHOD_FAIL_ALERT_THRESHOLD = 5;

    public SlideWindowErrorRecordFilter() {
        super.listener = new SlideWindowErrorRecordFilter.SlideWindowListener();
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        final RpcStatus rpcStatus = RpcStatus.getStatus(invoker.getUrl(), invocation.getMethodName());
        if (rpcStatus.getFailed() > METHOD_FAIL_ALERT_THRESHOLD) {
            // return mock
            logger.warn("method:" + invocation.getMethodName() + " error response reach max threshold");
        }
        Result result = invoker.invoke(invocation);
        return result;
    }

    static class SlideWindowListener implements Listener {
        @Override
        public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {
            // throw exception into here?
            RpcStatus.endCount(invoker.getUrl(), invocation.getMethodName(), getElapsed(invocation), true);
        }

        @Override
        public void onError(Throwable t, Invoker<?> invoker, Invocation invocation) {
            RpcStatus.endCount(invoker.getUrl(), invocation.getMethodName(), getElapsed(invocation), false);
        }

        private long getElapsed(Invocation invocation) {
            String beginTime = invocation.getAttachment(ERROR_RECORD_FILTER_START_TIME);
            return StringUtils.isNotEmpty(beginTime) ? System.currentTimeMillis() - Long.parseLong(beginTime) : 0;
        }
    }
}
