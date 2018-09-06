import com.lpshibaba.basic.StopWatch;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BasicCSVReader {
    private static int recordCount = 0;
    private static List<Double> actualMeanTempList = new ArrayList<>();

    public static void main(String[] args) throws IOException, ParseException{
        URL CSV_URL = new URL("https://raw.githubusercontent.com/fivethirtyeight/data/master/us-weather-history/KSEA.csv");
        try (
                Reader reader = new InputStreamReader(new BOMInputStream(CSV_URL.openStream()), StandardCharsets.UTF_8);
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withIgnoreHeaderCase()
                        .withTrim())
        ) {
            Comparator<String> stringLengthComparator = Comparator.comparingInt(String::length);
            PriorityQueue<String> pQueue = new PriorityQueue<>(stringLengthComparator);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            StopWatch timer = new StopWatch();
            Calendar calFirst = Calendar.getInstance();
            StringBuilder sb2 = new StringBuilder();

            for (CSVRecord csvRecord : csvParser) {
                /*
                when read a new record, recordCount will plus 1
                 */
                recordCount++;
                Calendar calConsequence = Calendar.getInstance();
                StringBuilder sb = new StringBuilder();
                String dateStr = csvRecord.get("date");
                Date dateInDate = formatter.parse(dateStr);
                final long recordNum = csvRecord.getRecordNumber();

                if (recordNum == 1) {
                    calFirst.setTime(dateInDate);
                }else{
                    calConsequence.setTime(dateInDate);
                }

                final boolean isSameYM = calFirst.get(Calendar.YEAR) == calConsequence.get(Calendar.YEAR) && calFirst.get(Calendar.MONTH) == calConsequence.get(Calendar.MONTH);

                String date = csvRecord.get("date");
                sb.append(date);
                sb.append(",");
                String actual_mean_temp = csvRecord.get("actual_mean_temp");
                actualMeanTempList.add(Double.parseDouble(actual_mean_temp));
                sb.append(actual_mean_temp);
                sb.append(",");
                String actual_min_temp = csvRecord.get("actual_min_temp");
                sb.append(actual_min_temp);
                sb.append(",");
                String actual_max_temp = csvRecord.get("actual_max_temp");
                sb.append(actual_max_temp);
                sb.append(",");
                String average_min_temp = csvRecord.get("average_min_temp");
                sb.append(average_min_temp);
                sb.append(",");
                String average_max_temp = csvRecord.get("average_max_temp");
                sb.append(average_max_temp);
                sb.append(",");
                String record_min_temp = csvRecord.get("record_min_temp");
                sb.append(record_min_temp);
                sb.append(",");
                String record_max_temp = csvRecord.get("record_max_temp");
                sb.append(record_max_temp);
                sb.append(",");
                String record_min_temp_year = csvRecord.get("record_min_temp_year");
                sb.append(record_min_temp_year);
                sb.append(",");
                String record_max_temp_year = csvRecord.get("record_max_temp_year");
                sb.append(record_max_temp_year);
                sb.append(",");
                String actual_precipitation = csvRecord.get("actual_precipitation");
                sb.append(actual_precipitation);
                sb.append(",");
                String average_precipitation = csvRecord.get("average_precipitation");
                sb.append(average_precipitation);
                sb.append(",");
                String record_precipitation = csvRecord.get("record_precipitation");
                sb.append(record_precipitation);

                /*
                    generate "longest 5 records in the dataSet" using PriorityQueue
                    custom Comparator will make PriorityQueue compare the length of record
                    PriorityQueue will auto sort all record
                    when pQueue is greater than 5, pQueue will poll the smallest record from the PriorityQueue
                 */
                pQueue.add(sb.toString());
                if (pQueue.size()>5){
                    pQueue.poll();
                }

                /*
                 generate "all records that were created in the first month of the data set's existence"
                 compare date using Calender
                 record with same year and month will append to StringBuilder sb2
                  */
                if (recordNum == 1 || isSameYM){
                    sb2.append(sb);
                    sb2.append("\n");
                }
            }
            /*
            stop the timer
             */
            double time = timer.endTime();
            System.out.printf("Totally %d records\n", recordCount);
            System.out.println("longest 5 records in the dataSet are:");
            while(!pQueue.isEmpty()){
                System.out.println(pQueue.remove());
            }
            System.out.print("all records that were created in the first month of the data set's existence are: \n");
            System.out.println(sb2);
            System.out.printf("Population standard deviation of actual_mean_temp is %.2f\n", getPopulationSD(actualMeanTempList));
            System.out.printf("Totally used %.3f second\n", time);
        }
    }

    private static double getPopulationSD(List<Double> arrList){
        double[] arr = arrList.stream().mapToDouble(d->d).toArray();
        StandardDeviation sd = new StandardDeviation();
        return sd.evaluate(arr);
    }
}