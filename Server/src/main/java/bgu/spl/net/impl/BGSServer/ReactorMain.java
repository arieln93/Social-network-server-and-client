package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.api.bidi.UserDataBase;
import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main (String[] args){
        if (args.length<2){
            throw  new IllegalArgumentException("please supply port and thread number");
        }
        UserDataBase dataBase = new UserDataBase();
        Server.reactor(
                Integer.parseInt(args[1]),
                Integer.parseInt(args[0]),
                () -> new BidiMessagingProtocolImpl(dataBase),
                MessageEncoderDecoderImpl::new
        ).serve();
    }
}
