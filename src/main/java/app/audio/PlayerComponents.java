package app.audio;

import app.components.audio.AudioInfoViewer;
import app.components.buttons.control.LikeButton;
import app.components.buttons.control.RepeatButton;
import app.components.buttons.control.ShuffleButton;
import app.components.buttons.playback.NextButton;
import app.components.buttons.playback.PlayButton;
import app.components.buttons.playback.PrevButton;
import app.components.buttons.playback.VolumeButton;
import app.components.PlaybackBar;
import org.jetbrains.annotations.NotNull;

public class PlayerComponents {
    private @NotNull AudioInfoViewer audioInfoViewer;
    private @NotNull PlaybackBar playbackBar;
    private @NotNull PrevButton prevButton;
    private @NotNull ShuffleButton shuffleButton;
    private @NotNull PlayButton playButton;
    private @NotNull RepeatButton repeatButton;
    private @NotNull NextButton nextButton;
    private @NotNull LikeButton likeButton;
    private @NotNull VolumeButton volumeButton;

    public PlayerComponents(AudioInfoViewer audioInfoViewer, @NotNull PlaybackBar playbackBar, @NotNull PrevButton prevButton, @NotNull ShuffleButton shuffleButton, @NotNull PlayButton playButton, @NotNull RepeatButton repeatButton, @NotNull NextButton nextButton, @NotNull LikeButton likeButton, @NotNull VolumeButton volumeButton) {
        this.audioInfoViewer = audioInfoViewer;
        this.playbackBar = playbackBar;
        this.prevButton = prevButton;
        this.shuffleButton = shuffleButton;
        this.playButton = playButton;
        this.repeatButton = repeatButton;
        this.nextButton = nextButton;
        this.likeButton = likeButton;
        this.volumeButton = volumeButton;
    }

    public PlaybackBar getPlaybackBar() {
        return playbackBar;
    }

    public PlayerComponents setPlaybackBar(@NotNull PlaybackBar playbackBar) {
        this.playbackBar = playbackBar;
        return this;
    }

    public @NotNull AudioInfoViewer getAudioInfoViewer() {
        return audioInfoViewer;
    }

    public PlayerComponents setAudioInfoViewer(@NotNull AudioInfoViewer audioInfoViewer) {
        this.audioInfoViewer = audioInfoViewer;
        return this;
    }

    public @NotNull PrevButton getPrevButton() {
        return prevButton;
    }

    public PlayerComponents setPrevButton(@NotNull PrevButton prevButton) {
        this.prevButton = prevButton;
        return this;
    }

    public @NotNull ShuffleButton getShuffleButton() {
        return shuffleButton;
    }

    public PlayerComponents setShuffleButton(ShuffleButton shuffleButton) {
        this.shuffleButton = shuffleButton;
        return this;
    }

    public PlayButton getPlayButton() {
        return playButton;
    }

    public PlayerComponents setPlayButton(PlayButton playButton) {
        this.playButton = playButton;
        return this;
    }

    public RepeatButton getRepeatButton() {
        return repeatButton;
    }

    public PlayerComponents setRepeatButton(RepeatButton repeatButton) {
        this.repeatButton = repeatButton;
        return this;
    }

    public NextButton getNextButton() {
        return nextButton;
    }

    public PlayerComponents setNextButton(NextButton nextButton) {
        this.nextButton = nextButton;
        return this;
    }

    public VolumeButton getVolumeButton() {
        return volumeButton;
    }

    public PlayerComponents setVolumeButton(VolumeButton volumeButton) {
        this.volumeButton = volumeButton;
        return this;
    }

    public LikeButton getLikeButton() {
        return likeButton;
    }

    public PlayerComponents setLikeButton(LikeButton likeButton) {
        this.likeButton = likeButton;
        return this;
    }
}
