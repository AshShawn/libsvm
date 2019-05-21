package cn;

import java.io.IOException;
import service.svm_predict;
import service.svm_train;

public class comMain {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
    //trainModel();//训练

    predictModel();//预测
    }
    //训练
    public static void trainModel() throws IOException {
        String[] arg = { "data\\trainData.txt", // 存放SVM训练模型用的数据的路径
                "data\\model_r.txt" }; // 存放SVM通过训练数据训/ //练出来的模型的路径
        // 创建一个训练对象
        svm_train t = new svm_train();
        t.main(arg); // 调用
    }
    //预测
    public static void predictModel()throws IOException{
        String[] parg = { "data\\testData.txt", // 这个是存放测试数据
                "data\\model_r.txt", // 调用的是训练以后的模型
                "data\\out_r.txt" }; // 生成的结果的文件的路径
        System.out.println("........SVM运行开始..........");

        // 创建一个预测或者分类的对象
        svm_predict p = new svm_predict();

        p.main(parg); // 调用
    }
}