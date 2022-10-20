package com.example.ChefEye.ui.image_search;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.ChefEye.MainActivity;
import com.example.ChefEye.R;
import com.example.ChefEye.databinding.FragmentIcCameraBinding;


public class ic_cameraFragment extends Fragment {
    private FragmentIcCameraBinding binding;
    ImageView imageView;
    Button pictureButton;
    TextView result;
    TextView confidence;
    Button searchRecipeButton;
    String[] classes;
    int imageSize = 224;
    public ic_cameraFragment(){}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,Bundle savedInstanceState) {
        super.onCreate(null);

        binding = FragmentIcCameraBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        result = binding.result;
        imageView = binding.imageView;
        pictureButton = binding.takePictureButton;
        searchRecipeButton =binding.searchIcRecipeButton;


        pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch camera if we have permission
                if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                } else {
                    //Request camera permission if we don't have it.
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });
        searchRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(result.getText()==null || result.getText().toString().length()==0){
                    Toast.makeText(getContext(),"You need to take a picture of the plate!",Toast.LENGTH_LONG).show();
                }
                else if(result.getText()=="Unknown") Toast.makeText(getContext(),"Can't recognise the plate!",Toast.LENGTH_LONG).show();
                else{
                    Bundle bundle= new Bundle();
                    bundle.putString("plate",result.getText().toString());
                  Navigation.findNavController(view).navigate(R.id.action_navigation_image_search_to_navigation_voice_search,bundle);
                }
            }
        });
        return root;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String res;
        if (requestCode == 1 && resultCode == -1) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            res=((MainActivity)requireActivity()).classifyImage(image);
            if(res=="none") result.setText("Unknown");
            else {
                String resultString = res.replace("_", " ");
                result.setText(resultString);
            }
        }

    }


}
