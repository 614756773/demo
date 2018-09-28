package helloworld;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

public class NioServer {

    private static Selector selector;
    private static ByteBuffer buffer=ByteBuffer.allocate(1024);
    public static void main(String[] args) throws IOException {
            selector = SelectorProvider.provider().openSelector();
            //创建一个新的server socket，设置为非阻塞模式
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);

            // 绑定server sokcet到本机和对应的端口
            InetAddress inetAddress = InetAddress.getLocalHost();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, 8900);
            serverSocketChannel.socket().bind(inetSocketAddress);

            SelectionKey acceptKey = serverSocketChannel.register(selector,
                    SelectionKey.OP_ACCEPT);

            int seletcorKey = 0;

            // 当任何一个注册事件发生的时候，select就会返回
            while ((seletcorKey = selector.select()) > 0) {
                Iterator iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = (SelectionKey) iterator.next();
                    iterator.remove();
                    process(key);
                }
            }
    }

    private static void process(SelectionKey key) throws IOException {
        // 接收请求
        if (key.isAcceptable()) {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel channel = server.accept();
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
        }
        // 读信息
        else if (key.isReadable()) {
            SocketChannel channel = (SocketChannel) key.channel();
            int count = channel.read(buffer);
            if (count > 0) {
                buffer.flip();
                channel.read(buffer);
                byte[] data=buffer.array();
                String msg=new String(data);
                System.out.println("服务端收到来自客户端的消息："+msg);
                channel.register(selector,SelectionKey.OP_WRITE);
            } else {
                channel.close();
            }
            buffer.clear();
        }
        // 写事件
        else if (key.isWritable()) {
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer byteBuffer=ByteBuffer.wrap(("hello，我是服务端").getBytes());
            channel.write(byteBuffer);
            channel.close();
            // 服务端发送消息后就得关掉这个channel，不然就弄成长连接了
            // 如果没关掉的话，key仍然是可写的状态，主程序跳出37-41后又去重新获得事件，
            // 事件仍然是可读的，所以就这样无限的向客户端写数据
        }
    }
}