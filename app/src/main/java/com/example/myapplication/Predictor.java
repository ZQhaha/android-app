package com.example.myapplication;

import static com.example.myapplication.Utils.assetFilePath;
import android.content.Context;
import android.util.Log;

import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;
import org.pytorch.Tensor;

import java.io.IOException;

public class Predictor {
    float[] data = new float[1200];
    Module module = null;
    String[] gtType={"Bike","Sit","Standing","Walk","Up Stairs","Down Stairs"};
    public void init(Context appCtx, String modelPath){
        try {
            module = LiteModuleLoader.load(assetFilePath(appCtx, "model.pt"));
        } catch (IOException e) {
            Log.e("PytorchHelloWorld", "Error reading assets", e);
        }
    }

    public void setData(float[][] data_acc,float[][]data_gyro) {
        for(int i=0;i<20;i++){
            for(int j=0;j<10;j++){
                for(int k=0;k<3;k++){
                    data[60*i+3*j+k]=data_acc[k][10*i+j];
                    data[60*i+3*j+k+30]=data_gyro[k][10*i+j];
                }
            }
        }
    }

    public String run() {
        Tensor input_tensor = Tensor.fromBlob(data, new long[]{1, 1200});
        Tensor outputTensor = module.forward(IValue.from(input_tensor)).toTensor();
        float[] scores = outputTensor.getDataAsFloatArray();
        int index=0;
        float max=-Float.MAX_VALUE;
        for(int i=0;i<scores.length;i++){
            if(max<scores[i]){
                max=scores[i];
                index=i;
            }
        }
        return "预测的行为是"+gtType[index];
    }
}
