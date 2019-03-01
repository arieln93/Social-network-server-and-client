package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.api.bidi.UserDataBase;
import bgu.spl.net.srv.Server;

public class TPCMAin {
    public static void main (String[] args){
        if (args.length<1){
            throw  new IllegalArgumentException("please supply port");
        }
        UserDataBase dataBase = new UserDataBase();
        Server.threadPerClient(
                Integer.parseInt(args[0]),
                () -> new BidiMessagingProtocolImpl(dataBase),
                MessageEncoderDecoderImpl::new
        ).serve();
    }
}
