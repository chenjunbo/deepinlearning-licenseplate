package com.yoobee.licenseplate.train;

import com.google.common.collect.Sets;
import com.yoobee.licenseplate.constant.Constant;
import com.yoobee.licenseplate.util.FileUtil;
import com.yoobee.licenseplate.util.PlateUtil;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.ml.ANN_MLP;
import org.opencv.ml.Ml;
import org.opencv.ml.TrainData;

import java.util.Set;
import java.util.Vector;


/**
 * 基于org.opencv官方包实现的训练
 * 图片文字识别训练
 * 训练出来的库文件，用于识别图片中的中文字符
 *
 * @author jackiechan
 */
public class CnANNTrain {

    private ANN_MLP ann = ANN_MLP.create();

    static {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.load("/usr/local/share/java/opencv4/libopencv_java4100.dylib");
    }

    // 默认的训练操作的根目录
    private static final String DEFAULT_PATH = "D:/PlateDetect/train/chars_sample/";

    // 训练模型文件保存位置
    private static final String MODEL_PATH = DEFAULT_PATH + "ann_cn.xml";


    public void train(int _predictsize, int _neurons) {
        Mat samples = new Mat(); // 使用push_back，行数列数不能赋初始值
        Vector<Integer> trainingLabels = new Vector<Integer>();

        Set<String> sampleDir = Sets.newHashSet();

        // 加载汉字字符
        for (int i = 0; i < Constant.STR_CHINESE.length; i++) {
            sampleDir.clear();
            sampleDir.add(DEFAULT_PATH + "chars_blue_new/" + Constant.STR_CHINESE[i]);
            Vector<String> files = new Vector<String>();
            for (String str : sampleDir) {
                FileUtil.getFiles(str, files);
            }
            for (String filename : files) {
                Mat img = Imgcodecs.imread(filename, 0);
                // 原图样本
                samples.push_back(PlateUtil.features(img, _predictsize));
                trainingLabels.add(i);
            }
        }

        samples.convertTo(samples, CvType.CV_32F);

        //440   vhist.length + hhist.length + lowData.cols() * lowData.rows();
        // CV_32FC1 CV_32SC1 CV_32F
        Mat classes = Mat.zeros(trainingLabels.size(), Constant.STR_CHINESE.length, CvType.CV_32F);

        float[] labels = new float[trainingLabels.size()];
        for (int i = 0; i < labels.length; ++i) {
            classes.put(i, trainingLabels.get(i), 1.f);
        }

        // samples.type() == CV_32F || samples.type() == CV_32S
        TrainData train_data = TrainData.create(samples, Ml.ROW_SAMPLE, classes);

        ann.clear();
        Mat layers = new Mat(1, 3, CvType.CV_32F);
        layers.put(0, 0, samples.cols());   // 样本特征数 140  10*10 + 20+20
        layers.put(0, 1, _neurons); // 神经元个数
        layers.put(0, 2, classes.cols());   // 字符数

        ann.setLayerSizes(layers);
        ann.setActivationFunction(ANN_MLP.SIGMOID_SYM, 1, 1);
        ann.setTrainMethod(ANN_MLP.BACKPROP);
        TermCriteria criteria = new TermCriteria(/*TermCriteria.EPS + */TermCriteria.MAX_ITER, 300000, 0.0001);
        ann.setTermCriteria(criteria);
        ann.setBackpropWeightScale(0.1);
        ann.setBackpropMomentumScale(0.1);
        ann.train(train_data);

        // FileStorage fsto = new FileStorage(MODEL_PATH, FileStorage.WRITE);
        // ann.write(fsto, "ann");
        ann.save(MODEL_PATH);
    }


    public void predict() {
        ann.clear();
        ann = ANN_MLP.load(MODEL_PATH);

        int total = 0;
        int correct = 0;

        Set<String> sampleDir = Sets.newHashSet();

        // 遍历测试样本下的所有文件，计算预测准确率
        for (int i = 0; i < Constant.STR_CHINESE.length; i++) {

            String strChinese = Constant.STR_CHINESE[i];
            sampleDir.clear();
            sampleDir.add(DEFAULT_PATH + "chars_blue_new/" + strChinese);


            Vector<String> files = new Vector<String>();
            for (String str : sampleDir) {
                FileUtil.getFiles(str, files);
            }

            for (String filePath : files) {
                Mat img = Imgcodecs.imread(filePath, 0);
                Mat f = PlateUtil.features(img, Constant.PREDICT_SIZE);

                int index = 0;
                double maxVal = -2;

                Mat output = new Mat(1, Constant.STR_CHINESE.length, CvType.CV_32F);
                ann.predict(f, output);  // 预测结果
                for (int j = 0; j < Constant.STR_CHINESE.length; j++) {
                    double val = output.get(0, j)[0];
                    if (val > maxVal) {
                        maxVal = val;
                        index = j;
                    }
                }

                // 腐蚀  -- 识别中文字符效果会好一点，识别数字及字母效果会更差
                /*f = PlateUtil.features(ImageUtil.erode(img, false, null, 2, 2), Constant.predictSize);
                ann.predict(f, output);  // 预测结果
                for (int j = 0; j < Constant.strChinese.length; j++) {
                    double val = output.get(0, j)[0];
                    if (val > maxVal) {
                        maxVal = val;
                        index = j;
                    }
                }*/

                String result = Constant.STR_CHINESE[index];

                if (result.equals(strChinese)) {
                    correct++;
                } else {
                    System.err.print(filePath);
                    System.err.println("\t预测结果：" + Constant.KEY_CHINESE_MAP.get(result));
                }
                total++;
            }
        }
        System.out.print("total:" + total);
        System.out.print("\tcorrect:" + correct);
        System.out.print("\terror:" + (total - correct));
        System.out.println("\t计算准确率为：" + correct / (total * 1.0));

        return;
    }


    public static void main(String[] args) {

        CnANNTrain annT = new CnANNTrain();

        // 这里演示只训练model文件夹下的ann.xml，此模型是一个predictSize=10,neurons=40的ANN模型
        // 可根据需要训练不同的predictSize或者neurons的ANN模型
        // 根据机器的不同，训练时间不一样，但一般需要10分钟左右，所以慢慢等一会吧
        // 可以考虑中文，数字字母分开训练跟识别，提高准确性
        annT.train(Constant.PREDICT_SIZE, Constant.NEURONS);

        annT.predict();

        System.out.println("The end.");
        return;
    }


}
