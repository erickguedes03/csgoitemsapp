package br.com.guedes.csgoitemsapp.ui.detail

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import br.com.guedes.csgoitemsapp.R
import br.com.guedes.csgoitemsapp.databinding.ActivityDetailBinding
import br.com.guedes.csgoitemsapp.model.Item
import coil.load
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.material.snackbar.Snackbar

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private var player: ExoPlayer? = null
    private var progressBar: ProgressBar? = null
    private var currentVideoUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = findViewById(R.id.playerProgress)
        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val item = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("item", Item::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("item") as? Item
        }

        item?.let {
            binding.tvDetailName.text = it.name
            binding.tvDetailSub.text = it.subtext
            binding.tvDescription.text = it.description ?: "No description available."
            binding.imgDetail.load(it.image) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_background)
            }

            // Show extra summary for crates
            if (!it.extraSummary.isNullOrEmpty()) {
                binding.tvExtraSummary.text = it.extraSummary
                binding.tvExtraSummary.visibility = View.VISIBLE
            }

            currentVideoUrl = it.videoUrl
            // Use ExoPlayer for video playback when available
            if (!currentVideoUrl.isNullOrEmpty()) {
                binding.playerView.visibility = View.VISIBLE
                progressBar?.visibility = View.VISIBLE
                preparePlayer(currentVideoUrl!!)
            }
        }
    }

    private fun preparePlayer(url: String) {
        releasePlayer()
        try {
            player = ExoPlayer.Builder(this).build()
            binding.playerView.player = player
            val mediaItem = MediaItem.fromUri(url.toUri())
            player?.setMediaItem(mediaItem)
            player?.playWhenReady = true

            player?.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_BUFFERING -> progressBar?.visibility = View.VISIBLE
                        Player.STATE_READY -> progressBar?.visibility = View.GONE
                        Player.STATE_ENDED -> player?.seekTo(0)
                        else -> progressBar?.visibility = View.GONE
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    progressBar?.visibility = View.GONE
                    showRetrySnackbar("Erro ao reproduzir vídeo")
                }
            })

            player?.prepare()
        } catch (e: Exception) {
            e.printStackTrace()
            progressBar?.visibility = View.GONE
            showRetrySnackbar("Erro ao iniciar player")
        }
    }

    private fun showRetrySnackbar(message: String) {
        val root = binding.root
        Snackbar.make(root, message, Snackbar.LENGTH_INDEFINITE)
            .setAction("Retry") {
                currentVideoUrl?.let { preparePlayer(it) }
            }
            .show()
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    override fun onStart() {
        super.onStart()
        // re-prepare if needed
        currentVideoUrl?.let { if (player == null) preparePlayer(it) }
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }
}
