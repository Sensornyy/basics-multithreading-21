package com.artemchep.basics_multithreading;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.artemchep.basics_multithreading.cipher.Cipher;
import com.artemchep.basics_multithreading.cipher.ICipher;
import com.artemchep.basics_multithreading.cipher.TaskManager;
import com.artemchep.basics_multithreading.domain.Message;
import com.artemchep.basics_multithreading.domain.WithMillis;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<WithMillis<Message>> mList = new ArrayList<>();
    private MessageAdapter mAdapter = new MessageAdapter(mList);
    private final TaskManager<Cipher> manager = new TaskManager<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        manager.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.stop();
    }

    public void onPushBtnClick(View view) {
        Message message = Message.generate();
        insert(new WithMillis<>(message));
    }

    @UiThread
    public void insert(final WithMillis<Message> message) {
        mList.add(message);
        mAdapter.notifyItemInserted(mList.size() - 1);

        Cipher task = new Cipher(message.value.plainText, System.currentTimeMillis());
        task.setCipher(new ICipher() {
            @Override
            public void updateUI(String cypheredText, long executionTime) {
                final Message messageNew = message.value.copy(cypheredText); // call with cyphered text
                final WithMillis<Message> messageNewWithMillis = new WithMillis<>(messageNew, executionTime);  //call with elapsed time
                update(messageNewWithMillis);
            }
        });

        manager.addTask(task);
    }

    @UiThread
    public void update(final WithMillis<Message> message) {
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).value.key.equals(message.value.key)) {
                mList.set(i, message);
                mAdapter.notifyItemChanged(i);
                return;
            }
        }

        throw new IllegalStateException();
    }
}
