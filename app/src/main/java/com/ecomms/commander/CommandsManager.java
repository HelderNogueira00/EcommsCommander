package com.ecomms.commander;

import java.util.Arrays;

import com.google.common.base.Enums;

public class CommandsManager {
    
    private NetworkCommander commander;

    public CommandsManager(NetworkCommander _commander) {

        this.commander = _commander;
    }

    public void sendPlayLocal(String _command) {

        String[] args = _command.split(" ");
        int type = -1;
        switch(args[1]) {

            case "local": type = EnumsList.ONPLAYREQUEST_LOCAL; break;
            case "radio": type = EnumsList.ONPLAYREQUEST_RADIO; break;
            case "device": type = EnumsList.ONPLAYREQUEST_DEVICE; break;
        }

        String song = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        NetworkPacket pck = new NetworkPacket(NetworkCommander.Commands.OnPlayRequest);
        pck.write(type);
        pck.write(song);
        commander.send(pck);
    }
}
