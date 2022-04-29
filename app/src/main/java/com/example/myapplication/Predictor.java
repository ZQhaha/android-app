package com.example.myapplication;

import android.content.Context;

import com.baidu.paddle.lite.MobileConfig;
import com.baidu.paddle.lite.PaddlePredictor;
import com.baidu.paddle.lite.Tensor;

import java.io.File;
import java.util.Arrays;

public class Predictor {
    protected long[] inputShape = new long[]{1, 600};
    protected float[] data=new float[600];
    protected float[] label;
    public PaddlePredictor paddlePredictor;
    protected String[] gtType = {"bike", "sit","stand","walk","down stairs","up stairs"};

    public void init(Context appCtx, String modelPath) {
        String realPath = appCtx.getCacheDir() + File.separator + modelPath;
        Utils.copyDirectoryFromAssets(appCtx, modelPath, realPath);
        MobileConfig config = new MobileConfig();
        config.setModelFromFile(realPath + File.separator + appCtx.getString(R.string.model_name));
        paddlePredictor = PaddlePredictor.createPaddlePredictor(config);
    }

    public void setData(float[][] data) {
        for(int j=0;j<3;j++){
            for(int i=0;i<200;i++){
                this.data[j*200+i] = data[j][i];
            }
        }
    }

    public String runModel() {

        Tensor inputTensor = paddlePredictor.getInput(0);
        inputTensor.resize(inputShape);
        inputTensor.setData(data);
        paddlePredictor.run();
        Tensor result = paddlePredictor.getOutput(0);
        float[] output = result.getFloatData();
        return "预测的行为是" + gtType[Utils.getMaxIndex(output)];

    }
}
