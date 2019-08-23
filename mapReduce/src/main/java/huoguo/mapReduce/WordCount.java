package huoguo.mapReduce;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class WordCount {

    public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
        private final static IntWritable count = new IntWritable(1);
        private Text key = new Text();

        @Override
        public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
            String line = value.toString();
            String[] strs = line.split(" ");
            for (String str : strs) {
                // 汉字unicode编码范围 [0x4e00,0x9fa5]
                if (!StringUtils.isEmpty(str) && str.charAt(0) >= 0x4E00 && str.charAt(0) <= 0x9FA5) {
                    collectChinese(output, str);
                } else {
                    this.key.set(str);
                    output.collect(this.key, count);
                }
            }
        }

        /**
         * 如果是汉字，则全部切分成一个字
         */
        private void collectChinese(OutputCollector<Text, IntWritable> output, String str) throws IOException {
            char[] chars = str.toCharArray();
            for (char aChar : chars) {
                this.key.set(String.valueOf(aChar));
                output.collect(this.key, count);
            }
        }
    }

    public static class Reduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
        @Override
        public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
            int sum = 0;
            while (values.hasNext()) {
                //values.next().get()获得键值对里面的值
                sum += values.next().get();
            }
            //生成键值对<key,sum>
            output.collect(key, new IntWritable(sum));
        }
    }

    public static void main(String[] args) throws Exception {
        JobConf conf = new JobConf(WordCount.class);
        conf.setJobName("wordcount");

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);

        conf.setMapperClass(Map.class);
        conf.setCombinerClass(Reduce.class);
        conf.setReducerClass(Reduce.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        String inputDir = "input";
        String outputDir = "output";
        validDir(inputDir, outputDir);
        FileInputFormat.setInputPaths(conf, new Path(inputDir));
        FileOutputFormat.setOutputPath(conf, new Path(outputDir));
        long a = System.currentTimeMillis();
        JobClient.runJob(conf);
        System.out.println("ok,spend " + (System.currentTimeMillis() - a));
    }

    /**
     * 检查目录
     */
    private static void validDir(String inputDir, String outputDir) {
        File file = new File(inputDir);
        if (!file.exists()) {
            throw new IllegalArgumentException("请在项目根目录下创建" + inputDir + "目录，并且在其中加入一些文本文件");
        }
        File file1 = new File(outputDir);
        if (file1.exists()) {
            file1.delete();
            System.out.println("已删除旧的输出目录:" + file1.getAbsolutePath());
        }
    }
}

//    Mapper中的map方法通过指定的 TextInputFormat(50行)一次处理一行。然后，它通过StringTokenizer 以空格为分隔符将一行切分为若干tokens，之后，输出< <key>, 1> 形式的键值对。
//    文件1中的内容：Hello World Bye World
//    文件2中的内容：Hello Hadoop Goodbye Hadoop
//    对于示例中的第一个输入，map输出是：
//          < Hello, 1>
//          < World, 1>
//          < Bye, 1>
//          < World, 1>
//    第二个输入，map输出是：
//          < Hello, 1>
//          < Hadoop, 1>
//          < Goodbye, 1>
//          < Hadoop, 1>
//
//    WordCount还指定了一个combiner (47行)。因此，每次map运行之后，会对输出按照key进行排序，然后把输出传递给本地的combiner（按照作业的配置与Reducer一样），进行本地聚合。
//
//    第一个map的输出是：
//          < Bye, 1>
//          < Hello, 1>
//          < World, 2>
//    第二个map的输出是：
//          < Goodbye, 1>
//          < Hadoop, 2>
//          < Hello, 1>
//    Reducer中的reduce方法仅是将每个key本例中就是单词）出现的次数求和。
//
//    因此这个作业的输出就是：
//          < Bye, 1>
//          < Goodbye, 1>
//          < Hadoop, 2>
//          < Hello, 2>
//          < World, 2>
