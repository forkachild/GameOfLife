package com.suhel.gameoflife

import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.suhel.gameoflife.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isPlaying = false
    private var currentGen = 0

    private val timer: SimpleTimer = object : SimpleTimer() {
        override fun tick() {
            binding.screen.nextFrame()
            currentGen++
            binding.tvGen.text = currentGen.toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.btnPlayPause.setOnClickListener { playPause() }
        binding.btnClear.isEnabled = false
        binding.btnEdit.setOnClickListener {
            val currentEditableState = binding.screen.isEditable
            if (!currentEditableState && isPlaying) {
                setPausedState()
            }
            binding.btnPlayPause.isEnabled = currentEditableState
            binding.seeker.isEnabled = currentEditableState
            binding.screen.isEditable = !currentEditableState
            binding.btnClear.isEnabled = !currentEditableState
        }
        binding.btnClear.setOnClickListener {
            binding.screen.clear()
            currentGen = 0
            binding.tvGen.text = currentGen.toString()
        }
        binding.seeker.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                timer.delay = (1000 / (progress + 1)).toLong()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        timer.isPaused = true
        timer.start()
    }

    private fun playPause() {
        if (isPlaying) setPausedState() else setPlayingState()
    }

    private fun setPausedState() {
        isPlaying = false
        timer.isPaused = true
        binding.btnPlayPause.setImageResource(R.drawable.ic_play)
    }

    private fun setPlayingState() {
        isPlaying = true
        timer.isPaused = false
        binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
    }
}