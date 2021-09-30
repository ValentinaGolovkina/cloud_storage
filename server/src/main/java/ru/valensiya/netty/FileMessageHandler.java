package ru.valensiya.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ru.valensiya.core.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileMessageHandler extends SimpleChannelInboundHandler<Command> {

    private static Path currentPath;

    public FileMessageHandler() throws IOException {
        currentPath = Paths.get("server", "root");
        if (!Files.exists(currentPath)) {
            Files.createDirectory(currentPath);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command) throws Exception {
        log.debug("received: {}", command.getType());
        switch (command.getType()) {
            case FILE_REQUEST:
                FileRequest fileRequest = (FileRequest) command;
                FileMessage msg = new FileMessage(currentPath.resolve(fileRequest.getName()));
                ctx.writeAndFlush(msg);
                break;
            case FILE_MESSAGE:
                FileMessage message = (FileMessage) command;
                Files.write(currentPath.resolve(message.getName()), message.getBytes());
                ctx.writeAndFlush(new ListResponse(currentPath));
                break;
            case PATH_REQUEST:
                ctx.writeAndFlush(new PathResponse(currentPath.toString()));
                break;
            case LIST_REQUEST:
                ctx.writeAndFlush(new ListResponse(currentPath));
                break;
        }
    }
}
