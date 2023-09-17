package com.champ.dns;

import java.net.InetAddress;
import java.util.logging.Logger;
import java.net.UnknownHostException;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.handler.codec.dns.DatagramDnsQuery;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.dns.DatagramDnsResponse;
import io.netty.handler.codec.dns.DefaultDnsRawRecord;

public class DNSHandler extends SimpleChannelInboundHandler<DatagramDnsQuery> {
    private static final Logger logger = Logger.getLogger(DNSHandler.class.getName());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramDnsQuery query) {
        DnsQuestion dnsQuestion = query.recordAt(DnsSection.QUESTION);
        DatagramDnsResponse response = new DatagramDnsResponse(query.recipient(), query.sender(), query.id());
        try {
            InetAddress addr = InetAddress.getByName(dnsQuestion.name());
            response.addRecord(DnsSection.ANSWER, new DefaultDnsRawRecord(
                    dnsQuestion.name(),
                    DnsRecordType.A,
                    60,
                    Unpooled.buffer().writeBytes(addr.getAddress())));

        } catch (UnknownHostException unk) {
            response.retain().release();
        }

        ctx.writeAndFlush(response).addListener((future) -> {
            if (!future.isSuccess())
                logger.severe(future.cause().getMessage());
        });
    }
}