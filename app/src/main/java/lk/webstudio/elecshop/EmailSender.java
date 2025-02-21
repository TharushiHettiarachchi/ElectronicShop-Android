package lk.webstudio.elecshop;
import okhttp3.*;

import java.io.IOException;

public class EmailSender {

    private static final String SERVER_URL = "https://1301-112-134-196-193.ngrok-free.app/ElectronicShop/SendEmail";

    public static void sendEmail(String email, String subject, String content, EmailCallback callback) {
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("email", email)
                .add("subject", subject)
                .add("content", content)
                .build();

        Request request = new Request.Builder()
                .url(SERVER_URL)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body().string());
                } else {
                    callback.onFailure("Request failed: " + response.code());
                }
            }
        });
    }

    public interface EmailCallback {
        void onSuccess(String response);
        void onFailure(String error);
    }
}
