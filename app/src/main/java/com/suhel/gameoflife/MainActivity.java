package com.suhel.gameoflife;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;

import com.suhel.gameoflife.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private boolean isPlaying = false;
    private int currentGen = 0;
    private SimpleTimer timer = new SimpleTimer() {

        @Override
        public void tick() {
            binding.screen.nextFrame();
            currentGen++;
            binding.tvGen.setText(String.valueOf(currentGen));
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.btnPlayPause.setOnClickListener(v -> playPause());
        binding.btnClear.setEnabled(false);
        binding.btnEdit.setOnClickListener(v -> {
            boolean currentEditableState = binding.screen.isEditable();

            if (!currentEditableState && isPlaying) {
                setPausedState();
            }

            binding.btnPlayPause.setEnabled(currentEditableState);
            binding.seeker.setEnabled(currentEditableState);
            binding.screen.setEditable(!currentEditableState);
            binding.btnClear.setEnabled(!currentEditableState);
        });
        binding.btnClear.setOnClickListener(v -> {
            binding.screen.clear();
            currentGen = 0;
            binding.tvGen.setText(String.valueOf(currentGen));
        });

        binding.seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                timer.setDelay(1000 / (progress + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        timer.setPaused(true);
        timer.start();
    }

    private void playPause() {
        if (isPlaying)
            setPausedState();
        else
            setPlayingState();
    }

    private void setPausedState() {
        isPlaying = false;
        timer.setPaused(true);
        binding.btnPlayPause.setImageResource(R.drawable.ic_play);
    }

    private void setPlayingState() {
        isPlaying = true;
        timer.setPaused(false);
        binding.btnPlayPause.setImageResource(R.drawable.ic_pause);
    }

}
