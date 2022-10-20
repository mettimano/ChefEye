package com.example.ChefEye.ui.voice_search;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ChefEye.MainActivity;
import com.example.ChefEye.R;
import com.example.ChefEye.databinding.FragmentVoiceSearchBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class VoiceSearchFragment extends Fragment{

    private VoiceSearchViewModel voiceSearchViewModel;
    private FragmentVoiceSearchBinding binding;

    //public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    private EditText textView;
    private ImageView micButton;
    private TextView boxRicetta;
    private ImageView sendButton;
    private TextView tv_msg;
    private List<String> allowedWords = Arrays.asList( "apple pie","beef carpaccio","beignets","cannoli","caprese salad"
            ,"chocolate cake","cup cakes","falafel","gnocchi","greek salad"
            ,"guacamole","hamburger","hot dog","ice cream","lasagna","omelette"
            ,"oysters","paella","pancakes","panna cotta","pizza","risotto"
            ,"spaghetti bolognese","spaghetti carbonara","tacos","tiramisu","waffles");
    private Map<String,Boolean> shoppingList;
    private boolean attivo = false;

    public VoiceSearchFragment(){}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        voiceSearchViewModel =
                new ViewModelProvider(this).get(VoiceSearchViewModel.class);

        binding = FragmentVoiceSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if(ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        }

        textView = binding.textSearch;
        micButton = binding.buttonVoiceSearch;
        sendButton=binding.buttonTextSearch;
        boxRicetta = binding.boxRicetta;
        boxRicetta.setMovementMethod(new ScrollingMovementMethod());
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this.getContext(), ComponentName.unflattenFromString("com.google.android.googlequicksearchbox/com.google.android.voicesearch.serviceapi.GoogleRecognitionService"));

        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-EN");
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en_EN");





        if(getArguments()!=null && !getArguments().isEmpty() && getArguments().containsKey("plate")){
            String plate=getArguments().getString("plate");
            textView.setText(plate);
            setRicetta(plate);
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textView!=null && textView.toString()!=""){
                   String word=textView.getText().toString().toLowerCase(Locale.ROOT);

                   setRicetta(word);
                }
                else  Toast.makeText(getContext(),"Devi scrivere qualcosa!",Toast.LENGTH_LONG).show();

            }
        });


        speechRecognizer.setRecognitionListener(new RecognitionListener() {

            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {
                textView.setText("");
                textView.setHint("I'm listening...");
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {
                textView.setText("");
            }

            @Override
            public void onError(int error) {
                textView.setHint("I didn't understand, please try again");
            }

            @Override
            public void onResults(Bundle bundle) {
                micButton.setImageResource(R.drawable.ic_not_recording_mic);
                textView.setHint("Search");
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION); // prende i dati ricevuti dall'utente (utente parla al microfono)
                String words="";
                for (int i=0;i<data.size();i++){
                    words= words+data.get(i).toLowerCase(Locale.ROOT);
                    if(i!=data.size()-1) words= words+" ";
                }
                textView.setText(words);
                if (allowedWords.contains(words.toLowerCase())) {
                   setRicetta(words);
                }
                else{
                    Toast.makeText(getContext(),"Recepie not found!",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }

        });

        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (attivo==true){
                    attivo=false;
                    micButton.setImageResource(R.drawable.ic_not_recording_mic);
                    speechRecognizer.stopListening();
                }else if (attivo==false){
                    attivo=true;
                    micButton.setImageResource(R.drawable.ic_recording_mic);
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        speechRecognizer.destroy();
    }


    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.println(Log.WARN,"ok","ok");
                } else {
                    Log.println(Log.WARN,"err","error");
                }
            });


    public void setRicetta(String recepie){
        if(allowedWords.contains(recepie.toLowerCase())) {
            String recepieClass= recepie.replace(" ","_");
            String ricetta = ((MainActivity) requireActivity()).getRecepie(recepieClass);
            boxRicetta.setText(ricetta);
        }
        else{
            Toast.makeText(getContext(),"Recepie not found!",Toast.LENGTH_LONG).show();
        }
    }



}


