package com.obfuscation.ttr_phase1b.gameViews;

import java.util.ArrayList;
import java.util.List;

import communication.GameHistory;

public interface IHistoryView extends IView {

    void setHistory(List<GameHistory> history);

}
