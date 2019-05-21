package knn;

import org.apache.commons.io.FileUtils;
import service.svm_predict;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

//客户端传过来的数据和data\knnTest.txt中文件一样；
//在临时文件放入测试数据data\temporary_testData.txt；
//结果（z）存入临时文件夹data\temporary_out_r.txt。
//传入值目前采用读data\knnTest.txt，后续给s传值就可以。
//这里训练数据和测试数据采用3行、2行交替，分别存入trainData.txt和testData.txt。
//data文件夹中所有文件名均采用语义化命名
//初始化时读取了训练文件的xy值，及训练样本数据，
public class KnnMain2 {

    public static List<String> fileXYList = new ArrayList<String>();
    public static String predict = "";
    public static Map<String, Map<String, String>> trainMap = new LinkedHashMap<String, Map<String, String>>();
    private int MACCOUNT = 576;

    /**
     * 初始化
     */
    public static void init() {
        try {
            List<String> trainList = FileUtils.readLines(new File("data/trainData.txt"), "utf-8");
            for (int i = 0; i < trainList.size(); i++) {
                Map<String, String> trainMapLine = new LinkedHashMap<String, String>();
                String[] arr2 = trainList.get(i).split(" ");
                for (int j = 1; j < arr2.length; j++) {
                    String[] arr3 = arr2[j].split(":");
                    trainMapLine.put(arr3[0], arr3[1]);
                }
                trainMap.put(i + " " + arr2[0], trainMapLine);
            }
            fileXYList = FileUtils.readLines(new File("data\\trainXY.txt"), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public KnnMain2() {
        init();
    }

    public static Map<String,String> readMsg(byte[] receiveBuf) {
        try {
            KnnMain2 knnMain2 = new KnnMain2();
            String msg = new String(receiveBuf, "utf-8");
            if (msg.length() < 5) {
                return null;
            }
            Map<String, String> map = knnMain2.getPredictiv(msg);//最终结果xyz
            System.out.println("预测值：");
            for (Map.Entry<String, String> entry : map.entrySet()) {
                System.out.println(entry.getKey() + "\t" + entry.getValue());
            }
            System.out.println("************************************");
            return map;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*xyz**/
    public Map<String, String> getPredictiv(String s) {
        Map<String, String> predictivMap = new LinkedHashMap<>();
        try {
            writeFile("data\\temporary_testData.txt", "0 " + s);
//            FileUtils.write
//
//            (new File("data\\temporary_testData.txt"),"0 "+ s,"utf-8");
            libsvmModel();
            predict = FileUtils.readFileToString(new File("data\\temporary_out_r.txt"), "utf-8").substring(0, 1);
            System.out.println("z为：" + predict);
            predictivMap.put("z", predict);
            String[] arrTestData = s.split(" ");
            Map<String, String> map = new LinkedHashMap<String, String>();
            for (String str : arrTestData) {
                String[] arr1 = str.split(":");
                map.put(arr1[0], arr1[1]);
            }
            Double endDouble = 100d;
            String lineNumber = "";
            for (Map.Entry<String, Map<String, String>> entry1 : trainMap.entrySet()) {
                if (!entry1.getKey().split(" ")[1].equals(predict)) {
                    continue;
                }
                List<Double> list = new ArrayList<Double>();
                for (int maci = 1; maci < MACCOUNT + 1; maci++) {
                    String mac1 = entry1.getValue().get(maci + "") == null ? "0" : entry1.getValue().get(maci + "");
                    String mac2 = map.get(maci + "") == null ? "0" : map.get(maci + "");
                    Double doubleLine = Math.pow(Double.valueOf(mac1) - Double.valueOf(mac2), 2);
                    list.add(doubleLine);
                }
               /* for (Map.Entry<String,String> entry2 : entry1.getValue().entrySet()){
                    String keyStr1 = entry2.getKey();
                    for(Map.Entry<String,String> entry : map.entrySet()){
                        String keyStr = entry.getKey();
                        if (keyStr1.equals(keyStr)){
                            Double doubleLine = Math.pow(Double.valueOf(entry.getValue())*100-Double.valueOf(entry2.getValue())*100, 2);
                            list.add(doubleLine);
                        }
                    }
                }*/
                Double allDouble = 0d;
                for (Double d : list) {
                    allDouble += d;
                }//循环做差，并累加；
                list.clear();
                Double doubleLine = euclidModel(allDouble);
                if (doubleLine < endDouble) {
//                    System.out.println("**");
//                    System.out.println(lineNumber);
//                    System.out.println(trainMap.size());
                    lineNumber = entry1.getKey().split(" ")[0];
                    endDouble = doubleLine;
                }
            }
            System.out.println("行号是：" + lineNumber);
            String[] arrXY = fileXYList.get(Integer.valueOf(lineNumber)).split(" ");
            predictivMap.put("x", arrXY[0]);
            predictivMap.put("y", arrXY[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return predictivMap;
    }

    public static Double euclidModel(Double allDouble) {
        Double d = Math.sqrt(allDouble);
        // System.out.println("距离为："+d);
        return d;

    }

    public static void libsvmModel() {
        try {
            String[] parg = {"data\\temporary_testData.txt", // 这个是存放测试数据
                    "data\\model_r.txt", // 调用的是训练以后的模型
                    "data\\temporary_out_r.txt"}; // 生成的结果的文件的路径
            System.out.println("........SVM运行开始..........");
            svm_predict p = new svm_predict();

            p.main(parg); // 调用
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //批量测试
    public static void writeFile(String fileName, String content) {
        FileOutputStream fop = null;
        File file;

        try {

            file = new File(fileName);
            fop = new FileOutputStream(file);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // get the content in bytes
            byte[] contentInBytes = content.getBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
