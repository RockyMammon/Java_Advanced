package com.rocky.project.outbound;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class OkhttpOutboundHandler {

    private static Logger logger = LoggerFactory.getLogger(OkhttpOutboundHandler.class);
    private OkHttpClient client;
    private String backendUrl;

    public OkhttpOutboundHandler(String backendUrl) {
        this.backendUrl = backendUrl;
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    public void handle(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx) throws Exception {
        final String url = this.backendUrl + fullRequest.uri();
        HttpHeaders headers = fullRequest.headers();
        Map<String, String> collect = headers.entries().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Headers newHeaders = Headers.of(collect).newBuilder().build();

        Request request = new Request.Builder().url(url).headers(newHeaders).build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        handleResponse(fullRequest, ctx, response);
    }

    private void handleResponse(final FullHttpRequest request, final ChannelHandlerContext ctx, final Response response) throws Exception {
        FullHttpResponse result = null;
        try {
            ResponseBody responseBody = response.body();
            byte[] body = responseBody.bytes();

            result = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(body));
            result.headers().set("Content-Type", "application/json");
            result.headers().setInt("Content-Length", body.length);
        } catch (Exception e) {
            e.printStackTrace();
            exceptionCaught(ctx, e);
        } finally {
            if (request != null) {
                if (!HttpUtil.isKeepAlive(request)) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    ctx.write(response);
                }
            }
            ctx.flush();
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}