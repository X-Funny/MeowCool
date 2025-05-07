package top.xfunny.meowcool.core.data;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class SettingsManager {
    private static SharedPreferences sharedPref;

    public SettingsManager(Context context) {
        sharedPref = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
    }

    public void setPRSubject(String uuid) {
        List<String> subjectUuidList = getPRSubject();
        subjectUuidList.add(uuid);
        sharedPref.edit().putString("PRSubject", String.join(",", subjectUuidList)).apply();
    }

    public void updatePRSubject(List<String> subjectUuidList) {
        sharedPref.edit().putString("PRSubject", String.join(",", subjectUuidList)).apply();
    }

    public List<String> getPRSubject() {
        String subjects = sharedPref.getString("PRSubject", "");
        ArrayList<String> subjectUuidList = new ArrayList<>(List.of(subjects.split(",")));
        subjectUuidList.removeIf(String::isEmpty); // 过滤掉空字符串
        return subjectUuidList;
    }

    public void setApiKey(String apiKey) {
        sharedPref.edit().putString("ApiKey", apiKey).apply();
    }

    public String getApiKey() {
        return sharedPref.getString("ApiKey", "");
    }

}
