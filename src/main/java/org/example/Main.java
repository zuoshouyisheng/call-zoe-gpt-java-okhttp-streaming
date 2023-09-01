package org.example;


import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

import java.io.IOException;
import java.util.Objects;

class Main {

    public static void main(String[] args) {
        OkHttpClient okHttpClient = new OkHttpClient();
        String url = "https://openapi.zuoshouyisheng.com/gpt/v1/openai-compatible/v1/chat/completions";
        String token = "ZOE-xxxxxxxx";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-API-KEY", token)
                .post(RequestBody
                        .create(MediaType.parse("application/json"),
                                "{\"model\": \"zoe-gpt\", \"messages\": [{\"role\": \"system\", \"content\": \"你是一名全科医生\"}, {\"role\": \"user\", \"content\": \"肚子疼应该挂什么科室\"}], \"stream\": true}"
                        ))
                .build();

        EventSource.Factory factory = EventSources.createFactory(okHttpClient);
        EventSourceListener eventSourceListener = new EventSourceListener() {

            @Override
            public void onOpen(final EventSource eventSource, final Response response) {
                System.out.println("建立sse连接...");
            }


            @Override
            public void onEvent(final EventSource eventSource, final String id, final String type, final String data) {
                System.out.printf("data: [%s]\n", data);
            }


            @Override
            public void onClosed(final EventSource eventSource) {
                System.out.println("关闭sse连接...");
            }

            @Override
            public void onFailure(final EventSource eventSource, final Throwable t, final Response response) {

                String responseString = "";

                if (Objects.nonNull(response) && Objects.nonNull(response.body())) {
                    try {
                        responseString = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                System.out.printf("使用事件源时出现异常... response body：[%s]...", responseString);
            }
        };

        factory.newEventSource(request, eventSourceListener);
        System.out.println("已经发送请求...");

    }

}

