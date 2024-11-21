package top.xfunny.meowcool.account_management_page;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AccountActivityViewModel extends ViewModel {
     public MutableLiveData<Boolean> allowSaving = new MutableLiveData<>(false);
}
