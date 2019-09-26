package org.apache.dubbo.rpc.cluster.router.range;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.cluster.Router;
import org.apache.dubbo.rpc.cluster.RouterFactory;

public class MaskRouterFactory implements RouterFactory {
    public static final String NAME = "mask";

    @Override
    public Router getRouter(URL url) {
        return new MaskRouter(url);
    }
}
