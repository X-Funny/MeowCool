package top.xfunny.meowcool.page.subject_management_page;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SubjectManagementViewModel extends ViewModel {
    private final MutableLiveData<Boolean> needAssetRefresh = new MutableLiveData<>();
    private final MutableLiveData<Boolean> needLiabilityRefresh = new MutableLiveData<>();
    private final MutableLiveData<Boolean> needEquityRefresh = new MutableLiveData<>();
    private final MutableLiveData<Boolean> needProfitLossRefresh = new MutableLiveData<>();


    public MutableLiveData<Boolean> getNeedAssetRefresh() {
        return needAssetRefresh;
    }

    public void setNeedAssetRefresh(boolean needAssetRefresh) {
        this.needAssetRefresh.setValue(needAssetRefresh);
    }

    public MutableLiveData<Boolean> getNeedLiabilityRefresh() {
        return needLiabilityRefresh;
    }

    public void setNeedLiabilityRefresh(boolean needLiabilityRefresh) {
        this.needLiabilityRefresh.setValue(needLiabilityRefresh);
    }

    public MutableLiveData<Boolean> getNeedEquityRefresh() {
        return needEquityRefresh;
    }

    public void setNeedEquityRefresh(boolean needEquityRefresh) {
        this.needEquityRefresh.setValue(needEquityRefresh);
    }

    public MutableLiveData<Boolean> getNeedProfitLossRefresh() {
        return needProfitLossRefresh;
    }

    public void setNeedProfitLossRefresh(boolean needProfitLossRefresh) {
        this.needProfitLossRefresh.setValue(needProfitLossRefresh);
    }

    public void refreshAll() {
        setNeedAssetRefresh(true);
        setNeedLiabilityRefresh(true);
        setNeedEquityRefresh(true);
        setNeedProfitLossRefresh(true);
    }


}
