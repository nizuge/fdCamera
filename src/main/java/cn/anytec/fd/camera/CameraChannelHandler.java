package cn.anytec.fd.camera;


import cn.anytec.fd.camera.pojo.FDCameraData;
import cn.anytec.fd.config.GeneralConfig;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
@Sharable
public class CameraChannelHandler extends ChannelInboundHandlerAdapter {
	private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("1", CharsetUtil.ISO_8859_1));
	private static final Logger logger = LoggerFactory.getLogger(CameraChannelHandler.class);
	private ThreadLocal threadLocal = new ThreadLocal();

	@Autowired
	GeneralConfig config;
	
	public CameraChannelHandler() {}



	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info(ctx.channel().remoteAddress() + " connected");
		threadLocal.set(0);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		String camIp = getCameraIp(ctx);
		logger.info("Camera:{} offline",camIp);
		//FDCameraDataHandler.setCameraSessionOffline(clientIp);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg != null && msg instanceof FDCameraData) {
			logger.debug(getCameraIp(ctx));
			FDCameraData data = (FDCameraData)msg;
			logger.info(data.mStrMac);

		}
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			logger.info("");
			String cameraIp = getCameraIp(ctx);
			ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate()).addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) {
					if (!future.isSuccess()) {
						future.channel().close();
						//FDCameraDataHandler.setCameraSessionOffline(clientIp);
					}
				}
			});

		} else {
			super.userEventTriggered(ctx, evt);
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		String clientIp = getCameraIp(ctx);
		logger.error(cause.getMessage() , cause);
		logger.info("ExceptionCaught from {}", clientIp);// let camera reconnect
		ctx.channel().close();
	}

	
	private String getCameraIp(ChannelHandlerContext ctx) {
		return ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();
	}

	
	
}
