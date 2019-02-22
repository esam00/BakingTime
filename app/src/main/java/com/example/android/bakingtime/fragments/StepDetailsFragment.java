package com.example.android.bakingtime.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.utils.PaneUtils;
import com.example.android.bakingtime.models.Recipe;
import com.example.android.bakingtime.models.Step;
import com.example.android.bakingtime.viewModel.DetailsViewModel;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/** A simple {@link Fragment} subclass. */
public class StepDetailsFragment extends Fragment {

  private static final String TAG = StepDetailsFragment.class.getSimpleName();
  private static final String PLAYER_POSITION = "playerPosition";
  private static final String PLAYER_PLAY_WHEN_READY = "playWhenReady";
  private DetailsViewModel viewDetailsViewModel;

  @BindView(R.id.playerView)
  SimpleExoPlayerView playerView;

  @BindView(R.id.description_text_view)
  TextView descriptionTextView;

  @BindView(R.id.last_step_button)
  @Nullable
  Button lastStepButton;

  @BindView(R.id.next_step_button)
  @Nullable
  Button nextStepButton;

  @BindView(R.id.no_video_text_view)
  TextView noVideoTextView;

  private SimpleExoPlayer exoPlayer;
  private MediaSessionCompat mediaSession;

  private Recipe recipe;
  private int stepId;

  private long playerPosition;
  private boolean playWhenReady = true;

  public StepDetailsFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (savedInstanceState != null) {
      playerPosition = savedInstanceState.getLong(PLAYER_POSITION);
      playWhenReady = savedInstanceState.getBoolean(PLAYER_PLAY_WHEN_READY);
      stepId = savedInstanceState.getInt("stepid");
    } else {
      stepId = getActivity().getIntent().getIntExtra("stepId", 0);
    }

    recipe = getActivity().getIntent().getParcelableExtra(getString(R.string.key_to_recipe));
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_step_details, container, false);
    ButterKnife.bind(this, view);

    return view;
  }

  private void setNavigateButtonVisibilities() {
    if (!PaneUtils.hasTwoPanes(getContext())) {
      lastStepButton.setVisibility(View.VISIBLE);
      nextStepButton.setVisibility(View.VISIBLE);

      if (stepId == 0) {
        lastStepButton.setVisibility(View.INVISIBLE);
      }
      if (stepId == recipe.getSteps().size() - 1) {
        nextStepButton.setVisibility(View.INVISIBLE);
      }
    }
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    ViewGroup.LayoutParams params = playerView.getLayoutParams();
    params.height = ((View) playerView.getParent()).getHeight();
    playerView.setLayoutParams(params);

    if (PaneUtils.isSinglePaneLandscape(getContext())) {
      ((ScrollView) playerView.getParent().getParent())
          .post(
              new Runnable() {
                @Override
                public void run() {
                  ViewGroup.LayoutParams params = playerView.getLayoutParams();
                  params.height = ((View) playerView.getParent()).getHeight();
                  playerView.setLayoutParams(params);
                  noVideoTextView.setLayoutParams(params);
                }
              });
    }
  }

  @Override
  public void onStart() {
    super.onStart();
    if (Util.SDK_INT > 23) {
      initializeMediaSession();
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    if (Util.SDK_INT <= 23 || exoPlayer == null) {
      initializeMediaSession();
    }
    // This is a hack to persist step because the viewModel initializes with 0.
    // Given time, I would start new StepDetailsFragment when step is changed instead
    // of maintaining state..
    int currentStep = stepId;
    initializeViewModel();
    viewDetailsViewModel.select(currentStep);
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putLong(PLAYER_POSITION, playerPosition);
    outState.putBoolean(PLAYER_PLAY_WHEN_READY, playWhenReady);
    outState.putInt("stepid", stepId);
  }

  @Optional
  @OnClick(R.id.last_step_button)
  public void toLast() {
    releasePlayer();
    initializeMediaSession();
    playerPosition = 0;
    playWhenReady = true;
    viewDetailsViewModel.select(--stepId);
  }

  @Optional
  @OnClick(R.id.next_step_button)
  public void toNext() {
    releasePlayer();
    initializeMediaSession();
    playerPosition = 0;
    playWhenReady = true;
    viewDetailsViewModel.select(++stepId);
  }

  /**
   * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
   * and media controller.
   */
  private void initializeMediaSession() {

    // Create a MediaSessionCompat.
    mediaSession = new MediaSessionCompat(getActivity(), TAG);

    // Enable callbacks from MediaButtons and TransportControls.
    mediaSession.setFlags(
        MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
            | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

    // Do not let MediaButtons restart the player when the app is not visible.
    mediaSession.setMediaButtonReceiver(null);

    // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
    PlaybackStateCompat.Builder stateBuilder =
        new PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY
                    | PlaybackStateCompat.ACTION_PAUSE
                    | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                    | PlaybackStateCompat.ACTION_PLAY_PAUSE);

    mediaSession.setPlaybackState(stateBuilder.build());

    // MySessionCallback has methods that handle callbacks from a media controller.
    // mediaSession.setCallback(new MySessionCallback());

    // Start the Media Session since the activity is active.
    mediaSession.setActive(true);

    if (exoPlayer == null) {
      // Create an instance of the ExoPlayer.
      TrackSelector trackSelector = new DefaultTrackSelector();
      LoadControl loadControl = new DefaultLoadControl();
      exoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
      playerView.setPlayer(exoPlayer);
    }
  }

  private void initializeViewModel() {
    viewDetailsViewModel = ViewModelProviders.of(getActivity()).get(DetailsViewModel.class);
    viewDetailsViewModel
        .getSelectedStepId()
        .observe(
            this,
            new Observer<Integer>() {
              @Override
              public void onChanged(@Nullable Integer stepId) {
                StepDetailsFragment.this.stepId = stepId;

                setNavigateButtonVisibilities();

                // Ideally should match on stepId
                Step step = recipe.getSteps().get(stepId);
                descriptionTextView.setText(step.getDescription());

                if (step.getVideoURL() == null || step.getVideoURL().isEmpty()) {
                  noVideoTextView.setVisibility(View.VISIBLE);
                  playerView.setVisibility(View.GONE);
                } else {
                  // Prepare the MediaSource.
                  String userAgent = Util.getUserAgent(getActivity(), "BakingTime");

                  MediaSource mediaSource =
                      new ExtractorMediaSource(
                          Uri.parse(step.getVideoURL()),
                          new DefaultDataSourceFactory(getActivity(), userAgent),
                          new DefaultExtractorsFactory(),
                          null,
                          null);
                  exoPlayer.prepare(mediaSource);
                  exoPlayer.seekTo(playerPosition);
                  exoPlayer.setPlayWhenReady(playWhenReady);

                  noVideoTextView.setVisibility(View.GONE);
                  playerView.setVisibility(View.VISIBLE);
                }
              }
            });
  }

  @Override
  public void onPause() {
    super.onPause();
    if (Util.SDK_INT <= 23) {
      releasePlayer();
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    if (Util.SDK_INT > 23) {
      releasePlayer();
    }
  }

  /** Release ExoPlayer. */
  private void releasePlayer() {
    if (exoPlayer != null) {
      playerPosition = exoPlayer.getCurrentPosition();
      playWhenReady = exoPlayer.getPlayWhenReady();
      exoPlayer.stop();
      exoPlayer.release();
      exoPlayer = null;
    }
  }
}
