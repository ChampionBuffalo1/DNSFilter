package com.champ.dns;

import java.util.logging.Logger;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.handler.codec.dns.DatagramDnsQuery;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.dns.DatagramDnsResponse;
import io.netty.handler.codec.dns.DefaultDnsRawRecord;
import io.netty.handler.codec.dns.DnsQuestion;

public class DNSHandler extends SimpleChannelInboundHandler<DatagramDnsQuery> {
    private static final Logger logger = Logger.getLogger(DNSHandler.class.getName());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramDnsQuery query) {
        DnsQuestion dnsQuestion = query.recordAt(DnsSection.QUESTION);

        DatagramDnsResponse response = new DatagramDnsResponse(query.recipient(), query.sender(), query.id());

        response.addRecord(DnsSection.ANSWER, new DefaultDnsRawRecord(
                dnsQuestion.name(),
                DnsRecordType.A,
                60,
                Unpooled.buffer()
                        .writeBytes(new byte[] { 127, 0, 0, 1 })
        ));

        ctx.writeAndFlush(response).addListener((future) -> {
            if (!future.isSuccess())
                logger.severe(future.cause().getMessage());
        });
    }
}