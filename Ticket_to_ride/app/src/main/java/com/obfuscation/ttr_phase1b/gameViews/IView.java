package com.obfuscation.ttr_phase1b.gameViews;

import com.obfuscation.ttr_phase1b.activity.IPresenter;

public interface IView {

    void updateUI();

    void setPresenter(IPresenter presenter);

    void sendToast(String toast);

}
