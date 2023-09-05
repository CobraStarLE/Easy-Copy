//package com.cyser.test;
//
//import org.msgpack.core.MessageBufferPacker;
//import org.msgpack.core.MessagePack;
//import org.msgpack.core.MessageUnpacker;
//
//import java.io.IOException;
//
//public class MsgpackTest {
//
//    private static String join(String[] in) {
//        StringBuilder s = new StringBuilder();
//        for (int i = 0; i < in.length; ++i) {
//            if (i > 0) {
//                s.append(", ");
//            }
//            s.append(in[i]);
//        }
//        return s.toString();
//    }
//
//    public static void main(String[] args) throws IOException {
//        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
//        packer
//                .packInt(1)
//                .packString("leo")
//                .packArrayHeader(2)
//                .packString("xxx-xxxx")
//                .packString("yyy-yyyy");
//        packer.close(); // Never forget to close (or flush) the buffer
//
//        // Deserialize with MessageUnpacker
//        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(packer.toByteArray());
//        int id = unpacker.unpackInt(); // 1
//        String name = unpacker.unpackString(); // "leo"
//        int numPhones = unpacker.unpackArrayHeader(); // 2
//        String[] phones = new String[numPhones];
//        for (int i = 0; i < numPhones; ++i) {
//            phones[i] = unpacker.unpackString(); // phones = {"xxx-xxxx", "yyy-yyyy"}
//        }
//        unpacker.close();
//
//        System.out.printf("id:%d, name:%s, phone:[%s]%n", id, name, join(phones));
//    }
//}
