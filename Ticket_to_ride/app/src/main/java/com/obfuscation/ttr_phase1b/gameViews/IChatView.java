package com.obfuscation.ttr_phase1b.gameViews;

import java.util.List;

import communication.Message;

/**
 * Created by jalton on 10/24/18.
 */

public interface IChatView extends IView {

    void setMessages(List<Message> messages);

    void setUsername(String username);

}
