package com.yoobee.licenseplate.train;

import java.io.File;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.Ml;
import org.opencv.ml.SVM;
import org.opencv.ml.TrainData;

import com.google.common.collect.Lists;
import com.yoobee.licenseplate.constant.Constant;
import com.yoobee.licenseplate.enumtype.Direction;
import com.yoobee.licenseplate.util.FileUtil;
import com.yoobee.licenseplate.util.GenerateIdUtil;

/**
 * 基于org.opencv官方包实现的训练
 * <p>
 * 图片识别车牌训练
 * 训练出来的xml模型文件，用于判断切图是否是车牌
 *
 * @author jackiechan
 * @date 2024-09-16
 */
public class SVMTrain {

    // 默认的训练操作的根目录
    private static final String DEFAULT_PATH = Constant.DEFAULT_DIR + "train/";

    // 训练模型文件保存位置
    private static final String MODEL_PATH = DEFAULT_PATH + GenerateIdUtil.getStrId() + "_svm.xml";

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] arg) {
        // 训练， 生成svm.xml库文件
        train();

        // 识别，判断样本文件是否是车牌
        // predict();
    }


    /**
     * 样本文件包含：
     * 1、【蓝牌车牌】样本；5899个样本
     * 2、【绿牌车牌】样本；773 个样本
     * 3、【白色警用牌】样本；7个样本
     * 4、【黄牌车牌】样本；288个样本
     * 5、无车牌样本，负样本；4826个样本
     */
    public static void train() {

        // 正样本  // 136 × 36 像素  训练的源图像文件要相同大小
        // List<File> imgList0 = FileUtil.listFile(new File(DEFAULT_PATH + "/learn/HasPlate"), Constant.DEFAULT_TYPE, false);

        List<File> imgList0 = Lists.newArrayList();
        imgList0.addAll(FileUtil.listFile(new File(DEFAULT_PATH + "plate_sample/blue_old"), Constant.DEFAULT_TYPE, false));
        imgList0.addAll(FileUtil.listFile(new File(DEFAULT_PATH + "plate_sample/blue_new"), Constant.DEFAULT_TYPE, false));
        imgList0.addAll(FileUtil.listFile(new File(DEFAULT_PATH + "plate_sample/green"), Constant.DEFAULT_TYPE, false));
        imgList0.addAll(FileUtil.listFile(new File(DEFAULT_PATH + "plate_sample/white"), Constant.DEFAULT_TYPE, false));
        imgList0.addAll(FileUtil.listFile(new File(DEFAULT_PATH + "plate_sample/yellow_old"), Constant.DEFAULT_TYPE, false));

        // 负样本   // 136 × 36 像素 训练的源图像文件要相同大小
        // List<File> imgList1 = FileUtil.listFile(new File(DEFAULT_PATH + "/learn/NoPlate"), Constant.DEFAULT_TYPE, false);
        List<File> imgList1 = Lists.newArrayList();
        imgList1.addAll(FileUtil.listFile(new File(DEFAULT_PATH + "plate_sample/no_plate_blue"), Constant.DEFAULT_TYPE, false));
        imgList1.addAll(FileUtil.listFile(new File(DEFAULT_PATH + "plate_sample/no_plate_green"), Constant.DEFAULT_TYPE, false));
        imgList1.addAll(FileUtil.listFile(new File(DEFAULT_PATH + "plate_sample/no_plate_old"), Constant.DEFAULT_TYPE, false));

        // 标记：正样本用 0 表示，负样本用 1 表示。
        int labels[] = createLabelArray(imgList0.size(), imgList1.size());
        int sample_num = labels.length; // 图片数量

        // 用于存放所有样本的矩阵
        Mat trainingDataMat = null;

        // 存放标记的Mat,每个图片都要给一个标记
        Mat labelsMat = new Mat(sample_num, 1, CvType.CV_32SC1);
        labelsMat.put(0, 0, labels);

        for (int i = 0; i < sample_num; i++) {  // 遍历所有的正负样本，处理样本用于生成训练的库文件
            String path = "";
            if (i < imgList0.size()) {
                path = imgList0.get(i).getAbsolutePath();
            } else {
                path = imgList1.get(i - imgList0.size()).getAbsolutePath();
            }

            Mat inMat = Imgcodecs.imread(path);   // 读取样本文件
            Mat dst = getFeature(inMat);    // 获取样本文件的特征

            // 创建一个行数为sample_num, 列数为 rows*cols 的矩阵; 用于存放样本
            if (trainingDataMat == null) {
                trainingDataMat = new Mat(sample_num, dst.rows() * dst.cols(), CvType.CV_32F);
            }

            // 将样本矩阵转换成只有一行的矩阵，保存为float数组
            float[] arr = new float[dst.rows() * dst.cols()];
            int l = 0;
            for (int j = 0; j < dst.rows(); j++) { // 遍历行
                for (int k = 0; k < dst.cols(); k++) { // 遍历列
                    double[] a = dst.get(j, k);
                    arr[l] = (float) a[0];
                    l++;
                }
            }

            trainingDataMat.put(i, 0, arr); // 多张图的特征合并到一个矩阵
        }

        // Imgcodecs.imwrite(DEFAULT_PATH + "trainingDataMat.jpg", trainingDataMat);

        // 配置SVM训练器参数
        TermCriteria criteria = new TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER, 20000, 0.0001);
        SVM svm = SVM.create();
        svm.setTermCriteria(criteria); // 指定
        svm.setKernel(SVM.RBF); // 使用预先定义的内核初始化
        svm.setType(SVM.C_SVC); // SVM的类型,默认是：SVM.C_SVC
        svm.setGamma(0.1); // 核函数的参数
        svm.setNu(0.1); // SVM优化问题参数
        svm.setC(1); // SVM优化问题的参数C
        svm.setP(0.1);
        svm.setDegree(0.1);
        svm.setCoef0(0.1);

        TrainData td = TrainData.create(trainingDataMat, Ml.ROW_SAMPLE, labelsMat);// 类封装的训练数据
        boolean success = svm.train(td.getSamples(), Ml.ROW_SAMPLE, td.getResponses());// 训练统计模型
        System.out.println("svm training result: " + success);
        svm.save(MODEL_PATH);// 保存模型
    }


    public static void predict() {
        // 加载训练得到的 xml 模型文件
        SVM svm = SVM.load(MODEL_PATH);

        // 136 × 36 像素   需要跟训练的源图像文件保持相同大小
        doPredict(svm, DEFAULT_PATH + "test/A01_NMV802_0.jpg");
        doPredict(svm, DEFAULT_PATH + "test/debug_resize_1.jpg");
        doPredict(svm, DEFAULT_PATH + "test/debug_resize_2.jpg");
        doPredict(svm, DEFAULT_PATH + "test/debug_resize_3.jpg");
        doPredict(svm, DEFAULT_PATH + "test/S22_KG2187_3.jpg");
        doPredict(svm, DEFAULT_PATH + "test/S22_KG2187_5.jpg");
    }

    public static void doPredict(SVM svm, String imgPath) {
        Mat src = Imgcodecs.imread(imgPath);
        Mat dst = getFeature(src);
        // 如果训练时使用这个标识，那么符合的图像会返回9.0
        float flag = svm.predict(dst);

        if (flag == 0) {
            System.err.println(imgPath + "： 目标符合");
        }
        if (flag == 1) {
            System.out.println(imgPath + "： 目标不符合");
        }
    }

    public static int[] createLabelArray(Integer i1, Integer i2) {
        int labels[] = new int[i1 + i2];

        for (int i = 0; i < labels.length; i++) {
            if (i < i1) {
                labels[i] = 0;
            } else {
                labels[i] = 1;
            }
        }
        return labels;
    }


    public static Mat getFeature(Mat inMat) {

        Mat histogram = getHistogramFeatures(inMat);
        Mat color = getColorFeatures(inMat);

        List<Mat> list = Lists.newArrayList();
        list.add(histogram);
        list.add(color);

        Mat dst = new Mat();
        // hconcat 水平拼接 // vconcat 垂直拼接
        Core.hconcat(list, dst);
        return dst;
    }


    public static Mat getHistogramFeatures(Mat src) {
        // 将输入图像转换为灰度图像
        Mat imgGrey = new Mat();
        Imgproc.cvtColor(src, imgGrey, Imgproc.COLOR_BGR2GRAY);

        // 对灰度图像进行二值化处理
        Mat imgThreshold = new Mat();
        Imgproc.threshold(imgGrey, imgThreshold, 0, 255, Imgproc.THRESH_OTSU + Imgproc.THRESH_BINARY);

        // 计算垂直投影直方图
        float[] vhist = projectedHistogram(imgThreshold, Direction.VERTICAL);
        // 计算水平投影直方图
        float[] hhist = projectedHistogram(imgThreshold, Direction.HORIZONTAL);

        // 计算特征向量的长度，等于垂直和水平投影直方图长度之和
        int numCols = vhist.length + hhist.length;

        // 创建特征向量
        Mat features = Mat.zeros(1, numCols, CvType.CV_32F);
        int j = 0;

        // 将垂直投影直方图添加到特征向量中
        for (int i = 0; i < vhist.length; i++) {
            features.put(0, j, vhist[i]);
            j++;
        }
        // 将水平投影直方图添加到特征向量中
        for (int i = 0; i < hhist.length; i++) {
            features.put(0, j, hhist[i]);
            j++;
        }
        return features;
    }


    /**
     * 计算图像的投影直方图
     *
     * @param inMat     输入的图像矩阵
     * @param direction 投影方向，横向或纵向
     * @return 返回投影直方图数组
     */
    public static float[] projectedHistogram(Mat inMat, Direction direction) {
        // 创建一个新的矩阵 img，将输入矩阵 inMat 复制到其中。
        Mat img = new Mat();
        inMat.copyTo(img);

        // 获取矩阵 img 的行数或列数，根据 direction 指定的方向确定 sz 的值。
        int sz = img.rows();
        if (Direction.VERTICAL.equals(direction)) {
            sz = img.cols();
        }

        // 创建一个浮点数数组 nonZeroMat，用于保存投影直方图的值。
        // 在后续的计算过程中，该数组中的每个元素将分别对应图像的每一行或列的像素值之和。
        float[] nonZeroMat = new float[sz];

        // 提取矩阵 img 的 0 通道，即将非 0 像素值转换为 1.
        Core.extractChannel(img, img, 0);

        // 针对每一行或每一列，统计非零元素的个数，并将其保存到 nonZeroMat 数组中。
        for (int j = 0; j < sz; j++) {
            Mat data = Direction.HORIZONTAL.equals(direction) ? img.row(j) : img.col(j);
            int count = Core.countNonZero(data);
            nonZeroMat[j] = count;
        }

        // 归一化直方图，即将 nonZeroMat 数组中的每个值除以最大值，得到归一化后的直方图。
        float max = 1F;
        for (int j = 0; j < nonZeroMat.length; j++) {
            max = Math.max(max, nonZeroMat[j]);
        }
        for (int j = 0; j < nonZeroMat.length; j++) {
            nonZeroMat[j] /= max;
        }

        // 返回归一化后的直方图数组。
        return nonZeroMat;
    }


    /**
     * 从输入的图像中提取颜色特征
     *
     * @param src 原始图像
     * @return 颜色特征向量
     */
    public static Mat getColorFeatures(Mat src) {
        // 将图像转换为HSV色彩空间
        Mat src_hsv = new Mat();
        Imgproc.cvtColor(src, src_hsv, Imgproc.COLOR_BGR2GRAY);

        // 初始化直方图数组h
        int sz = 180;
        int[] h = new int[180];

        // 统计每个色相（hue）在输入图像中出现的次数
        for (int i = 0; i < src_hsv.rows(); i++) {
            for (int j = 0; j < src_hsv.cols(); j++) {
                int H = (int) src_hsv.get(i, j)[0];// 0-180
                // 将色相值转换为0-179的范围
                if (H > sz - 1) {
                    H = sz - 1;
                }
                if (H < 0) {
                    H = 0;
                }
                h[H]++;
            }
        }
        // 将直方图数组h转换为OpenCV中的Mat对象，以便进一步处理
        Mat features = Mat.zeros(1, sz, CvType.CV_32F);
        for (int j = 0; j < sz; j++) {
            features.put(0, j, (float) h[j]);
        }

        // 归一化直方图数组
        MinMaxLocResult m = Core.minMaxLoc(features);
        double max = m.maxVal;
        if (max > 0) {
            features.convertTo(features, -1, 1.0f / max, 0);
        }
        return features;
    }


}
