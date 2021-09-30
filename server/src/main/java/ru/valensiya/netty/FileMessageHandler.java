package ru.valensiya.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.valensiya.core.Command;
import ru.valensiya.core.FileMessage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileMessageHandler extends SimpleChannelInboundHandler<Command> {

    private static Path ROOT = Paths.get("server", "root");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command cmd) throws Exception {/*
        Files.write(
                ROOT.resolve(fileMessage.getName()),
                fileMessage.getBytes()
        );

        ctx.writeAndFlush("OK");*/
        switch (cmd.getType()) {
            case FILE_MESSAGE:
            {

            }
        }
    }
}
