package common.nimbus.tournament;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import java.util.ArrayList;

public class PluginMessageReceiver {
    String subChannel;
    ArrayList<String> strings;

    public PluginMessageReceiver(byte[] bytes){
        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        this.subChannel = in.readUTF();
        this.strings = new ArrayList<>();
        while (true) {
            String str;
            try {
                str = in.readUTF();
            } catch (Exception e){break;}
            strings.add(str);
        }
    }

    public ArrayList<String> getStrings() {
        return strings;
    }

    public String getSubChannel() {
        return subChannel;
    }
}
