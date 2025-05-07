package top.xfunny.meowcool.page.initial_page.ui.home;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import top.xfunny.meowcool.R;
import top.xfunny.meowcool.core.DatabaseManager;
import top.xfunny.meowcool.core.TransactionManager;
import top.xfunny.meowcool.core.data.SettingsManager;
import top.xfunny.meowcool.databinding.FragmentHomeBinding;
import top.xfunny.meowcool.page.initial_page.ui.home.BottomSheetDialog.AddTransactionsBottomSheetDialogFragment;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TextView tvDate;
    private TextView tvGreating;
    private TextView accumulate_payment;
    private TextView accumulate_revenue;
    private TextView tvResult;
    private Button analyzeBtn;

    private TransactionManager transactionManager;

    private final OkHttpClient client = new OkHttpClient();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    SettingsManager settingsManager;
    private String API_KEY;
    SQLiteDatabase db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        db = DatabaseManager.openDatabase(requireContext());
        settingsManager = new SettingsManager(requireContext());

        if(db != null){
            transactionManager = new TransactionManager(db);
        }


        API_KEY = settingsManager.getApiKey();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fab();
        tvDate();
        tvGreating();

        if(db!=null){
            tvAccumulate();
            tvResult();
            analyzeBtn();
        }else{
            Toast.makeText(requireContext(), "请先打开账套", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void fab() {
        ExtendedFloatingActionButton fab = binding.fabAdd;
        fab.setVisibility(db != null? View.VISIBLE:View.GONE);
        fab.setOnClickListener(
                v -> {
                    AddTransactionsBottomSheetDialogFragment bottomSheet = new AddTransactionsBottomSheetDialogFragment();
                    bottomSheet.show(getChildFragmentManager(), "AddTransactionsBottomSheetDialogFragment");
                }
        );
    }

    private void tvDate(){
        tvDate = requireView().findViewById(R.id.tv_date);
        long time = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
        String formattedDate = sdf.format(new Date(time));
        tvDate.setText(formattedDate);
    }

    private void tvGreating(){
        tvGreating = requireView().findViewById(R.id.tv_greating);
        long time = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("HH", Locale.getDefault());
        String formattedDate = sdf.format(new Date(time));
        int hour = Integer.parseInt(formattedDate);
        if (hour >= 0 && hour < 4) {
            tvGreating.setText("夜深了");
        } else if(hour >= 12 && hour < 14) {
            tvGreating.setText("中午好!");
        } else if(hour >= 14 && hour < 18) {
            tvGreating.setText("下午好!");
        } else if(hour >= 18 && hour < 24) {
            tvGreating.setText("晚上好!");
        } else {
            tvGreating.setText("夜深了");
        }
    }

    @SuppressLint("SetTextI18n")
    private void tvAccumulate(){
        accumulate_payment = requireView().findViewById(R.id.accumulate_payment);
        accumulate_revenue = requireView().findViewById(R.id.accumulate_revenue);
        BigDecimal payment = transactionManager.getAccumulatedPaymentOrRevenue(requireContext(),-1);
        BigDecimal revenue = transactionManager.getAccumulatedPaymentOrRevenue(requireContext(),1);
        accumulate_payment.setText("¥ "+payment);
        accumulate_revenue.setText("¥ "+revenue);
    }

    private void tvResult(){
        tvResult = requireView().findViewById(R.id.tvResult);

    }

    private void analyzeBtn(){
        analyzeBtn = requireView().findViewById(R.id.btnAnalyze);
        analyzeBtn.setOnClickListener(v -> {
            tvResult.setText("正在分析中,请确保网络畅通");
            sendStreamingRequest();

        });
    }

    private void sendStreamingRequest() {
        // 构建请求体
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "deepseek-chat");
            jsonBody.put("messages", new JSONArray()
                    .put(new JSONObject()
                            .put("role", "system")
                            .put("content", "你是一名专业的个人高级财务顾问,致力于为用户进行财务分析"))
                    .put(new JSONObject()
                            .put("role", "user")
                            .put("content", transactionManager.getAllTransactionPrompt())));
            jsonBody.put("stream", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .url("https://api.deepseek.com/chat/completions")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(RequestBody.create(
                        jsonBody.toString(),
                        MediaType.parse("application/json"))
                )
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                updateUI("请求失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    updateUI("响应错误: " + response.code());
                    return;
                }

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.body().byteStream()))) {

                    String line;
                    StringBuilder fullContent = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        String data = parseStreamLine(line);
                        if (data != null && !data.isEmpty()) {
                            fullContent.append(data);
                            updateUI(fullContent.toString());
                        }
                    }
                } catch (JSONException e) {
                    updateUI("解析错误: " + e.getMessage());
                }
            }
        });
    }

    private String parseStreamLine(String line) throws JSONException {
        // 示例流式数据格式：data: {"choices":[{"delta":{"content":"Hi"}}]}
        if (line.startsWith("data: ")) {
            String jsonStr = line.substring(6).trim();
            if (jsonStr.equals("[DONE]")) return "";

            JSONObject json = new JSONObject(jsonStr);
            return json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("delta")
                    .optString("content", "");
        }
        return "";
    }

    private void updateUI(String text) {
        mainHandler.post(() -> tvResult.setText(text));
    }
}