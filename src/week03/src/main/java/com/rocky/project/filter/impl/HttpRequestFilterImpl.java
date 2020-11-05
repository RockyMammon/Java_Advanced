package com.rocky.project.filter.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rocky.project.filter.HttpRequestFilter;

public class HttpRequestFilterImpl implements HttpRequestFilter{
    private static Logger logger = LoggerFactory.getLogger(HttpRequestFilterImpl.class);

    @Override
    public void filter(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {

        fullRequest.headers().set("nid","liuli");
    }
}
