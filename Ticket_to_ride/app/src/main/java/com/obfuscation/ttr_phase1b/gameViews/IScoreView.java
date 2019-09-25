package com.obfuscation.ttr_phase1b.gameViews;

import java.util.ArrayList;

import communication.PlayerStats;

public interface IScoreView extends IView {

    void setInfo(ArrayList<PlayerStats> stats);

}
