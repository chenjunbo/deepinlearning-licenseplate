package com.yoobee.licenseplate.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yoobee.licenseplate.constant.Constant;
import com.yoobee.licenseplate.entity.PlateRecoResult;
import com.yoobee.licenseplate.enumtype.Direction;
import com.yoobee.licenseplate.enumtype.PlateColor;
import com.yoobee.licenseplate.enumtype.PlateHSV;
import com.yoobee.licenseplate.train.SVMTrain;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.ANN_MLP;
import org.opencv.ml.SVM;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 车牌处理工具类 车牌切图按字符分割 字符识别
 *
 * @author jackiechan
 * @date 2024-09-18
 */
public class PlateUtil {

    private static SVM svm;
    private static ANN_MLP ann_blue;
    private static ANN_MLP ann_cn;

    static {
        //load opencv native library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //load svm model
        svm = SVM.create();
        //load ann model
        ann_blue = ANN_MLP.create();
        ann_cn = ANN_MLP.create();
        //load svm model
        loadSvmModel(Constant.DEFAULT_SVM_PATH);
        //load ann model
        loadAnnBlueModel(Constant.DEFAULT_ANN_PATH);
        loadAnnCnModel(Constant.DEFAULT_ANN_CN_PATH);
    }

    public static void loadSvmModel(String path) {
        svm.clear();
        svm = SVM.load(path);
    }

    public static void loadAnnBlueModel(String path) {
        ann_blue.clear();
        ann_blue = ANN_MLP.load(path);
    }

    public static void loadAnnCnModel(String path) {
        ann_cn.clear();
        ann_cn = ANN_MLP.load(path);
    }

    /**
     * 根据正则表达式判断字符串是否是车牌
     * 定义正则表达式模式：通过Pattern.compile(Constant.PLATE_REG)，代码将Constant.PLATE_REG字符串编译成一个Pattern对象。这个Pattern对象代表了一个正则表达式的编译表示，
     * 可以用于后续创建Matcher对象以匹配字符串。
     * <p>
     * 初始化返回值：Boolean bl = false;这行代码初始化了一个Boolean对象bl，并将其值设置为false。这表示如果输入的字符串str不匹配正则表达式，则isPlate方法将返回false。
     * <p>
     * 创建Matcher对象：通过p.matcher(str)，代码创建了一个Matcher对象m，该对象可以对输入字符串str执行正则表达式匹配操作。
     * <p>
     * 查找匹配项：使用while (m.find())循环，代码尝试在字符串str中找到与正则表达式匹配的子序列。如果找到至少一个匹配项，m.find()将返回true，并且循环体内的代码将执行。
     * <p>
     * 在循环体内，bl = true;将bl的值设置为true，表示找到了至少一个匹配项。
     * break;语句立即退出循环，因为一旦找到匹配项，就没有必要继续查找其他匹配项了。
     * 返回结果：最后，return bl;语句返回bl的值。如果字符串str与正则表达式匹配，则bl为true；否则，由于循环结束后bl的值没有被改变，它将保持为false。
     *
     * @param str
     * @return
     */
    public static Boolean isPlate(String str) {
        Pattern p = Pattern.compile(Constant.PLATE_REG);
        Boolean bl = false;
        Matcher m = p.matcher(str);
        while (m.find()) {
            bl = true;
            break;
        }
        return bl;
    }


    /**
     * 根据图片，获取可能是车牌的图块集合
     * 收一个图片路径（imagePath）、一个Vector<Mat>类型的集合（dst，可能用于存储处理结果或其他目的）、一个布尔值（debug，用于指示是否开启调试模式）、以及一个临时文件路径（tempPath），
     * 但最终并没有直接处理这些参数来寻找车牌，而是进行了一些前置准备后，调用了另一个同名的findPlateByContours方法（很可能是该类的另一个方法或者另一个类中的方法）来完成实际的检测工作。
     * <p>
     * 读取图片：首先，通过Imgcodecs.imread(imagePath)读取指定路径（imagePath）的图片，并将其存储在Mat类型的变量src中。Imgcodecs是OpenCV库中用于图像编码和解码的类。
     * <p>
     * 调整图片大小：接着，调用ImageUtil.narrow(src, 600, debug, tempPath)方法（这里假设ImageUtil是自定义的工具类）对原始图片src进行大小调整。
     * 这个方法的目的可能是为了将图片缩放到一个更小的尺寸（在这个例子中，宽度被调整为600像素），以减少后续处理步骤的计算量，从而提高效率。同时，这个方法可能还根据debug参数的值来决定是否保存调整大小后的图片到tempPath指定的临时路径下，以便于调试。
     * <p>
     * 调用另一个findPlateByContours方法：最后，这段代码并没有直接实现车牌检测的逻辑，而是调用了另一个同名的findPlateByContours方法，
     * 并将原始图片src、调整大小后的图片resized、以及之前提到的dst、debug、tempPath参数一起传递给了这个方法。这表明，真正的车牌检测逻辑是在这个被调用的方法中实现的。
     * <p>
     * 返回值：虽然这个重载版本的findPlateByContours方法没有直接返回车牌检测的结果，但根据命名和上下文，我们可以合理推测，被调用的findPlateByContours方法可能会修改dst集合，
     * 向其中添加检测到的车牌图像（以Mat对象的形式），并返回这个集合。然而，由于这个方法的具体实现没有给出，这只是一种假设。
     *
     * @param imagePath
     * @param dst
     * @param debug
     * @param tempPath
     * @return
     */
    public static Vector<Mat> findPlateByContours(String imagePath, Vector<Mat> dst, Boolean debug, String tempPath) {
        Mat src = Imgcodecs.imread(imagePath);
        final Mat resized = ImageUtil.narrow(src, 600, debug, tempPath); // 调整大小,加快后续步骤的计算效率
        return findPlateByContours(src, resized, dst, debug, tempPath);
    }

    /**
     * 根据图片，获取可能是车牌的图块集合
     *
     * @param src      输入原图
     * @param inMat    调整尺寸后的图
     * @param dst      可能是车牌的图块集合
     * @param debug    是否保留图片的处理过程
     * @param tempPath 图片处理过程的缓存目录
     */
    public static Vector<Mat> findPlateByContours(Mat src, Mat inMat, Vector<Mat> dst, Boolean debug, String tempPath) {
        // 灰度转换：首先，将输入图片inMat转换为灰度图gray。这是图像处理中常见的第一步，因为灰度图能够简化图像信息，同时保留足够的结构特征用于后续处理。
        Mat gray = new Mat();
        ImageUtil.gray(inMat, gray, debug, tempPath);

        // 高斯模糊：对灰度图gray应用高斯模糊，得到gsMat。高斯模糊是一种图像平滑技术，用于减少图像噪声和细节层次。这对于边缘检测等后续步骤是有益的，因为它可以消除小的细节，使得边缘更加突出。
        Mat gsMat = new Mat();
        ImageUtil.gaussianBlur(gray, gsMat, debug, tempPath);

        // Sobel算子：对高斯模糊后的图像gsMat应用Sobel算子，得到sobel。Sobel算子是一种用于边缘检测的算法，它能够计算图像的一阶导数，从而突出显示图像中的边缘信息。
        Mat sobel = new Mat();
        ImageUtil.sobel(gsMat, sobel, debug, tempPath);

        // 二值化：将Sobel算子处理后的图像sobel进行二值化处理，得到threshold。二值化是一种将图像转换为只有两种颜色的过程（通常是黑色和白色），这有助于进一步简化图像，使其更易于分析和处理。
        Mat threshold = new Mat();
        ImageUtil.threshold(sobel, threshold, debug, tempPath);

        // 闭操作：对二值图像threshold进行闭操作，得到morphology。闭操作是形态学操作的一种，它首先进行膨胀操作，然后进行腐蚀操作。闭操作通常用于填充图像中的小孔或小黑点，同时保持前景对象的大小不变。
        Mat morphology = threshold.clone();
        // 闭操作
        ImageUtil.morphologyClose(threshold, morphology, debug, tempPath);

        // 边缘腐蚀与膨胀：对闭操作后的图像morphology进行腐蚀和膨胀操作，以进一步细化边缘并去除噪声。这里可能进行了多次腐蚀和膨胀操作，以达到更好的效果。
        morphology = ImageUtil.erode(morphology, debug, tempPath, 4, 4);
        morphology = ImageUtil.dilate(morphology, debug, tempPath, 4, 4, true);

        // 图像放大：将二值图像morphology放大到原始图片src的尺寸。这是为了确保后续提取的图块不会因尺寸变化而失真，从而影响车牌识别的准确性。
        ImageUtil.enlarge(morphology, morphology, src.size(), debug, tempPath);

        // 轮廓检测：在放大后的二值图像上检测轮廓，得到轮廓列表contours。
        List<MatOfPoint> contours = ImageUtil.contours(src, morphology, debug, tempPath);
        // 筛选车牌图块：根据检测到的轮廓，筛选出可能是车牌的图块，并将这些图块存储在blockMat中。
        Vector<Mat> blockMat = ImageUtil.screenBlock(src, contours, false, debug, tempPath);

        // 更新结果集：调用hasPlate方法（尽管该方法的实现未给出，但我们可以假设它负责将blockMat中的车牌图块添加到dst集合中）。
        hasPlate(blockMat, dst, debug, tempPath);

        return dst;
    }

    /**
     * 通过HSV颜色空间，筛选出可能是车牌的图块
     * 个方法的主要目的是在一张图片中通过HSV（Hue, Saturation, Value）色彩空间过滤的方式来寻找可能的车牌区域，并将这些区域作为Mat对象存储在Vector<Mat>集合中返回。
     *
     * @param imagePath 图片的路径，表示要处理的图片文件的位置。
     * @param dst       一个Vector<Mat>类型的集合，用于存储检测到的车牌图像。这个方法最终会将检测到的车牌图像添加到这个集合中并返回
     * @param plateHSV  一个自定义的PlateHSV对象，可能包含了与HSV色彩空间过滤相关的阈值或其他参数，用于指定车牌在HSV色彩空间中的颜色范围。
     * @param debug     一个布尔值，用于指示是否开启调试模式。在调试模式下，可能会保存一些中间处理结果到磁盘上，以便于开发者进行问题排查。
     * @param tempPath  一个字符串，表示临时文件的保存路径。在调试模式下，一些中间结果可能会被保存到这个路径下。
     * @return
     */
    public static Vector<Mat> findPlateByHsvFilter(String imagePath, Vector<Mat> dst, PlateHSV plateHSV, Boolean debug, String tempPath) {
        //通过Imgcodecs.imread(imagePath)读取指定路径的图片，并将其存储在Mat类型的变量src中。Imgcodecs是OpenCV库中用于图像编码和解码的类。
        Mat src = Imgcodecs.imread(imagePath);
        //调用ImageUtil.narrow(src, 600, debug, tempPath)方法（假设ImageUtil是一个自定义的工具类）对原始图片src进行大小调整，将其宽度调整为600像素。这个步骤的目的是为了减少后续处理步骤的计算量，提高处理效率。同时，根据debug参数的值，可能会将调整大小后的图片保存到tempPath指定的路径下。
        final Mat resized = ImageUtil.narrow(src, 600, debug, tempPath); // 调整大小,加快后续步骤的计算效率
        //调用另一个同名的findPlateByHsvFilter方法，并将原始图片src、调整大小后的图片resized、dst集合、plateHSV对象、debug参数和tempPath路径一起传递给它。这个被调用的方法将执行实际的HSV过滤和车牌检测逻辑。
        return findPlateByHsvFilter(src, resized, dst, plateHSV, debug, tempPath);
    }

    /**
     * 通过HSV色彩空间过滤和一系列图像处理步骤来检测并提取图片中的车牌区域。
     * 该方法接收多个参数，包括原始图片src、待处理的输入图片inMat、用于存储结果的Vector<Mat>集合dst、包含HSV阈值的PlateHSV对象plateHSV、调试模式标志debug以及临时文件路径tempPath。
     *
     * @param src      输入原图
     * @param inMat    调整尺寸后的图
     * @param dst      可能是车牌的图块集合
     * @param debug    是否保留图片的处理过程
     * @param tempPath 图片处理过程的缓存目录
     * @return
     */
    public static Vector<Mat> findPlateByHsvFilter(Mat src, Mat inMat, Vector<Mat> dst, PlateHSV plateHSV, Boolean debug, String tempPath) {
        // hsv取值范围过滤
        //HSV取值范围过滤：首先，使用ImageUtil.hsvFilter方法对输入图片inMat进行HSV色彩空间过滤，根据plateHSV对象中定义的minH和maxH阈值来提取特定色调范围内的图像区域。过滤后的图像存储在hsvMat中
        Mat hsvMat = ImageUtil.hsvFilter(inMat, debug, tempPath, plateHSV.minH, plateHSV.maxH);
        // 图像均衡化
        //图像均衡化：然而，接下来的代码行似乎有一个逻辑错误或误解。它尝试将hsvMat从HSV色彩空间转换回BGR色彩空间（Imgproc.cvtColor(hsvMat, hsvMat, Imgproc.COLOR_HSV2BGR)），然后对这个BGR图像进行直方图均衡化（ImageUtil.equalizeHist）。
        // 但直方图均衡化通常用于灰度图像以增强对比度，而不是彩色图像。此外，将HSV图像转换为BGR后再进行均衡化可能不是检测车牌的最佳方法。这里可能是为了某种特定的处理需求，但通常不是HSV过滤后的标准步骤。
        Imgproc.cvtColor(hsvMat, hsvMat, Imgproc.COLOR_HSV2BGR);
        Mat equalizeMat = ImageUtil.equalizeHist(hsvMat, debug, tempPath);
        hsvMat.release();

        // 二次hsv过滤，二值化
        //二次HSV过滤和二值化：对均衡化后的图像（尽管这一步可能不是必需的或最优的）进行二次HSV阈值过滤，这次使用的是plateHSV.equalizeMinH和plateHSV.equalizeMaxH阈值，以进一步细化车牌区域。过滤后的图像通过二值化处理转换为二值图像threshold。
        Mat threshold = ImageUtil.hsvThreshold(equalizeMat, debug, tempPath, plateHSV.equalizeMinH, plateHSV.equalizeMaxH);
        Mat morphology = threshold.clone();
        // 闭操作
        //形态学闭操作：对二值图像threshold进行形态学闭操作，以填充车牌区域内部的小孔或断裂，同时保持车牌的整体形状。闭操作的结果存储在morphology中。
        ImageUtil.morphologyClose(threshold, morphology, debug, tempPath);
        threshold.release();

        Mat rgb = new Mat();
        Imgproc.cvtColor(morphology, rgb, Imgproc.COLOR_BGR2GRAY);

        // 将二值图像，resize到原图的尺寸； 如果使用缩小后的图片提取图块，可能会出现变形，影响后续识别结果
        //灰度转换和尺寸调整：将形态学处理后的图像morphology从BGR色彩空间转换为灰度图像rgb（这里变量命名可能有些误导，因为它实际上是灰度图）。然后，将灰度图像rgb调整到原始图片src的尺寸，以确保后续提取的车牌图块不会因尺寸变化而失真。
        ImageUtil.enlarge(rgb, rgb, src.size(), debug, tempPath);
        // 提取轮廓
        //轮廓检测：在调整尺寸后的灰度图像上检测轮廓，并将检测到的轮廓存储在contours列表中。
        List<MatOfPoint> contours = ImageUtil.contours(src, rgb, debug, tempPath);
        // 根据轮廓， 筛选出可能是车牌的图块 // 切图的时候， 处理绿牌，需要往上方扩展一定比例像素
        //筛选车牌图块：根据检测到的轮廓，使用ImageUtil.screenBlock方法筛选出可能是车牌的图块。如果plateHSV对象表示的是绿色车牌（通过plateHSV.equals(PlateHSV.GREEN)判断），则在切图时可能需要往上方扩展一定比例的像素，以更好地包含绿色车牌的顶部区域。
        Vector<Mat> blockMat = ImageUtil.screenBlock(src, contours, plateHSV.equals(PlateHSV.GREEN), debug, tempPath);

        // 找出可能是车牌的图块，存到dst中， 返回结果
        //存储结果：调用hasPlate方法（尽管其实现未给出，但可以假设它负责将筛选出的车牌图块添加到dst集合中）。最后，返回包含检测到的车牌图像的dst集合。
        hasPlate(blockMat, dst, debug, tempPath);
        return dst;
    }

    /**
     * 输入车牌切图集合，判断是否包含车牌
     * 从一组Mat对象（图像）中筛选出那些被认为是车牌的图像，并将这些图像添加到另一个Vector<Mat>集合中。该方法还提供了调试和保存中间结果的选项
     *
     * @param inMat    输入参数，包含了一系列待检测的Mat对象（图像）。
     * @param dst      包含车牌的图块,输出参数，用于存储被识别为车牌的Mat`对象。
     * @param debug    一个布尔值，指示是否开启调试模式。
     * @param tempPath 一个字符串，指定了临时文件的保存路径，用于在调试模式下保存图像。
     */
    public static void hasPlate(Vector<Mat> inMat, Vector<Mat> dst, Boolean debug, String tempPath) {
        //遍历输入图像：通过for (Mat src : inMat)循环遍历inMat集合中的每一个Mat对象（图像），每次循环中，当前图像被赋值给变量src。
        for (Mat src : inMat) {
            //尺寸检查：在将图像添加到结果集合之前，首先检查图像的尺寸是否符合预期（即是否已经被调整到特定的宽度和高度，
            // 这里用Constant.DEFAULT_HEIGHT和Constant.DEFAULT_WIDTH表示）。这是为了确保只有经过预处理并符合特定尺寸要求的图像才会被进一步处理。
            if (src.rows() == Constant.DEFAULT_HEIGHT && src.cols() == Constant.DEFAULT_WIDTH) { // 尺寸限制; 已经结果resize了，此处判断一下
                //特征提取：如果图像的尺寸符合要求，则调用SVMTrain.getFeature(src)方法从图像中提取特征。这个方法的具体实现没有给出，但可以假设它返回了一个能够代表图像特征的数据结构（在OpenCV中，这通常是一个Mat对象）。
                Mat samples = SVMTrain.getFeature(src);
                //SVM预测：使用训练好的支持向量机（SVM）模型（svm）对提取的特征进行预测，以判断该图像是否包含车牌。预测结果是一个浮点数flag，这里假设当flag等于0时，表示图像中包含车牌。
                float flag = svm.predict(samples);
                if (flag == 0) { // 目标符合
                    //添加结果：如果图像被识别为车牌（即flag == 0），则将其添加到输出集合dst中。
                    dst.add(src);
                    //如果开启了调试模式（debug为true），则调用ImageUtil.debugImg方法将识别为车牌的图像保存到指定的临时路径下，并可能附带一些调试信息（如文件名"platePredict"）。
                    ImageUtil.debugImg(true, tempPath, "platePredict", src);
                }
            }
        }
    }

    /**
     * 判断车牌切图颜色
     * 根据输入的图片（inMat）来判断车牌的颜色，并返回一个PlateColor枚举值来表示车牌的颜色
     *
     * @param inMat          一个Mat类型的图片
     * @param adaptive_minsv
     * @param debug
     * @param tempPath       临时文件路径tempPath
     * @return
     */
    public static PlateColor getPlateColor(Mat inMat, Boolean adaptive_minsv, Boolean debug, String tempPath) {
        // 判断阈值,定义了一个阈值，用于比较颜色匹配的结果。
        final float thresh = 0.5f;
        // 转到HSV空间，对H均衡化之后的结果
        Mat hsvMat = ImageUtil.equalizeHist(inMat, debug, tempPath);

        if (colorMatch(hsvMat, PlateColor.GREEN, adaptive_minsv, debug, tempPath) > thresh) {
            return PlateColor.GREEN;
        }
        if (colorMatch(hsvMat, PlateColor.YELLOW, adaptive_minsv, debug, tempPath) > thresh) {
            return PlateColor.YELLOW;
        }
        if (colorMatch(hsvMat, PlateColor.BLUE, adaptive_minsv, debug, tempPath) > thresh) {
            return PlateColor.BLUE;
        }
        return PlateColor.BLUE;
    }

    /**
     * 颜色匹配计算
     * 在给定的HSV色彩空间图像hsvMat中，根据特定的颜色范围（由PlateColor对象r定义）和可选的自适应饱和度阈值调整（由adaptive_minsv布尔值控制）
     * ，计算并返回匹配该颜色范围的像素点占总像素点的比例。这个方法可能用于车牌识别或类似应用中，以识别具有特定颜色的车牌区域。
     *
     * @param hsvMat
     * @param r
     * @param adaptive_minsv
     * @param debug
     * @param tempPath
     * @return
     */
    public static Float colorMatch(Mat hsvMat, PlateColor r, Boolean adaptive_minsv, Boolean debug, String tempPath) {
        //HSV色彩空间中饱和度和亮度（Value）的最大值，这里是255。
        final float max_sv = 255;
        //参考的最小饱和度阈值，用于自适应调整。
        final float minref_sv = 64;
        //绝对的最小饱和度阈值，当不使用自适应调整时使用。
        final float minabs_sv = 95;
        //图像的总像素点数，通过图像的宽乘以高得到。
        Integer countTotal = hsvMat.rows() * hsvMat.cols();
        //匹配到指定颜色范围的像素点数，初始化为0。
        Integer countMatched = 0;

        // 匹配模板基色,切换以查找想要的基色
        int min_h = r.minH;
        int max_h = r.maxH;
        //根据PlateColor对象r中的minH和maxH属性，确定要匹配的颜色在HSV色彩空间中的色调（Hue）范围。
        float diff_h = (float) ((max_h - min_h) / 2);
        //计算色调范围的平均值avg_h和一半范围diff_h，用于后续计算。
        int avg_h = (int) (min_h + diff_h);

        for (int i = 0; i < hsvMat.rows(); i++) {
            /*
            使用两个嵌套的for循环遍历图像的每一行和每一列。
            对于每个像素点，从hsvMat中获取其色调（H）、饱和度（S）和亮度（V）值。
             */
            for (int j = 0; j < hsvMat.cols(); j++) {
                int H = (int) hsvMat.get(i, j)[0];
                int S = (int) hsvMat.get(i, j)[1];
                int V = (int) hsvMat.get(i, j)[2];

                boolean colorMatched = false;
                //检查当前像素点的色调值是否在指定的范围内（min_h < H && H <= max_h）。
                if (min_h < H && H <= max_h) {
                    //如果是，则根据色调值到平均值avg_h的距离（Hdiff），以及是否启用自适应饱和度阈值调整（adaptive_minsv），计算当前的最小饱和度阈值min_sv。
                    int Hdiff = Math.abs(H - avg_h);
                    float Hdiff_p = Hdiff / diff_h;
                    float min_sv = 0;
                    if (adaptive_minsv) {
                        min_sv = minref_sv - minref_sv / 2 * (1 - Hdiff_p);
                    } else {
                        min_sv = minabs_sv;
                    }
                    //如果当前像素点的饱和度（S）和亮度（V）值都大于最小阈值min_sv且小于等于最大值max_sv，则认为该像素点的颜色与指定范围匹配，将colorMatched设置为true。
                    if ((min_sv < S && S <= max_sv) && (min_sv < V && V <= max_sv)) {
                        colorMatched = true;
                    }
                }
                //如果当前像素点的颜色与指定范围匹配，则增加countMatched的值。
                if (colorMatched) {
                    countMatched++;
                }
            }
        }
        //将匹配到的像素点数countMatched除以总像素点数countTotal，并乘以1.0F（确保结果为浮点数），然后返回这个比例值。这个比例值表示了图像中匹配指定颜色范围的像素点所占的比例。
        return countMatched * 1F / countTotal;
    }

    /**
     * 车牌切图，分割成单个字符切图
     *
     * @param inMat    输入原始图像
     * @param color   车牌颜色
     * @param debug
     * @param tempPath 临时文件存储路径，用于保存调试图像。
     */
    public static String charsSegment(Mat inMat, PlateColor color, Boolean debug, String tempPath) {
        // 车牌字符个数
        int charCount = 7;

        // 切换到灰度图
        Mat gray = new Mat();
        Imgproc.cvtColor(inMat, gray, Imgproc.COLOR_BGR2GRAY);
        //使用高斯模糊减少噪声。
        ImageUtil.gaussianBlur(gray, gray, debug, tempPath);

        // 图像进行二值化 // 图像二值化阈值选取,应用Otsu阈值方法进行二值化处理，使图像更加清晰。
        Mat threshold = new Mat();
        Imgproc.threshold(gray, threshold, 10, 255, Imgproc.THRESH_OTSU + Imgproc.THRESH_BINARY);
        /* 输出二值图 */
        ImageUtil.debugImg(debug, tempPath, "plateThreshold", threshold);

        // 边缘腐蚀,使用腐蚀操作（erode）来消除小对象（噪声）和细化边缘。
        threshold = ImageUtil.erode(threshold, debug, tempPath, 2, 2);

        // 垂直方向投影，错切校正,通过垂直投影和错切校正来调整车牌的角度。 // 理论上，还可以用于分割字符
        Integer px = getShearPx(threshold);
        ImageUtil.shearCorrection(threshold, threshold, px, debug, tempPath);

        // 前面已经结果错切校正了，可以按照垂直、水平方向投影进行精确定位
        // 垂直投影 + 垂直分割线，分割字符 // 水平投影，去掉上下边框、铆钉干扰
        threshold = sepAndClear(threshold, px, charCount, debug, tempPath);

        // 边缘膨胀 // 还原腐蚀操作产生的影响 // 会影响中文字符的精确度
        threshold = ImageUtil.dilate(threshold, debug, tempPath, 2, 2, true);

        // 提取外部轮廓
        List<MatOfPoint> contours = Lists.newArrayList();

        Imgproc.findContours(threshold, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
        // 字符轮廓集合
        Vector<Rect> charRect = new Vector<Rect>();

        Mat dst = null;
        if (debug) {
            dst = inMat.clone();
            Imgproc.cvtColor(threshold, dst, Imgproc.COLOR_GRAY2BGR);
        }
        // 遍历轮廓
        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint contour = contours.get(i);
            // 得到包覆此轮廓的最小正矩形
            Rect mr = Imgproc.boundingRect(contour);
            // 验证尺寸，主要验证高度是否满足要求，去掉不符合规格的字符，中文字符后续处理
            if (checkCharSizes(mr)) {
                charRect.add(mr);
                if (debug) {
                    ImageUtil.drawRectangle(dst, mr);
                    ImageUtil.debugImg(debug, tempPath, "boundingRect", dst);
                }
            }
        }
        // 未识别到字符
        if (null == charRect || charRect.size() <= 0) {
            return null;
        }
        // 字符个数不足，要按照分割的区域补齐 // 同时处理中文字符
        // System.out.println("字符个数===>" + charRect.size());

        // 遍历轮廓，修正超高超宽的字符，去掉铆钉的干扰, 并排序
        Vector<Rect> sorted = new Vector<Rect>();
        sortRect(charRect, sorted);

        // 定位省份字母位置
        Integer posi = getSpecificRect(sorted, color);
        Integer prev = posi - 1 <= 0 ? 0 : posi - 1;

        // 定位中文字符 // 中文字符可能不是连续的轮廓，需要特殊处理
        Rect chineseRect = getChineseRect(sorted.get(posi), sorted.get(prev));

        Mat chineseMat = new Mat(threshold, chineseRect);
        chineseMat = preprocessChar(chineseMat);
        ImageUtil.debugImg(debug, tempPath, "chineseMat", chineseMat);

        // 识别字符，计算置信度
        List<PlateRecoResult> result = Lists.newArrayList();

        // 一般来说，中文字符预测比较不准确，所以参考业界的做法，设置默认所在的省份字符，如果置信度较低， 则默认该字符
        PlateRecoResult chinese = new PlateRecoResult();
        chinese.setSort(0);
        chinese.setRect(chineseRect);
        // 预测中文字符
        predictChinese(chineseMat, chinese);
        result.add(chinese);
        charCount--;
        // 预测中文之外的字符
        for (int i = posi; i < sorted.size() && charCount > 0; i++, charCount--) {
            Mat img_crop = new Mat(threshold, sorted.get(i));
            img_crop = preprocessChar(img_crop);
            PlateRecoResult chars = new PlateRecoResult();
            chars.setSort(i + 1);
            chars.setRect(chineseRect);
            // 预测数字、字符
            predict(img_crop, color, chars);
            result.add(chars);
            ImageUtil.debugImg(debug, tempPath, "charMat", img_crop);
        }
        // 车牌识别结果
        String plate = "";
        // 置信度
        Double fonfidence = 0.0D;
        for (PlateRecoResult p : result) {
            plate += p.getChars();
            fonfidence += p.getConfi();
        }
        System.out.println(plate + "===>" + fonfidence);
        return plate;
    }

    /**
     * 垂直方向投影，错切校正
     *
     * @param threshold
     * @param px
     * @param charCount
     * @param debug
     * @param tempPath
     */
    public static Mat sepAndClear(Mat threshold, Integer px, Integer charCount, Boolean debug, String tempPath) {
        Mat dst = threshold.clone();
        Set<Integer> rows = Sets.newHashSet();
        int ignore = 10;
        // 水平方向投影 // 按rows清除干扰 // 去掉上下边框干扰像素
        // 垂直方向投影; 按cols清楚干扰; 意义不大; 直接分割，提取字符更简单
        for (int i = 0; i < threshold.rows(); i++) {
            int count = Core.countNonZero(threshold.row(i));
            if (count <= 15) {
                rows.add(i);
                if (i < ignore) {
                    for (int j = 0; j < i; j++) {
                        rows.add(j);
                    }
                }
                if (i > threshold.rows() - ignore) {
                    for (int j = i + 1; j < threshold.rows(); j++) {
                        rows.add(j);
                    }
                }
            }
        }

        Integer minY = 0;
        for (int i = 0; i < threshold.rows(); i++) {
            if (rows.contains(i)) {
                if (i <= threshold.rows() / 2) {
                    minY = i;
                }
                for (int j = 0; j < threshold.cols(); j++) {
                    dst.put(i, j, 0);
                }
            }
        }

        threshold.release();
        ImageUtil.debugImg(debug, tempPath, "sepAndClear", dst);

        // 分割字符，返回所有字符的边框，Rect(x, y, width, height)
        // 在这里提取，估计比轮廓提取方式更准确，尤其在提取中文字符方面
        //这里可能要注释掉
        Integer height = dst.rows() - rows.size();
        Integer y = minY + 1;   // 修正一个像素
        Integer x = 0;
        Integer width = 0;
        Boolean bl = false; // 是否是全0的列
        Vector<Rect> rects = new Vector<Rect>();    // 提取到的轮廓集合，可用于后续的字符识别，也可用于去除垂直方向的干扰
        for (int i = 0; i < dst.cols(); i++) {
            int count = Core.countNonZero(dst.col(i));
            if(count <= 0) { // 黑色的列; 因为前面就行了边缘腐蚀，这里选择全黑色的列作为分割
                bl = true;
            } else {
                if(bl) {
                    x = i;
                }
                bl =false;
                width++;
            }
            if(bl && width > 0) {   // 切割图块
                Rect r = new Rect(x, y, width, height); // 提取到的轮廓
                // 按轮廓切图
                Mat img_crop = new Mat(dst, r);
                ImageUtil.debugImg(debug, tempPath, "sepAndClear-crop", img_crop);
                rects.add(r);
                width = 0;
            }
        }
        return dst;
    }

    /**
     * 基于字符垂直方向投影，计算错切值，用于错切校正 上下均去掉6个像素；
     * 去掉上下边框的干扰 最大处理25px的错切校；
     * 左右去掉25像素，保证每列都能取到完整的数据 相当于实现任意角度的投影计算；
     *
     * @param threshold 二值图像， 0黑色 255白色； 136 * 36
     * @return 错切像素值
     */
    public static Integer getShearPx(Mat threshold) {
        int px = 25; // 最大处理25像素的错切
        int ignore = 6; // 去掉上下边框干扰像素

        int maxCount = 0; // 取count值最大
        int minPx = px; // 取绝对值最小
        Integer result = 0;

        for (int i = -px; i <= px; i++) {
            int colCount = 0; // 计数满足条件的列
            // 计算按像素值倾斜的投影
            for (int j = px; j < threshold.cols() - px; j++) {
                int cellCount = 0; // 计数每列为0的值
                float c = i * 1F / threshold.rows();
                for (int k = ignore; k < threshold.rows() - ignore; k++) {
                    double d = threshold.get(k, Math.round(j + k * c))[0];
                    if (d <= 10) {
                        cellCount++;
                    }
                }
                if (cellCount >= 24) {
                    colCount++;
                }
            }
            // System.out.println(i + "===>" + minPx + "===>" + colCount + "===>" + maxCount);
            if (colCount == maxCount) {
                if (Math.abs(i) <= minPx) {
                    minPx = Math.abs(i);
                    result = i;
                }
            }
            if (colCount > maxCount) {
                maxCount = colCount;
                minPx = Math.abs(i);
                result = i;
            }
        }
        System.err.println("错切校正像素值===>" + result);
        return result;
    }

    /**
     * 根据字符的外接矩形倾斜角度计算错切值，
     * 用于错切校正 提取最小正矩形的时候，同时提取最小外接斜矩形；
     * 计算错切像素值 在切割字符之后，进行预测之前，对每个字符进行校正
     *
     * @param angleRect 字符最小外接矩形 集合
     * @return
     */
    public static Integer getShearPx(Vector<RotatedRect> angleRect) {
        Integer posCount = 0;
        Integer negCount = 0;
        Float posAngle = 0F;
        Float negAngle = 0F;
        for (RotatedRect r : angleRect) {
            if (Math.abs(r.angle) >= 45) { // 向右倾斜 需要向左校正
                posCount++;
                posAngle = posAngle + 90F + (float) r.angle;
            } else {
                negCount++;
                negAngle = negAngle - (float) r.angle;
            }
        }
        Integer px = 0;
        if (posCount > negCount) {
            px = -posAngle.intValue() / posCount / 2;
        } else {
            px = negAngle.intValue() / negCount / 2;
        }
        if (Math.abs(px) > 10) {
            px = px % 10;
        }
        return px;
    }

    /**
     * 预测数字、字母 字符
     *
     * @param img
     * @return
     */
    public static void predict(Mat img, PlateColor color, PlateRecoResult chars) {
        Mat f = PlateUtil.features(img, Constant.PREDICT_SIZE);

        int index = 0;
        Double maxVal = -2D;
        Mat output = new Mat(1, Constant.STR_CHARACTERS.length, CvType.CV_32F);
        if (color.equals(PlateColor.GREEN)) {
//            ann_green.predict(f, output); // 预测结果
        } else {
            ann_blue.predict(f, output); // 预测结果
        }
        for (int j = 0; j < Constant.STR_CHARACTERS.length; j++) {
            double val = output.get(0, j)[0];
            if (val > maxVal) {
                maxVal = val;
                index = j;
            }
        }
        String result = String.valueOf(Constant.STR_CHARACTERS[index]);
        chars.setChars(result);
        chars.setConfi(maxVal);
    }

    /**
     * 预测中文字符
     *
     * @param img
     * @return
     */
    public static void predictChinese(Mat img, PlateRecoResult chinese) {
        Mat f = PlateUtil.features(img, Constant.PREDICT_SIZE);
        int index = 0;
        Double maxVal = -2D;

        Mat output = new Mat(1, Constant.STR_CHINESE.length, CvType.CV_32F);
        ann_cn.predict(f, output); // 预测结果
        for (int j = 0; j < Constant.STR_CHINESE.length; j++) {
            double val = output.get(0, j)[0];
            if (val > maxVal) {
                maxVal = val;
                index = j;
                System.err.println(index);
            }
        }
        String result = Constant.STR_CHINESE[index];
        chinese.setChars(Constant.KEY_CHINESE_MAP.get(result));
        chinese.setConfi(maxVal);
    }

    /**
     * 找出指示城市的字符的Rect，
     * 例如 苏A7003X，就是A的位置
     * 之所以选择城市的字符位置，是因为该位置不管什么字母，占用的宽度跟高度的差不多，而且字符笔画是连续的，能大大提高位置的准确性
     *
     * @param vecRect
     * @return
     */
    public static Integer getSpecificRect(Vector<Rect> vecRect, PlateColor color) {
        List<Integer> xpositions = Lists.newArrayList();

        int maxHeight = 0;
        int maxWidth = 0;
        for (int i = 0; i < vecRect.size(); i++) {
            xpositions.add(vecRect.get(i).x);
            if (vecRect.get(i).height > maxHeight) {
                maxHeight = vecRect.get(i).height;
            }
            if (vecRect.get(i).width > maxWidth) {
                maxWidth = vecRect.get(i).width;
            }
        }
        int specIndex = 0;
        for (int i = 0; i < vecRect.size(); i++) {
            Rect mr = vecRect.get(i);
            int midx = mr.x + mr.width / 2;

            if (PlateColor.GREEN.equals(color)) {
                if ((mr.width > maxWidth * 0.8 || mr.height > maxHeight * 0.8) && (midx < Constant.DEFAULT_WIDTH * 2 / 8 && midx > Constant.DEFAULT_WIDTH / 8)) {
                    specIndex = i;
                }
            } else {
                // 如果一个字符有一定的大小，并且在整个车牌的1/7到2/7之间，则是我们要找的特殊车牌
                if ((mr.width > maxWidth * 0.8 || mr.height > maxHeight * 0.8) && (midx < Constant.DEFAULT_WIDTH * 2 / 7 && midx > Constant.DEFAULT_WIDTH / 7)) {
                    specIndex = i;
                }
            }
        }
        return specIndex;
    }

    /**
     * 根据特殊车牌来构造猜测中文字符的位置和大小
     *
     * @param rectSpe
     * @return
     */
    public static Rect getChineseRect(Rect rectSpe, Rect rectPrev) {
        int height = rectSpe.height;
        float newwidth = rectSpe.width * 1.15f;
        int x = rectSpe.x;
        int y = rectSpe.y;

        // 判断省份字符前面的位置，是否有宽度符合要求的中文字符
        if (rectPrev.width >= rectSpe.width && rectPrev.x <= rectSpe.x - rectSpe.width) {
            return rectPrev;
        }
        // 如果没有，则按照车牌尺寸来切割
        int newx = x - (int) (newwidth * 1.15);
        newx = Math.max(newx, 0);
        Rect a = new Rect(newx, y, (int) newwidth, height);
        return a;
    }

    /**
     * 字符预处理: 统一每个字符的大小
     *
     * @param in
     * @return
     */
    final static int CHAR_SIZE = 20;

    private static Mat preprocessChar(Mat in) {
        int h = in.rows();
        int w = in.cols();
        // 生成输出对角矩阵(2, 3)
        // 1 0 0
        // 0 1 0
        Mat transformMat = Mat.eye(2, 3, CvType.CV_32F);
        int m = Math.max(w, h);
        transformMat.put(0, 2, (m - w) / 2f);
        transformMat.put(1, 2, (m - h) / 2f);

        Mat warpImage = new Mat(m, m, in.type());
        Imgproc.warpAffine(in, warpImage, transformMat, warpImage.size(), Imgproc.INTER_LINEAR, Core.BORDER_CONSTANT, new Scalar(0));
        Mat resized = new Mat(CHAR_SIZE, CHAR_SIZE, CvType.CV_8UC3);
        Imgproc.resize(warpImage, resized, resized.size(), 0, 0, Imgproc.INTER_CUBIC);
        return resized;
    }

    /**
     * 字符尺寸验证；
     * 去掉尺寸不符合的图块 此处计算宽高比意义不大，因为字符 1 的宽高比干扰就已经很大了
     *
     * @param r
     * @return
     */
    public static Boolean checkCharSizes(Rect r) {
        float minHeight = 15f;
        float maxHeight = 35f;
        double charAspect = r.size().width / r.size().height;
        return charAspect < 1 && minHeight <= r.size().height && r.size().height < maxHeight;
    }

    /**
     * 将Rect按位置从左到右进行排序 遍历轮廓，修正超高的字符，去掉铆钉的干扰, 并排序
     *
     * @param vecRect
     * @param out
     * @return
     */
    public static void sortRect(Vector<Rect> vecRect, Vector<Rect> out) {
        Map<Integer, Integer> map = Maps.newHashMap();
        Integer avgY = 0; // 所有字符的平均起点Y值 // 小于平均值的，削脑袋
        Integer avgHeight = 0; // 所有字符的平均身高 //大于平均值的，剁脚
        Integer avgWidth = 0; // 计算所有大于8像素(去掉【1】字符的干扰)轮廓的均值 // 大于平均值的，进行瘦身操作
        Integer wCount = 0;

        for (int i = 0; i < vecRect.size(); ++i) {
            map.put(vecRect.get(i).x, vecRect.indexOf(vecRect.get(i)));
            avgY += vecRect.get(i).y;
            avgHeight += vecRect.get(i).height;
            if (vecRect.get(i).width >= 10) {
                wCount++;
                avgWidth += vecRect.get(i).width;
            }
        }
        avgY = avgY / vecRect.size();
        avgHeight = avgHeight / vecRect.size();
        avgWidth = avgWidth / wCount;
        Set<Integer> set = map.keySet();
        Object[] arr = set.toArray();
        Arrays.sort(arr);
        for (Object key : arr) {
            Rect r = vecRect.get(map.get(key));
            if (Math.abs(avgY - r.y) >= 2 || Math.abs(r.height - avgHeight) >= 2) {
                r = new Rect(r.x, avgY - 1, r.width, avgHeight); // 身材超高或者超矮的，修正一下
            }
            if (r.width > avgWidth) { // 轮廓偏宽
                r = new Rect(r.x, r.y, avgWidth, r.height); // 瘦身
            }
            out.add(r);
        }
    }

    public static float[] projectedHistogram(final Mat img, Direction direction) {
        int sz = img.rows();
        if (direction.equals(Direction.VERTICAL)) {
            sz = img.cols();
        }

        // 统计这一行或一列中，非零元素的个数，并保存到nonZeroMat中
        float[] nonZeroMat = new float[sz];
        Core.extractChannel(img, img, 0);
        for (int j = 0; j < sz; j++) {
            Mat data = direction.equals(Direction.VERTICAL) ? img.row(j) : img.col(j);
            int count = Core.countNonZero(data);
            nonZeroMat[j] = count;
        }
        float max = 0;
        for (int j = 0; j < nonZeroMat.length; ++j) {
            max = Math.max(max, nonZeroMat[j]);
        }
        if (max > 0) {
            for (int j = 0; j < nonZeroMat.length; ++j) {
                nonZeroMat[j] /= max;
            }
        }
        return nonZeroMat;
    }

    /**
     * 计算投影直方图：
     * <p>
     * 使用 projectedHistogram 方法（该方法在代码段中未给出，可能是自定义或来自其他部分的代码）计算输入图像 in 的垂直（vhist）和水平（hhist）投影直方图。
     * 这些直方图通常以浮点数数组的形式返回，分别表示图像在垂直和水平方向上的像素分布。
     * 图像缩放：
     * <p>
     * 如果 sizeData 大于0，则使用 Imgproc.resize 方法将原始图像 in 缩放到 sizeData x sizeData 的尺寸，并将结果存储在 lowData 变量中。
     * 这一步可能是为了降低后续处理的计算量或提取图像的某种低分辨率特征。
     * 计算特征向量长度：
     * <p>
     * 接下来，计算输出 Mat 对象 out 的列数（即特征向量的长度）。这个长度是垂直和水平投影直方图的长度之和，加上缩放后图像 lowData 的像素总数。
     * 初始化输出 Mat 对象：
     * <p>
     * 使用计算出的列数和一行的高度（因为特征向量是一维的），以及 CvType.CV_32F（表示32位浮点数）来初始化 out 变量。
     * 填充特征向量：
     * <p>
     * 通过遍历垂直和水平投影直方图的每个元素，以及缩放后图像的每个像素，将它们的值依次放入 out 的相应位置。这里，j 变量用于跟踪当前要填充的 out 中的位置。
     * 返回结果：
     * <p>
     * 最后，返回包含所有计算出的特征（垂直和水平投影直方图的值以及缩放后图像的像素值）的 out 变量。
     *
     * @param in
     * @param sizeData
     * @return
     */
    public static Mat features(Mat in, int sizeData) {
        float[] vhist = projectedHistogram(in, Direction.VERTICAL);
        float[] hhist = projectedHistogram(in, Direction.HORIZONTAL);
        Mat lowData = new Mat();
        if (sizeData > 0) {
            Imgproc.resize(in, lowData, new Size(sizeData, sizeData));
        }
        int numCols = vhist.length + hhist.length + lowData.cols() * lowData.rows();
        Mat out = new Mat(1, numCols, CvType.CV_32F);

        int j = 0;
        for (int i = 0; i < vhist.length; ++i, ++j) {
            out.put(0, j, vhist[i]);
        }
        for (int i = 0; i < hhist.length; ++i, ++j) {
            out.put(0, j, hhist[i]);
        }
        for (int x = 0; x < lowData.cols(); x++) {
            for (int y = 0; y < lowData.rows(); y++, ++j) {
                double[] val = lowData.get(x, y);
                out.put(0, j, val[0]);
            }
        }
        return out;
    }

    /**
     * 从给定的图像路径imagePath中识别车牌，并返回一个包含识别结果的Vector<Mat>集合。这里使用了异步编程（通过CompletableFuture）来并行处理两种不同的车牌识别方法：
     * 一种是通过轮廓检测（findPlateByContours），另一种是通过HSV颜色过滤（findPlateByHsvFilter）
     * 根据图片，获取可能是车牌的图块集合 多种方法实现：
     * 1、网上常见的轮廓提取车牌算法
     * 2、hsv色彩分割算法
     *
     * @param imagePath 输入原图
     * @param dst       可能是车牌的图块集合
     * @param debug     是否保留图片的处理过程
     * @param tempPath  图片处理过程的缓存目录
     */
    public static Vector<Mat> getPlateMat(String imagePath, Vector<Mat> dst, Boolean debug, String tempPath) {
        //读取图像：首先，使用Imgcodecs.imread(imagePath)从给定的路径读取图像，并将其存储在Mat对象src中。
        Mat src = Imgcodecs.imread(imagePath);
        // 图像调整大小：为了提高后续处理的效率，调用ImageUtil.narrow(src, 600, debug, tempPath)方法将图像的大小调整为最大宽度为600像素（高度按比例缩放）。调整后的图像存储在resized变量中。加快后续步骤的计算效率
        final Mat resized = ImageUtil.narrow(src, 600, debug, tempPath);
        System.out.println("Source image size: " + src.size());
        System.out.println("Resized image size: " + resized.size());
        /*
        使用CompletableFuture.supplyAsync(() -> ...)创建两个异步任务，分别调用findPlateByContours和findPlateByHsvFilter方法来识别车牌。这两个方法都接受原始图像src、调整大小后的图像resized、目标Vector<Mat>集合dst（用于存储结果）、调试标志debug和临时文件路径tempPath作为参数。findPlateByHsvFilter方法还额外接受一个PlateHSV.BLUE参数，这可能指定了用于HSV过滤的颜色范围。
         */
        CompletableFuture<Vector<Mat>> f1 = CompletableFuture.supplyAsync(() -> {
            Vector<Mat> r = findPlateByContours(src, resized, dst, debug, tempPath);
            return r;
        });
        CompletableFuture<Vector<Mat>> f2 = CompletableFuture.supplyAsync(() -> {
            Vector<Mat> r = findPlateByHsvFilter(src, resized, dst, PlateHSV.BLUE, debug, tempPath);
            return r;
        });

        // 这里的 join() 将阻塞，直到所有的任务执行结束
        //CompletableFuture.allOf(f1, f2).join();语句会阻塞当前线程，直到f1和f2两个异步任务都完成。CompletableFuture.allOf方法返回一个CompletableFuture<Void>，其join()方法会等待所有给定的CompletableFuture实例完成。
        CompletableFuture.allOf(f1, f2).join();
        try {
            /*
            使用f1.get()和f2.get()分别获取两个异步任务的结果。这两个结果都是Vector<Mat>类型，代表从图像中识别出的车牌区域。
            使用addAll方法将f2的结果添加到f1的结果中，合并两个方法找到的所有车牌区域。
            返回合并后的结果。
             */
            Vector<Mat> result = f1.get();
            result.addAll(f2.get());
            return result;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        Instant start = Instant.now();

        String tempPath = Constant.DEFAULT_TEMP_DIR;
        String filename = Constant.DEFAULT_DIR + "test/11.jpg";
        File f = new File(filename);
        if (!f.exists()) {
            File f1 = new File(filename.replace("jpg", "png"));
            File f2 = new File(filename.replace("png", "bmp"));
            filename = f1.exists() ? f1.getPath() : f2.getPath();
        }

        Boolean debug = true;
        Vector<Mat> dst = new Vector<Mat>();
        // 提取车牌图块
        // getPlateMat(filename, dst, debug, tempPath);
        // findPlateByContours(filename, dst, debug, tempPath);
        // findPlateByHsvFilter(filename, dst, PlateHSV.BLUE, debug, tempPath);
        findPlateByHsvFilter(filename, dst, PlateHSV.GREEN, debug, tempPath);

        Set<String> result = Sets.newHashSet();
        dst.stream().forEach(inMat -> {
            // 识别车牌颜色
            PlateColor color = PlateUtil.getPlateColor(inMat, true, debug, tempPath);
            // 识别车牌字符
            String plateNo = PlateUtil.charsSegment(inMat, color, debug, tempPath);
            result.add(plateNo + "\t" + color.desc);
        });
        System.out.println(result.toString());

        Instant end = Instant.now();
        System.err.println("总耗时：" + Duration.between(start, end).toMillis());
    }

}
