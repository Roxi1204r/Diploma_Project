package com.example.register1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DiseaseActivity extends AppCompatActivity {

    protected Interpreter tensorClassifier;

    private int imageResizeX;
    private int imageResizeY;
    private TensorImage inputImageBuffer;
    private TensorBuffer outputProbabilityBuffer;
    private TensorProcessor probabilityProcessor;

    private static final float IMAGE_MEAN = 0.0f;
    private static final float IMAGE_STD = 1.0f;
    private static final float PROBABILITY_MEAN = 0.0f;
    private static final float PROBABILITY_STD = 255.0f;

    private Bitmap bitmap;
    Uri imageUri;

    private List<String> labels;

    private TextView classitext;
    private ImageView image;
    private Button buclassify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease);

        classitext = findViewById(R.id.text);
        image = findViewById(R.id.image);
        buclassify = findViewById(R.id.button);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");  //used to pick an image, no matter its type
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 12);
            }
        });

        MappedByteBuffer classifierModel = null;
        try {
            classifierModel = FileUtil.loadMappedFile(DiseaseActivity.this, "model.tflite");
        } catch (IOException e) {
            e.printStackTrace();
        }
        tensorClassifier = new Interpreter(classifierModel, null);
//        try{
//            tensorClassifier = new Interpreter(loadmodelfile(this));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        buclassify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int imageTensorIndex = 0;
                int[] imageShape = tensorClassifier.getInputTensor(imageTensorIndex).shape();
                imageResizeY = imageShape[1];
                imageResizeX = imageShape[2];
                DataType imageDataType = tensorClassifier.getInputTensor(imageTensorIndex).dataType();

                int probabilityTensorIndex = 0;
                int[] probabilityShape = tensorClassifier.getOutputTensor(probabilityTensorIndex).shape();
                DataType probabilityDataType = tensorClassifier.getOutputTensor(probabilityTensorIndex).dataType();

                inputImageBuffer = new TensorImage(imageDataType);

                outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);
                probabilityProcessor = new TensorProcessor.Builder().add(getPostprocessNormalizeOp()).build();

                inputImageBuffer = loadImage(bitmap);

                tensorClassifier.run(inputImageBuffer.getBuffer(),outputProbabilityBuffer.getBuffer().rewind());
                showresult();
            }
        });
    }

    private TensorImage loadImage(final Bitmap bitmap){
        //loads bitmap into a tensor image
        inputImageBuffer.load(bitmap);

        //Creates processor for the TensorImage
        int cropSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
        ImageProcessor imageProcessor = new ImageProcessor.Builder()
                .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                .add(new ResizeOp(imageResizeX, imageResizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                .add(getPreprocessNormalizeOp())
                .build();
        return imageProcessor.process(inputImageBuffer);
    }

//    private MappedByteBuffer loadmodelfile(Activity activity) throws IOException{
//        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd("model.tflite");
//        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
//        FileChannel fileChannel = inputStream.getChannel();
//        long startoffset = fileDescriptor.getStartOffset();
//        long declaredLength = fileDescriptor.getDeclaredLength();
//        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startoffset, declaredLength);
//    }

    private TensorOperator getPreprocessNormalizeOp(){
        return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
    }
    private TensorOperator getPostprocessNormalizeOp(){
        return new NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD);
    }

    private void showresult(){
        try {
            labels = FileUtil.loadLabels(this, "labels.txt");
        }catch (Exception e){
            e.printStackTrace();
        }
        Map<String, Float> labeledProbability = new TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer)).getMapWithFloatValue();
        float maxValueInMap = (Collections.max(labeledProbability.values()));

        for(Map.Entry<String, Float> entry : labeledProbability.entrySet()){
            if (entry.getValue()==maxValueInMap){
                classitext.setText(entry.getKey());
            }
        }
//        tensorClassifier.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
// check if the request code is same as what is passed
        if(requestCode==12 && resultCode==RESULT_OK && data!=null){
            imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                image.setImageBitmap(bitmap);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}