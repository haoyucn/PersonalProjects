package gamePresenters;

import com.obfuscation.ttr_phase1b.gameViews.IMenuView;

public class MenuPresenter implements IMenuPresenter {

    private IMenuView view;
    private MenuListener listener;

    public MenuPresenter(IMenuView view, MenuListener listener) {
        this.view = view;
        view.setPresenter(this);
        this.listener = listener;
    }

    @Override
    public void goBack() {
        listener.onBack();
    }

    @Override
    public void logout() {
        listener.onLogout();
    }

    @Override
    public void onGameSelection() {

    }

    @Override
    public void updateInfo(Object result) {
        view.updateUI();
    }

    @Override
    public void update() {

    }

    @Override
    public void showToast(String toast) {
        view.sendToast(toast);
    }
}
