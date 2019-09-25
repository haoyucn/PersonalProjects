package com.obfuscation.ttr_phase1b.gameViews;

import java.util.List;

import communication.Player;
import communication.PlayerOpponent;

/**
 * Created by jalton on 10/24/18.
 */

public interface IPlayerInfoView extends IView {

    void setPlayers(List<PlayerOpponent> players);

}
