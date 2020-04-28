import java.io.*;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Search {
    private static int allTimes = 0;
    private static int errTimes = 0;
    private static OutputStream outPutStream;

    public static void main(String[] args) throws IOException {

//        String dirPath = "C:\\Users\\59603\\Desktop\\log2(1)\\log2\\";
        String dirPath = "C:\\Users\\59603\\Documents\\WeChat Files\\sjh596030631\\FileStorage\\File\\2020-04\\归档 (2)\\";

        try {
            outPutStream = new FileOutputStream(dirPath+"log.txt");
        } catch (FileNotFoundException e) {
            System.out.println("文件打开异常");
            e.printStackTrace();
        }
        File dir = new File(dirPath);
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files == null) {
                return;
            }

            for (int i = 0; i < files.length; i++) {
                File f = files[i];

                if (f.isDirectory()) {
                    File aa = new File(dirPath+f.getName());
                    // 父级文件目录，获取机器号码
                    String ru = "[0-9]{10}"; // 10位机器号
                    Pattern pattern = Pattern.compile(ru);
                    Matcher matcher = pattern.matcher(f.getName());
                    if (matcher.find()) {
                        System.out.printf("分析第%s个  机器号：%s   ",i,matcher.group(0));
                        outPutStream.write(("第"+(i+1)+"台机器\t机器号："+matcher.group(0)+"\t").getBytes());
                    } else {
                        System.out.print("未发现编号！    ");
                        outPutStream.write("错误*****************************未发现编号************************************".getBytes());
                    }

                    File[] bb = aa.listFiles();
                    if (bb == null) {
                        return;
                    }
                    for (File c : bb) {

                        // 父级文件目录，获取日期
                        String ddd = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
                        Pattern aaa = Pattern.compile(ddd);
                        Matcher vvv = aaa.matcher(f.getName());

                        if (vvv.find()) {
                            System.out.print("日期："+vvv.group(0)+"    ");
                            outPutStream.write(("日期："+vvv.group(0)+"    ").getBytes());

                        } else {
                            System.out.println("日期匹配错误！");
                            outPutStream.write("错误*****************************日期匹配错误************************************".getBytes());

                        }

                        try {
                            inputStream(dirPath+f.getName()+"\\"+c.getName());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        outPutStream.write("\r\n".getBytes());
                        outPutStream.write("\r\n".getBytes());
                    }
                }
            }
        } else {
            System.out.println("非目录！");
        }
        // 计算总差异率
        BigDecimal all = new BigDecimal(allTimes);
        BigDecimal err = new BigDecimal(errTimes);

        if (!BigDecimal.ZERO.equals(all)) {
            System.out.println();
            System.out.print("总数:"+ all);
            System.out.print("       错误："+ err);
            BigDecimal d = err.divide(all, 4, BigDecimal.ROUND_DOWN);
            BigDecimal f = d.multiply(new BigDecimal("100"));
            System.out.println("     异常占比：" + f.toString() + " %");
        }
        outPutStream.flush();
        outPutStream.close();
    }

    public static void inputStream(String filepath) throws IOException {
        FileReader fr = new FileReader(filepath);
        BufferedReader bf = new BufferedReader(fr);
        int allNum=0;
        int rightNum=0;
        int errNum=0;
        String str;
        String lastTimeHour = "";

        while((str = bf.readLine()) != null){
            if (str.contains("7DEFEEFE") || str.contains("7defeefe")) { // 取7D指令
                allNum ++;
                String reg = "7DEFEEFE10[0-3]"; // 匹配正常的光感日志
                Pattern pattern = Pattern.compile(reg,Pattern.CASE_INSENSITIVE); //忽略大小写
                Matcher matcher = pattern.matcher(str);

                // 匹配发生时间 00:00:03:049
                String timeRU = "[0-9]{2}:[0-9]{2}:[0-9]{2}:[0-9]{3}"; // 匹配正常的光感日志
                Pattern pppp = Pattern.compile(timeRU);
                Matcher mmmm = pppp.matcher(str);
                if (mmmm.find()) {
                    if (!lastTimeHour.equals(mmmm.group(0).substring(0,2))) {
                        lastTimeHour = mmmm.group(0).substring(0,2);
                        System.out.println();

                        System.out.print(lastTimeHour);
                        outPutStream.write("\r\n".getBytes());
                        outPutStream.write(lastTimeHour.getBytes());
                    }
                }

                if (matcher.find()) { // 正常
                    System.out.print("+");
                    outPutStream.write('+');
                    rightNum ++;
                } else { // 异常
                    errNum ++;
                    System.out.print("-");
                    outPutStream.write('-');
                }
            }
        }

        allTimes += allNum;
        errTimes += errNum;
        BigDecimal b = new BigDecimal(allNum);
        BigDecimal c = new BigDecimal(errNum);
        if (!BigDecimal.ZERO.equals(b)) {
            System.out.print("       总数:"+ allNum);
            System.out.print("       正确:"+ rightNum);
            System.out.print("       错误："+ errNum);
            BigDecimal d = c.divide(b, 4, BigDecimal.ROUND_DOWN);
            BigDecimal f = d.multiply(new BigDecimal("100"));
            System.out.println("     异常占比：" + f.toString() + " %");
        }
        bf.close();
        fr.close();
    }
}
